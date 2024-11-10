package net.allbareum.allbareumbackend.domain.feedback.application;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.PronunciationFeedbackCreateRequestDto;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.PronunciationFeedbackResponseDto;
import net.allbareum.allbareumbackend.domain.feedback.domain.FeedbackService;
import net.allbareum.allbareumbackend.domain.user.domain.User;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FeedbackApplicationService {
    private final FeedbackService feedbackService;

    @Transactional
    public PronunciationFeedbackResponseDto createPronunciation(User user, PronunciationFeedbackCreateRequestDto pronunciationFeedbackCreateRequestDto) throws IOException {
        return feedbackService.createPronunciation(user, pronunciationFeedbackCreateRequestDto);
    }
}
