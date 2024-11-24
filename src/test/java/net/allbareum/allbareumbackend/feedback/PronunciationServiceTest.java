package net.allbareum.allbareumbackend.feedback;

import net.allbareum.allbareumbackend.domain.feedback.application.FeedbackApplicationService;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.FeedbackCreateRequestDto;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.PronunciationFeedbackResponseDto;
import net.allbareum.allbareumbackend.domain.user.domain.User;
import net.allbareum.allbareumbackend.domain.user.infrastructure.UserRepository;
import net.allbareum.allbareumbackend.util.ObjectFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class PronunciationServiceTest {
    @Autowired
    private FeedbackApplicationService feedbackApplicationService;
    @Autowired
    UserRepository userRepository;

    User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        user = ObjectFixtures.getUser();
        userRepository.save(user);  // 실제로 메모리 DB에 유저를 저장
    }


    @Test
    public void testCreateFeedback() throws IOException, ExecutionException, InterruptedException {
        // Given: 오디오 파일을 리소스에서 읽어와 MockMultipartFile로 생성
        ClassPathResource audioResource = new ClassPathResource("0_3gp.3gp");
        InputStream inputStream = audioResource.getInputStream();

        MultipartFile audioFile = new MockMultipartFile(
                "audioFile",
                "0_3gp.3gp",
                "audio/3gp",
                inputStream
        );

        String textSentence = "나는 행복하게 끝나는 영화가 좋다";
        FeedbackCreateRequestDto feedbackCreateRequestDto =
                new FeedbackCreateRequestDto(textSentence, audioFile);

        // When: 서비스 호출
        PronunciationFeedbackResponseDto pronunciationFeedbackResponseDto =
                feedbackApplicationService.createPronunciation(user, feedbackCreateRequestDto);

        // Then: 응답 값 검증
        assertNotNull(pronunciationFeedbackResponseDto);
        assertEquals("나는 행복하게 끝나는 영화가 좋다", pronunciationFeedbackResponseDto.getTextSentence());
        assertNotNull(pronunciationFeedbackResponseDto.getPronunciationFeedbacks());
        assertNotNull(pronunciationFeedbackResponseDto.getTextSentence());
        assertNotNull(pronunciationFeedbackResponseDto.getFeedbackImageUrls());
        assertNotNull(pronunciationFeedbackResponseDto.getUserId());
        assertNotNull(pronunciationFeedbackResponseDto.getTranscription());
        assertNotNull(pronunciationFeedbackResponseDto.getWordIndex());
        assertNotNull(pronunciationFeedbackResponseDto.getWrongSpellings());
    }
}
