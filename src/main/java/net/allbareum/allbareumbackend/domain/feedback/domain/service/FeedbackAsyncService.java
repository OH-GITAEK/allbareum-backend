package net.allbareum.allbareumbackend.domain.feedback.domain.service;

import lombok.RequiredArgsConstructor;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.PronunciationFeedbackCreateRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class FeedbackAsyncService {

    private final RestTemplate restTemplate;

    @Value("${ml.server.url}")
    private String mlServerUrl;

    @Async("taskExecutor")  // AsyncConfig에서 정의한 스레드 풀 사용
    public CompletableFuture<ResponseEntity<Map>> getPronunciationFeedback(PronunciationFeedbackCreateRequestDto pronunciationFeedbackCreateRequestDto) {
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

        return CompletableFuture.completedFuture(response);
    }

    @Async("taskExecutor")  // AsyncConfig에서 정의한 스레드 풀 사용
    public CompletableFuture<ResponseEntity<Map>> getPronouncedText(String textSentence) {
        HttpEntity<String> requestEntity = new HttpEntity<>(textSentence);
        ResponseEntity<Map> response = restTemplate.postForEntity("http://ml.server.url/get-pronounced-text", requestEntity, Map.class);
        return CompletableFuture.completedFuture(response);
    }
}


