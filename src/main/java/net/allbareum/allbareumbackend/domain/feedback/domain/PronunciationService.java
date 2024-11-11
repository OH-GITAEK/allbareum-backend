package net.allbareum.allbareumbackend.domain.feedback.domain;

import lombok.RequiredArgsConstructor;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.PronunciationFeedbackCreateRequestDto;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.PronunciationFeedbackResponseDto;
import net.allbareum.allbareumbackend.domain.feedback.infrastructure.FeedbackRepository;
import net.allbareum.allbareumbackend.domain.user.domain.User;
import net.allbareum.allbareumbackend.global.exception.CustomException;
import net.allbareum.allbareumbackend.global.exception.ErrorCode;
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

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PronunciationService {
    private final FeedbackRepository feedbackRepository;
    private final RestTemplate restTemplate;
    private final S3Service s3Service;

    @Value("${ml.server.url}")
    private String mlServerUrl;

    public PronunciationFeedbackResponseDto createPronunciation(User user, PronunciationFeedbackCreateRequestDto pronunciationFeedbackCreateRequestDto) throws IOException {
        // 1. 음성 파일 바이트 배열로 변환
        byte[] audioBytes = pronunciationFeedbackCreateRequestDto.getAudioFile().getBytes();
        String originalFileName = pronunciationFeedbackCreateRequestDto.getAudioFile().getOriginalFilename();

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

        body.add("text", pronunciationFeedbackCreateRequestDto.getTextSentence());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        System.out.println(requestEntity);

        // 4. ML 서버로 요청 전송
        ResponseEntity<Map> response = restTemplate.postForEntity(
                mlServerUrl + "/get-pronunciation-feedback", requestEntity, Map.class);

        // 5. 응답 처리
        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null) {
            throw new CustomException(ErrorCode.ML_SERVER_RESPONSE_IS_EMPTY);
        }
        // 필드 값 추출
        int status = (int) responseBody.get("status");
        String transcription = (String) responseBody.get("transcription");
        int feedbackCount = (int) responseBody.getOrDefault("feedback_count", 1);
        List<Integer> wordIndex = (List<Integer>) responseBody.getOrDefault("word_indexes", List.of());
        List<String> pronunciationFeedbacks = (List<String>) responseBody.getOrDefault("pronunciation_feedbacks", List.of());
        List<String> wrongSpellings = (List<String>) responseBody.getOrDefault("wrong_spellings", List.of());
        Double pronunciationScore = Double.valueOf(responseBody.getOrDefault("accuracy", "0").toString());

        // 이미지 정보 추출
        List<String> feedbackImages = (List<String>) responseBody.get("feedback_image_names");
        System.out.println("feedbackImages: " + feedbackImages);
        List<String> feedbackImageUrls = feedbackImages != null
                ? feedbackImages.stream()
                .map(imageData -> "https://allbareum.s3.ap-northeast-2.amazonaws.com/images/feedback/" + imageData)
                .toList()
                : List.of();


        // Pronunciation 엔티티 빌드
        Pronunciation pronunciation = Pronunciation.builder()
                .user(user)
                .textSentence(pronunciationFeedbackCreateRequestDto.getTextSentence())
                .status(status)
                .transcription(transcription)
                .feedbackCount(feedbackCount)
                .wordIndex(wordIndex)
                .pronunciationFeedbacks(pronunciationFeedbacks)
                .feedbackImageUrls(feedbackImageUrls)
                .wrongSpellings(wrongSpellings)
                .pronunciationScore(pronunciationScore)
                .build();

        feedbackRepository.save(pronunciation);
        // DTO로 반환
        return new PronunciationFeedbackResponseDto(pronunciation);
    }
}
