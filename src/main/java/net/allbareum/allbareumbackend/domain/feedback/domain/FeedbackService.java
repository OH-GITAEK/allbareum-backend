package net.allbareum.allbareumbackend.domain.feedback.domain;

import lombok.RequiredArgsConstructor;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.FeedbackCreateRequestDto;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.FeedbackResponseDto;
import net.allbareum.allbareumbackend.domain.feedback.infrastructure.FeedbackRepository;
import net.allbareum.allbareumbackend.domain.user.domain.User;
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
        System.out.println("FeedbackService 도착");

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
        System.out.println(requestEntity);

        // 4. ML 서버로 요청 전송
        ResponseEntity<byte[]> response = restTemplate.postForEntity(mlServerUrl + "/get-feedback", requestEntity, byte[].class);

        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Response Headers: " + response.getHeaders());

        // 5. 응답 본문 길이 출력
        byte[] responseBody = response.getBody();
        if (responseBody == null) {
            throw new IOException("ML 서버로부터 빈 응답을 받았습니다.");
        }
        System.out.println("Response Body Length: " + responseBody.length);

        // 6. multipart 응답 파싱
        String boundary = Objects.requireNonNull(response.getHeaders().getContentType()).getParameter("boundary");
        MultipartResponseParser parser = new MultipartResponseParser(responseBody, boundary);

        // 각 파트 파싱 확인
        String transcription = parser.getTextPart("transcription");
        System.out.println("Transcription: " + transcription);

        String pronunciation_feedback = parser.getTextPart("pronunciation_feedback");
        System.out.println("Pronunciation Feedback: " + pronunciation_feedback);

        Double pronunciation_score = Double.parseDouble(parser.getTextPart("pronunciation_score"));
        System.out.println("Pronunciation Score: " + pronunciation_score);

        String intonation_feedback = parser.getTextPart("intonation_feedback");
        System.out.println("Intonation Feedback: " + intonation_feedback);

        MultipartFile pronunciation_feedback_image = parser.getFilePart("pronunciation_feedback_image");
        MultipartFile intonation_feedback_image = parser.getFilePart("intonation_feedback_image");

        // 이미지 URL 업로드
        String pronunciation_feedback_image_url = s3Service.upload(pronunciation_feedback_image, "images");
        String intonation_feedback_image_url = s3Service.upload(intonation_feedback_image, "images");
        System.out.println("pronunciation_feedback_image_url : " + pronunciation_feedback_image_url);
        System.out.println("intonation_feedback_image_url : " + intonation_feedback_image_url);

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
        System.out.println("FeedbackService 끝");
        return new FeedbackResponseDto(feedback);
    }
}
