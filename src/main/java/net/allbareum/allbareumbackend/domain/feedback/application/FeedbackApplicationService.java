package net.allbareum.allbareumbackend.domain.feedback.application;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.FeedbackCreateRequestDto;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.IntonationFeedbackResponseDto;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.PronunciationFeedbackResponseDto;
import net.allbareum.allbareumbackend.domain.feedback.domain.service.IntonationService;
import net.allbareum.allbareumbackend.domain.feedback.domain.service.PronunciationService;
import net.allbareum.allbareumbackend.domain.user.domain.User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class FeedbackApplicationService {
    private final PronunciationService pronunciationService;
    private final IntonationService intonationService;

    @Transactional
    public PronunciationFeedbackResponseDto createPronunciation(User user, FeedbackCreateRequestDto feedbackCreateRequestDto) throws IOException, ExecutionException, InterruptedException {
        return pronunciationService.createPronunciation(user, feedbackCreateRequestDto);
    }

    @Transactional
    public IntonationFeedbackResponseDto createIntonation(User user, FeedbackCreateRequestDto feedbackCreateRequestDto) throws IOException, ExecutionException, InterruptedException {
        return intonationService.createIntonation(user, feedbackCreateRequestDto);
    }
}
