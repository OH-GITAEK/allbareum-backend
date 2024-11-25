package net.allbareum.allbareumbackend.domain.feedback.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.FeedbackCreateRequestDto;
import net.allbareum.allbareumbackend.global.exception.CustomException;
import net.allbareum.allbareumbackend.global.exception.ErrorCode;
import net.allbareum.allbareumbackend.global.util.MultipartResponseParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
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
    public CompletableFuture<ResponseEntity<Map>> getFeedback(FeedbackCreateRequestDto feedbackCreateRequestDto) throws IOException {
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
            handleHttpError(e);
            return CompletableFuture.failedFuture(new CustomException(ErrorCode.ML_SERVER_RESPONSE_IS_EMPTY));
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
            handleHttpError(e);
            return CompletableFuture.failedFuture(new CustomException(ErrorCode.ML_SERVER_RESPONSE_IS_EMPTY));
        }
    }

    @Async("taskExecutor")  // AsyncConfig에서 정의한 스레드 풀 사용
    public CompletableFuture<Map<String, Object>> getByteFeedback(FeedbackCreateRequestDto feedbackCreateRequestDto) throws IOException {
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
                return originalFileName;
            }
        });
        body.add("text", feedbackCreateRequestDto.getTextSentence());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 4. RestTemplate에 바이트 배열 처리용 메시지 컨버터 추가
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());

        System.out.println(requestEntity);
        // 5. ML 서버로 요청 전송
        ResponseEntity<byte[]> response;
        try {
            response = restTemplate.postForEntity(
                    mlServerUrl + "/get-feedback", requestEntity, byte[].class);
        } catch (HttpClientErrorException e) {
            handleHttpError(e);
            return CompletableFuture.failedFuture(new CustomException(ErrorCode.ML_SERVER_RESPONSE_IS_EMPTY));
        }


        // 6. 멀티파트 응답 처리
        byte[] responseBody = response.getBody();
        if (responseBody == null || !response.getHeaders().getContentType().toString().contains("multipart")) {
            throw new IOException("ML 서버로부터 멀티파트 응답을 받지 못했습니다.");
        }

        String boundary = extractBoundary(response.getHeaders().getContentType());
        MultipartResponseParser parser = new MultipartResponseParser(responseBody, boundary);

        // 7. 응답 데이터 추출
        Map<String, Object> parsedResponse = parseMultipartResponse(parser);

        return CompletableFuture.completedFuture(parsedResponse);
    }

    private String extractBoundary(MediaType contentType) {
        String contentTypeValue = contentType.toString();
        String boundaryKey = "boundary=";
        int boundaryIndex = contentTypeValue.indexOf(boundaryKey);
        if (boundaryIndex == -1) {
            throw new IllegalArgumentException("Boundary 정보를 Content-Type에서 찾을 수 없습니다.");
        }
        return contentTypeValue.substring(boundaryIndex + boundaryKey.length());
    }

    private Map<String, Object> parseMultipartResponse(MultipartResponseParser parser) throws IOException {
        Map<String, Object> parsedResponse = new HashMap<>();
        parsedResponse.put("status", parser.getTextPart("status"));
        parsedResponse.put("feedback_count", parser.getTextPart("feedback_count"));
        parsedResponse.put("word_indexes", parser.getTextPart("word_indexes"));
        parsedResponse.put("intonation_score ", Double.parseDouble(parser.getTextPart("intonation_score ")));
        parsedResponse.put("intonation_feedbacks ", parser.getTextPart("intonation_feedbacks"));
        parsedResponse.put("feedback_image ", parser.getFilePart("feedback_image "));
        return parsedResponse;
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


