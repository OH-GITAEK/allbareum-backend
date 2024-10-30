package net.allbareum.allbareumbackend.feedback;

import net.allbareum.allbareumbackend.domain.feedback.application.FeedbackApplicationService;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.FeedbackCreateRequestDto;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.FeedbackResponseDto;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class FeedbackServiceTest {
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
    public void testCreateFeedback() throws IOException {
        // Given: 오디오 파일을 리소스에서 읽어와 MockMultipartFile로 생성
        ClassPathResource audioResource = new ClassPathResource("dataset_Test_audio_0.wav");
        InputStream inputStream = audioResource.getInputStream();

        MultipartFile audioFile = new MockMultipartFile(
                "audioFile",
                "dataset_Test_audio_0.wav",
                "audio/wav",
                inputStream
        );

        String textSentence = "Hello, how are you?";
        FeedbackCreateRequestDto feedbackCreateRequestDto =
                new FeedbackCreateRequestDto(textSentence, audioFile);

        // When: 서비스 호출
        FeedbackResponseDto feedbackResponseDto =
                feedbackApplicationService.create(user, feedbackCreateRequestDto);

        // Then: 응답 값 검증
        assertNotNull(feedbackResponseDto);
        assertEquals("Hello, how are you?", feedbackResponseDto.getTextSentence());
        assertNotNull(feedbackResponseDto.getIncorrectWordIndices());
        assertNotNull(feedbackResponseDto.getAccuracyScore());
        assertNotNull(feedbackResponseDto.getSpeechFeedback());
        assertNotNull(feedbackResponseDto.getOralStructureImage());
        assertNotNull(feedbackResponseDto.getFrequencyAnalysisImage());
        assertNotNull(feedbackResponseDto.getFrequencyFeedback());
    }
}
