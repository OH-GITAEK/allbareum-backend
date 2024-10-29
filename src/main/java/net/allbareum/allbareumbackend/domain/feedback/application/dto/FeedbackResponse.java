package net.allbareum.allbareumbackend.domain.feedback.application.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class FeedbackResponse {
    private String id;
    private String textSentence;
    private List<Integer> incorrectWordIndices;
    private Double accuracyScore;
    private String speechFeedback;
    private String frequencyFeedback;
    private String oralStructureImage;
    private String frequencyAnalysisImage;
}
