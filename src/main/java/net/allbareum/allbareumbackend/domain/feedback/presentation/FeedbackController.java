package net.allbareum.allbareumbackend.domain.feedback.presentation;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import net.allbareum.allbareumbackend.domain.feedback.application.FeedbackApplicationService;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.PronunciationFeedbackCreateRequestDto;
import net.allbareum.allbareumbackend.domain.feedback.application.dto.PronunciationFeedbackResponseDto;
import net.allbareum.allbareumbackend.global.security.userdetails.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/feedback/pronunciation")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackApplicationService feedbackApplicationService;

    @PostMapping(value = "/create", produces = "application/json", consumes = "multipart/form-data")
    @Operation(summary = "피드백 생성")
    public PronunciationFeedbackResponseDto createPronunciation(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestPart("textSentence") String textSentence,
                                                                @RequestPart("audioFile") MultipartFile audioFile) throws IOException, ExecutionException, InterruptedException {
        PronunciationFeedbackCreateRequestDto pronunciationFeedbackCreateRequestDto = new PronunciationFeedbackCreateRequestDto(textSentence,audioFile);
        return this.feedbackApplicationService.createPronunciation(userDetails.getUser(), pronunciationFeedbackCreateRequestDto);
    }
}
