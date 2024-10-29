package net.allbareum.allbareumbackend.domain.feedback.presentation;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackApplicationService feedbackApplicationService;

    @PostMapping("/create")
    @Operation(summary = "피드백 생성")
    public FeedbackResponse signUp(@RequestBody @Valid FeedbackCreateRequestDto feedbackCreateRequestDto) {
        FeedbackResponse feedbackResponse = feedbackApplicationService.create(feedbackCreateRequestDto);
        return feedbackResponse;
    }
}
