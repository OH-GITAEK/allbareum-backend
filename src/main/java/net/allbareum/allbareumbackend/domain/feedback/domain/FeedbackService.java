package net.allbareum.allbareumbackend.domain.feedback.domain;

import lombok.RequiredArgsConstructor;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.FeedbackCreateRequestDto;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.FeedbackResponseDto;
import net.allbareum.allbareumbackend.domain.feedback.infrastructure.FeedbackRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final RestTemplate restTemplate;
    private final S3Service s3Service;

    @Value("${ml.server.url}")
    private String mlServerUrl;

    public FeedbackResponseDto create(User user, FeedbackCreateRequestDto feedbackCreateRequestDto) throws IOException {
        // 1. 음성 파일 바이트 배열로 변환
        byte[] audioBytes = feedbackCreateRequestDto.getAudioFile().getBytes();
        String originalFileName = feedbackCreateRequestDto.getAudioFile().getOriginalFilename();

        // 2. HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 3. HTTP 요청 바디 구성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("audio", new ByteArrayResource(audioBytes) {
            @Override
            public String getFilename() {
                return originalFileName; // 임시 파일 이름 설정
            }
        });
        body.add("text", feedbackCreateRequestDto.getTextSentence());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        try {
            // 4. ML 서버로 요청 전송
            ResponseEntity<byte[]> response = restTemplate.postForEntity(mlServerUrl + "/get-feedback", requestEntity, byte[].class);

            // 5. 응답 본문 길이 출력
            byte[] responseBody = response.getBody();
            if (responseBody == null) {
                throw new IOException("ML 서버로부터 빈 응답을 받았습니다.");
            }

            // 6. multipart 응답 파싱
            String boundary = Objects.requireNonNull(response.getHeaders().getContentType()).getParameter("boundary");
            MultipartResponseParser parser = new MultipartResponseParser(responseBody, boundary);

            // 각 파트 파싱 확인
            String transcription = parser.getTextPart("transcription");
            String pronunciation_feedback = parser.getTextPart("pronunciation_feedback");
            Double pronunciation_score = Double.parseDouble(parser.getTextPart("pronunciation_score"));
            String intonation_feedback = parser.getTextPart("intonation_feedback");

            MultipartFile pronunciation_feedback_image = parser.getFilePart("pronunciation_feedback_image");
            MultipartFile intonation_feedback_image = parser.getFilePart("intonation_feedback_image");

            // 이미지 URL 업로드
            String pronunciation_feedback_image_url = "";
            String intonation_feedback_image_url = "";
            int status = Integer.parseInt(parser.getTextPart("status"));

            if(status == 1 || status == 5){
                pronunciation_feedback_image_url = "https://allbareum.s3.ap-northeast-2.amazonaws.com/images/100point.png";
                intonation_feedback_image_url = "https://allbareum.s3.ap-northeast-2.amazonaws.com/images/100point.png";
            }
            else{
                pronunciation_feedback_image_url = s3Service.upload(pronunciation_feedback_image, "images");
                intonation_feedback_image_url = s3Service.upload(intonation_feedback_image, "images");
            }

            // Feedback 객체 생성
            Feedback feedback = Feedback.builder()
                    .textSentence(feedbackCreateRequestDto.getTextSentence())
                    .transcription(transcription)
                    .pronunciation_feedback(pronunciation_feedback)
                    .pronunciation_score(pronunciation_score)
                    .intonation_feedback(intonation_feedback)
                    .pronunciation_feedback_image(pronunciation_feedback_image_url)
                    .intonation_feedback_image(intonation_feedback_image_url)
                    .user(user)
                    .build();

            feedbackRepository.save(feedback);
            return new FeedbackResponseDto(feedback);

        } catch (HttpClientErrorException e) {
            // 에러 상태 코드 확인
            if (e.getStatusCode().value() == 422) {
                throw new CustomException(ErrorCode.VOICE_NOT_DETECTED);
            } else if (e.getStatusCode().value() == 423) {
                throw new CustomException(ErrorCode.VOICE_INCORRECT_PHRASE);
            } else {
                throw e; // 다른 에러는 그대로 던지기
            }
        }
    }
}
