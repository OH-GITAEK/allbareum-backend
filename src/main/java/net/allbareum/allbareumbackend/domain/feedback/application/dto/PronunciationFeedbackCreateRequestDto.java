package net.allbareum.allbareumbackend.domain.feedback.application.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PronunciationFeedbackCreateRequestDto {
    @NotEmpty(message = "피드백 받을 문장을 입력해주세요")
    private String textSentence;

    @NotEmpty(message = "음성 파일 입력은 사항입니다.")
    private MultipartFile audioFile;
}
