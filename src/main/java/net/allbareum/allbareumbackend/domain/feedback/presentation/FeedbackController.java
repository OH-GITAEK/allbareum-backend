package net.allbareum.allbareumbackend.domain.feedback.presentation;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.FeedbackCreateRequestDto;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.FeedbackResponse;
import net.allbareum.allbareumbackend.global.security.userdetails.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public FeedbackResponse create(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody @Valid FeedbackCreateRequestDto feedbackCreateRequestDto) {
        FeedbackResponse feedbackResponse = feedbackApplicationService.create(userDetails,feedbackCreateRequestDto);
        return feedbackResponse;
    }
}
