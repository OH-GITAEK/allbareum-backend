package net.allbareum.allbareumbackend.domain.feedback.domain.service;

import lombok.RequiredArgsConstructor;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.PronunciationFeedbackCreateRequestDto;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.PronunciationFeedbackResponseDto;
import net.allbareum.allbareumbackend.domain.feedback.domain.Pronunciation;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class PronunciationService {
    private final FeedbackRepository feedbackRepository;
    private final FeedbackAsyncService feedbackAsyncService;
    private final RestTemplate restTemplate;
    private final S3Service s3Service;

    @Value("${ml.server.url}")
    private String mlServerUrl;

    public PronunciationFeedbackResponseDto createPronunciation(User user, PronunciationFeedbackCreateRequestDto pronunciationFeedbackCreateRequestDto) throws IOException, ExecutionException, InterruptedException {
        // 비동기 호출
        CompletableFuture<ResponseEntity<Map>> pronunciationFeedbackFuture =
                feedbackAsyncService.getPronunciationFeedback(pronunciationFeedbackCreateRequestDto);

        CompletableFuture<ResponseEntity<Map>> pronouncedTextFuture =
                feedbackAsyncService.getPronouncedText(pronunciationFeedbackCreateRequestDto.getTextSentence());

        // 두 비동기 작업이 모두 완료될 때까지 기다림
        CompletableFuture.allOf(pronunciationFeedbackFuture, pronouncedTextFuture).join();


        // 응답 처리
        Map<String, Object> feedbackResponseBody = pronunciationFeedbackFuture.get().getBody();
        Map<String, Object> textResponseBody = pronouncedTextFuture.get().getBody();

        // 필드 값 추출
        int status = (int) feedbackResponseBody.get("status");
        String pronounced_text = (String) textResponseBody.get("pronounced_text");
        String transcription = (String) feedbackResponseBody.get("transcription");
        int feedbackCount = (int) feedbackResponseBody.getOrDefault("feedback_count", 1);
        List<Integer> wordIndex = (List<Integer>) feedbackResponseBody.getOrDefault("word_indexes", List.of());
        List<String> pronunciationFeedbacks = (List<String>) feedbackResponseBody.getOrDefault("pronunciation_feedbacks", List.of());
        List<String> wrongSpellings = (List<String>) feedbackResponseBody.getOrDefault("wrong_spellings", List.of());
        Double pronunciationScore = Double.valueOf(feedbackResponseBody.getOrDefault("accuracy", "0").toString());

        // 이미지 정보 추출
        List<String> feedbackImages = (List<String>) feedbackResponseBody.get("feedback_image_names");
        System.out.println("feedbackImages: " + feedbackImages);
        List<String> feedbackImageUrls = feedbackImages != null
                ? feedbackImages.stream()
                .map(imageData -> "https://allbareum.s3.ap-northeast-2.amazonaws.com/images/feedback/" + imageData)
                .toList()
                : List.of();


        // Pronunciation 엔티티 빌드
        Pronunciation pronunciation = Pronunciation.builder()
                .user(user)
                .pronounced_text(pronounced_text)
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
