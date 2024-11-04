package net.allbareum.allbareumbackend.domain.feedback.domain;

import lombok.RequiredArgsConstructor;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.FeedbackCreateRequestDto;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.FeedbackResponseDto;
import net.allbareum.allbareumbackend.domain.feedback.infrastructure.FeedbackRepository;
import net.allbareum.allbareumbackend.domain.user.domain.User;
import net.allbareum.allbareumbackend.global.service.S3Service;
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
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
        System.out.println("FeedbackService 1");

        // 2. HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"audio\"; filename=\"" + originalFileName + "\"");

        // 3. HTTP 요청 바디 구성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("audio", new ByteArrayResource(audioBytes) {
            @Override
            public String getFilename() {
                return originalFileName; // 임시 파일 이름 설정
            }
        });
        System.out.println("FeedbackService 3");

        body.add("text", feedbackCreateRequestDto.getTextSentence());
        System.out.println("FeedbackService 4");

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        System.out.println(requestEntity);

        // 4. ML 서버로 요청 전송
        ResponseEntity<Map> response = restTemplate.postForEntity(
                mlServerUrl + "/get-feedback", requestEntity, Map.class);
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody());

        System.out.println("FeedbackService 6");

        // 5. 응답 처리
        Map<String, Object> responseBody = response.getBody();
        // 'feedback_data' 내부의 값 추출
   
//        Map<String, Object> pronunciation_feedback_data = (Map<String, Object>) responseBody.get("pronunciation_feedback");
//        Map<String, Object> intonation_feedback_data = (Map<String, Object>) responseBody.get("intonation_feedback");
        System.out.println("FeedbackService 7");

        String transcription = (String)  responseBody.get("transcription");
        String pronunciation_feedback = (String)  responseBody.get("pronunciation_feedback");
        Double pronunciation_score = Double.valueOf(responseBody.get("pronunciation_score").toString());
        String intonation_feedback = (String)  responseBody.get("intonation_feedback");

        System.out.println("FeedbackService 8");

        // 이미지 데이터를 바이너리로 받아와서 MultipartFile로 변환
        MultipartFile pronunciation_feedback_image = null;
        if (responseBody.get("pronunciation_feedback_image") instanceof byte[] pronunciationImageBytes) {
            pronunciation_feedback_image = new MockMultipartFile("pronunciation_feedback_image", "pronunciation_feedback_image.png", "image/png", pronunciationImageBytes);
        }

        MultipartFile intonation_feedback_image = null;
        if (responseBody.get("intonation_feedback_image") instanceof byte[] intonationImageBytes) {
            intonation_feedback_image = new MockMultipartFile("intonation_feedback_image", "intonation_feedback_image.png", "image/png", intonationImageBytes);
        }

        String pronunciation_feedback_image_url = s3Service.upload(pronunciation_feedback_image,"images");
        String intonation_feedback_image_url = s3Service.upload(intonation_feedback_image,"images");

        System.out.println("FeedbackService 9");
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
        // DTO로 반환
        System.out.println("FeedbackService 끝");
        return new FeedbackResponseDto(feedback);
    }
}
