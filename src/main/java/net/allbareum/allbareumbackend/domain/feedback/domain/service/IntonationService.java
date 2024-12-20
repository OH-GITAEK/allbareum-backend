package net.allbareum.allbareumbackend.domain.feedback.domain.service;

import lombok.RequiredArgsConstructor;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.FeedbackCreateRequestDto;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.IntonationFeedbackResponseDto;
import net.allbareum.allbareumbackend.domain.feedback.domain.Intonation;
import net.allbareum.allbareumbackend.domain.feedback.infrastructure.IntonationRepository;
import net.allbareum.allbareumbackend.domain.feedback.infrastructure.PronunciationRepository;
import net.allbareum.allbareumbackend.domain.user.domain.User;
import net.allbareum.allbareumbackend.global.exception.CustomException;
import net.allbareum.allbareumbackend.global.exception.ErrorCode;
import net.allbareum.allbareumbackend.global.service.S3Service;
import net.allbareum.allbareumbackend.global.util.MultipartResponseParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class IntonationService {
    private final IntonationRepository intonationRepository;
    private final RestTemplate restTemplate;
    private final S3Service s3Service;

    @Value("${ml.server.url}")
    private String mlServerUrl;


    public IntonationFeedbackResponseDto createIntonation(User user, FeedbackCreateRequestDto feedbackCreateRequestDto) throws IOException, ExecutionException, InterruptedException {
        System.out.println("FeedbackService 도착");

        // 1. 음성 파일 바이트 배열로 변환
        byte[] audioBytes = feedbackCreateRequestDto.getAudioFile().getBytes();
        String originalFileName = feedbackCreateRequestDto.getAudioFile().getOriginalFilename();
        s3Service.upload(feedbackCreateRequestDto.getAudioFile(), "images/audio-intonation");
        // 2. HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 3. HTTP 요청 바디 구성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("audio", new ByteArrayResource(audioBytes) {
            @Override
            public String getFilename() {
                return "audio_input.3gpp"; // 임시 파일 이름 설정
            }
        });
        String sentenceCode = feedbackCreateRequestDto.getTextSentence().replace("\"", "");
        System.out.println("Processed sentence_code: " + sentenceCode);
        body.add("sentence_code", sentenceCode);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        System.out.println(requestEntity);

        // 4. ML 서버로 요청 전송
        ResponseEntity<byte[]> response = null;
        try {
            response = restTemplate.postForEntity(mlServerUrl + "/get-intonation-feedback", requestEntity, byte[].class);
        } catch (HttpClientErrorException e) {
            handleHttpError(e);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.ML_SERVER_RESPONSE_IS_EMPTY);
        }

        // 5. 응답 본문 길이 출력
        byte[] responseBody = response.getBody();
        if (responseBody == null) {
            throw new IOException("ML 서버로부터 빈 응답을 받았습니다.");
        }
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Response Headers: " + response.getHeaders());
        System.out.println("Response Body Length: " + responseBody.length);

        // 6. multipart 응답 파싱
        String boundary = Objects.requireNonNull(response.getHeaders().getContentType()).getParameter("boundary");
        MultipartResponseParser parser = new MultipartResponseParser(responseBody, boundary);

        // 각 파트 파싱
        int status = Integer.parseInt(parser.getTextPart("status"));
        String intonationFeedbacks = parser.getTextPart("feedback_text");
        double intonationScore = Double.parseDouble(parser.getTextPart("intonation_score"));
        System.out.println("Status: " + status);
        System.out.println("Intonation Feedbacks: " + intonationFeedbacks);
        System.out.println("Intonation Score: " + intonationScore);

        // 이미지 파일 파싱
        MultipartFile intonationFeedbackImage = parser.getFilePart("feedback_image");
        String feedbackImageUrls = "https://allbareum.s3.ap-northeast-2.amazonaws.com/images/100point.png";
        if (intonationFeedbackImage == null) {
            System.out.println("Feedback image is null or not provided");
        } else {
            feedbackImageUrls = s3Service.upload(intonationFeedbackImage, "images/intonation");
            System.out.println("Intonation Feedback Image URL: " + feedbackImageUrls);
        }
        String intonationFeedbackImageUrl = "https://allbareum.s3.ap-northeast-2.amazonaws.com/images/image/" + sentenceCode + ".jpg";

        // 디버그 로그

        System.out.println("Intonation Feedback Image URL: " + intonationFeedbackImageUrl);

        // Feedback 객체 생성
        Intonation intonation = Intonation.builder()
                .sentenceCode(sentenceCode)
                .status(status)
                .intonationFeedbacks(intonationFeedbacks)
                .feedbackImageUrls(feedbackImageUrls)
                .intonationImageUrls(intonationFeedbackImageUrl)
                .intonationScore(intonationScore)
                .user(user)
                .build();

        intonationRepository.save(intonation);
        System.out.println("FeedbackService 끝");
        return new IntonationFeedbackResponseDto(intonation);
    }


    private void handleHttpError(HttpClientErrorException e) {
        int statusCode = e.getStatusCode().value();
        if (statusCode == 422) {
            throw new CustomException(ErrorCode.VOICE_NOT_DETECTED);
        } else if (statusCode == 423) {
            throw new CustomException(ErrorCode.VOICE_INCORRECT_PHRASE);
        } else if (statusCode == 501) {
            throw new CustomException(ErrorCode.ML_SERVER_NOT_IMPLEMENTED);
        } else {
            throw new CustomException(ErrorCode.ML_SERVER_RESPONSE_IS_EMPTY);
        }
    }
}
