package net.allbareum.allbareumbackend.domain.feedback.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.FeedbackCreateRequestDto;
import net.allbareum.allbareumbackend.global.exception.CustomException;
import net.allbareum.allbareumbackend.global.exception.ErrorCode;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class FeedbackAsyncService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${ml.server.url}")
    private String mlServerUrl;

    @Async("taskExecutor")  // AsyncConfig에서 정의한 스레드 풀 사용
    public CompletableFuture<ResponseEntity<Map>> getPronunciationFeedback(FeedbackCreateRequestDto feedbackCreateRequestDto) throws IOException {
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

        // 4. ML 서버로 요청 전송
        ResponseEntity<Map> response;
        try {
            System.out.println(requestEntity);
            response = restTemplate.postForEntity(
                    mlServerUrl + "/get-pronunciation-feedback", requestEntity, Map.class);
            System.out.println(response);
            return CompletableFuture.completedFuture(response);
        } catch (HttpClientErrorException e) {
            // 에러 상태 코드 확인
            if (e.getStatusCode().value() == 422) {
                System.out.println("1");
                throw new CustomException(ErrorCode.VOICE_NOT_DETECTED);
            } else if (e.getStatusCode().value() == 423) {
                System.out.println("11");
                System.out.println(e.getStatusCode().value());
                throw new CustomException(ErrorCode.VOICE_INCORRECT_PHRASE);
            } else if (e.getStatusCode().value() == 501) {
                System.out.println("111");
                throw new CustomException(ErrorCode.ML_SERVER_NOT_IMPLEMENTED);
            } else {
                System.out.println("111111");
                System.out.println(e.getStatusCode().value());
                throw new CustomException(ErrorCode.ML_SERVER_RESPONSE_IS_EMPTY);// 다른 에러는 그대로 던지기
            }
        }
    }

    @Async("taskExecutor")  // AsyncConfig에서 정의한 스레드 풀 사용
    public CompletableFuture<ResponseEntity<Map>> getPronouncedText(String textSentence) throws JsonProcessingException {
        // 1. 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 2. 요청 바디를 Map으로 설정한 후 JSON으로 변환
        Map<String, String> requestBodyMap = new HashMap<>();
        requestBodyMap.put("hangul", textSentence);
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);

        // 3. HttpEntity에 JSON 문자열과 헤더를 포함
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // 4. 요청 전송
        ResponseEntity<Map> response;
        try {System.out.println(requestEntity);
            response = restTemplate.postForEntity(
                    mlServerUrl + "/get-pronounced-text", requestEntity, Map.class);
            System.out.println(response);
            return CompletableFuture.completedFuture(response);
        } catch (HttpClientErrorException e) {
            // 에러 상태 코드 확인
            if (e.getStatusCode().value() == 422) {
                throw new CustomException(ErrorCode.UNPROCESSABLE_TEXT);
            } else {
                System.out.println("22222");
                System.out.println(e.getStatusCode().value());
                throw new CustomException(ErrorCode.ML_SERVER_RESPONSE_IS_EMPTY);// 다른 에러는 그대로 던지기
            }
        }
    }
}


