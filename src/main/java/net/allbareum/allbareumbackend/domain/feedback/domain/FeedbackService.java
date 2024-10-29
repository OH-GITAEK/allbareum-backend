package net.allbareum.allbareumbackend.domain.feedback.domain;

import lombok.RequiredArgsConstructor;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.FeedbackCreateRequestDto;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.FeedbackResponse;
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

import java.io.File;
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

    public FeedbackResponse create(User user, FeedbackCreateRequestDto feedbackCreateRequestDto) throws IOException {
        // 1. 음성 파일 바이트 배열로 변환
        byte[] audioBytes = feedbackCreateRequestDto.getAudioFile().getBytes();

        // 2. HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 3. HTTP 요청 바디 구성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("audio", new ByteArrayResource(audioBytes) {
            @Override
            public String getFilename() {
                return "audio.3gp"; // 임시 파일 이름 설정
            }
        });
        body.add("text", feedbackCreateRequestDto.getTextSentence());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 4. ML 서버로 요청 전송
        ResponseEntity<Map> response = restTemplate.postForEntity(
                mlServerUrl + "/get-feedback", requestEntity, Map.class);

        // 5. 응답 처리
        Map<String, Object> responseBody = response.getBody();

        List<Integer> incorrectWordIndices = (List<Integer>) responseBody.get("incorrect_word_indices");
        Double accuracyScore = Double.valueOf(responseBody.get("accuracy").toString());
        String speechFeedback = (String) responseBody.get("speech_feedback");
        String frequencyFeedback = (String) responseBody.get("frequency_feedback");

        // 이미지 데이터(Base64 인코딩) 처리
        String oralStructureImageBase64 = (String) responseBody.get("oral_structure_image");
        String frequencyAnalysisImageBase64 = (String) responseBody.get("frequency_analysis_image");

        Feedback feedback = Feedback.builder()
                .textSentence(feedbackCreateRequestDto.getTextSentence())
                .incorrectWordIndices(incorrectWordIndices)
                .accuracyScore(accuracyScore)
                .speechFeedback(speechFeedback)
                .frequencyFeedback(frequencyFeedback)
                .user(user)
                .build();
        // DTO로 반환
        return new FeedbackResponse(
                feedback,
                oralStructureImageBase64,
                frequencyAnalysisImageBase64
        );
    }
}
