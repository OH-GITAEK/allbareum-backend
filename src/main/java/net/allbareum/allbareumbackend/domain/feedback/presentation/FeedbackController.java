package net.allbareum.allbareumbackend.domain.feedback.presentation;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.allbareum.allbareumbackend.domain.feedback.application.FeedbackApplicationService;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.FeedbackCreateRequestDto;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.FeedbackResponseDto;
import net.allbareum.allbareumbackend.global.security.userdetails.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackApplicationService feedbackApplicationService;

    @PostMapping(value = "/create", produces = "application/json", consumes = "multipart/form-data")
    @Operation(summary = "피드백 생성")
    public FeedbackResponseDto create(@AuthenticationPrincipal CustomUserDetails userDetails,  @RequestPart("textSentence") String textSentence,
                                      @RequestPart("audioFile") MultipartFile audioFile) throws IOException {
        System.out.println("controller도착");
        FeedbackCreateRequestDto feedbackCreateRequestDto = new FeedbackCreateRequestDto(textSentence,audioFile);
        return this.feedbackApplicationService.create(userDetails.getUser(),feedbackCreateRequestDto);
    }
}
