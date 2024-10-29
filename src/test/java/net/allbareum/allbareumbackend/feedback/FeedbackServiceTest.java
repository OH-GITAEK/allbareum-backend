package net.allbareum.allbareumbackend.feedback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
public class FeedbackServiceTest {
    @Autowired
    private FeedbackApplicationService feedbackApplicationService;

    @Test
    public void testCreateFeedback() throws IOException {
        // Given
        MockMultipartFile audioFile = new MockMultipartFile(
                "audioFile", "test_audio.3gp", "audio/3gp", "dummy audio content".getBytes());
        String textSentence = "Hello, how are you?";

        // When
        FeedbackResponse feedbackResponse = feedbackApplicationService.create(audioFile, textSentence);

        // Then
        assertNotNull(feedbackResponse);
        assertEquals("Hello, how are you?", feedbackResponse.getTextSentence());
        assertNotNull(feedbackResponse.getIncorrectWordIndices());
        assertNotNull(feedbackResponse.getAccuracyScore());
        assertNotNull(feedbackResponse.getSpeechFeedback());
        assertNotNull(feedbackResponse.getOralStructureImage());
        assertNotNull(feedbackResponse.getFrequencyAnalysisImage());
        assertNotNull(feedbackResponse.getFrequencyFeedback());
    }
}
