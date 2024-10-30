package net.allbareum.allbareumbackend.domain.feedback.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.allbareum.allbareumbackend.domain.feedback.domain.Feedback;
import net.allbareum.allbareumbackend.domain.user.domain.User;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResponseDto {
    private String id;
    private String userId;
    private String textSentence;
    private List<Integer> incorrectWordIndices;
    private Double accuracyScore;
    private String speechFeedback;
    private String frequencyFeedback;
    private String oralStructureImage;
    private String frequencyAnalysisImage;

    @Builder
    public FeedbackResponseDto(Feedback feedback, String oralStructureImage,  String frequencyAnalysisImage) {
        this.id = feedback.getId();
        this.userId = feedback.getUser().getId();
        this.textSentence = feedback.getTextSentence();
        this.incorrectWordIndices = feedback.getIncorrectWordIndices();
        this.accuracyScore = feedback.getAccuracyScore();
        this.speechFeedback = feedback.getSpeechFeedback();
        this.frequencyFeedback = feedback.getFrequencyFeedback();
        this.oralStructureImage = oralStructureImage;
        this.frequencyAnalysisImage = frequencyAnalysisImage;
    }
}
