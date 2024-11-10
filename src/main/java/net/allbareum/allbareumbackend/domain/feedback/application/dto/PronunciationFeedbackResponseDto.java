package net.allbareum.allbareumbackend.domain.feedback.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.allbareum.allbareumbackend.domain.feedback.domain.Feedback;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PronunciationFeedbackResponseDto {
    private String id;
    private String userId;
    private String textSentence;

    private String transcription;
    private String pronunciation_feedback;
    private Double pronunciation_score;
    private String pronunciation_feedback_image;

    private String intonation_feedback;
    private String intonation_feedback_image;


    @Builder
    public PronunciationFeedbackResponseDto(Feedback feedback) {
        this.id = feedback.getId();
        this.userId = feedback.getUser().getId();
        this.textSentence = feedback.getTextSentence();
        this.transcription = feedback.getTranscription();
        this.pronunciation_feedback = feedback.getPronunciation_feedback();
        this.pronunciation_score = feedback.getPronunciation_score();
        this.pronunciation_feedback_image =feedback.getPronunciation_feedback_image();
        this.intonation_feedback = feedback.getIntonation_feedback();
        this.intonation_feedback_image = feedback.getIntonation_feedback_image();
    }
}
