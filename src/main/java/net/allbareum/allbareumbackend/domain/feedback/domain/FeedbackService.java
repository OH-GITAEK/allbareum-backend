package net.allbareum.allbareumbackend.domain.feedback.domain;

import lombok.RequiredArgsConstructor;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.FeedbackCreateRequestDto;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.FeedbackResponseDto;
import net.allbareum.allbareumbackend.domain.feedback.infrastructure.FeedbackRepository;
import net.allbareum.allbareumbackend.domain.user.domain.User;
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

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final RestTemplate restTemplate;

    @Value("${ml.server.url}")
    private String mlServerUrl;

    public FeedbackResponseDto create(User user, FeedbackCreateRequestDto feedbackCreateRequestDto) throws IOException {
        System.out.println("FeedbackService 도착");
        // 1. 음성 파일 바이트 배열로 변환
        byte[] audioBytes = feedbackCreateRequestDto.getAudioFile().getBytes();
        System.out.println("FeedbackService 1");

        // 2. HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"audio\"; filename=\"audio.wav\"");

        // 3. HTTP 요청 바디 구성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("audio", new ByteArrayResource(audioBytes) {
            @Override
            public String getFilename() {
                return "audio.wav"; // 임시 파일 이름 설정
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
        Map<String, Object> feedbackData = (Map<String, Object>) responseBody.get("feedback_data");
        System.out.println("FeedbackService 7");

        List<Integer> incorrectWordIndices = (List<Integer>) feedbackData.get("incorrect_word_indices");
        Double accuracyScore = Double.valueOf(feedbackData.get("accuracy").toString());
        String speechFeedback = (String) feedbackData.get("speech_feedback");
        String frequencyFeedback = (String) feedbackData.get("frequency_feedback");

        System.out.println("FeedbackService 8");

        // 이미지 정보 추출
        Map<String, Object> oralImageData = (Map<String, Object>) responseBody.get("oral_structure_image");
        Map<String, Object> frequencyImageData = (Map<String, Object>) responseBody.get("frequency_analysis_image");

        String oralStructureImagePath = (String) oralImageData.get("path");
        String frequencyAnalysisImagePath = (String) frequencyImageData.get("path");


        System.out.println("FeedbackService 9");
        Feedback feedback = Feedback.builder()
                .textSentence(feedbackCreateRequestDto.getTextSentence())
                .incorrectWordIndices(incorrectWordIndices)
                .accuracyScore(accuracyScore)
                .speechFeedback(speechFeedback)
                .frequencyFeedback(frequencyFeedback)
                .user(user)
                .build();

        feedbackRepository.save(feedback);
        // DTO로 반환
        System.out.println("FeedbackService 끝");
        return new FeedbackResponseDto(
                feedback,
                oralStructureImagePath,
                frequencyAnalysisImagePath
        );
    }
}
