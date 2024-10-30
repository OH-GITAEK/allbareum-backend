package net.allbareum.allbareumbackend.domain.feedback.application;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.FeedbackCreateRequestDto;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.FeedbackResponseDto;
import net.allbareum.allbareumbackend.domain.feedback.domain.Feedback;
import net.allbareum.allbareumbackend.domain.feedback.domain.FeedbackService;
import net.allbareum.allbareumbackend.domain.user.domain.User;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FeedbackApplicationService {
    private final FeedbackService feedbackService;

    @Transactional
    public FeedbackResponseDto create(User user, FeedbackCreateRequestDto feedbackCreateRequestDto) throws IOException {
        System.out.println("FeedbackApplicationService 도착");
        return feedbackService.create(user,feedbackCreateRequestDto);
    }
}
