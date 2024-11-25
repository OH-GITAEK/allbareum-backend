package net.allbareum.allbareumbackend.domain.feedback.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.allbareum.allbareumbackend.domain.feedback.domain.Intonation;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IntonationFeedbackResponseDto {
    private String id;
    private String userId;
    private String textSentence;

    private int status; // 발음 상태
    private int feedbackCount; // 피드백 수
    private String wordIndex; // 문제 발생 단어 인덱스 리스트
    private String intonationFeedbacks; // 발음 피드백 리스트
    private String feedbackImageUrls; // 피드백 이미지 이름 리스트
    private double intonationScore; // 발음 점수

    @Builder
    public IntonationFeedbackResponseDto(Intonation intonation) {
        this.id = intonation.getId();
        this.userId = intonation.getUser().getId();
        this.textSentence = intonation.getTextSentence();
        this.status = intonation.getStatus(); // 발음 상태
        this.feedbackCount = intonation.getFeedbackCount(); // 피드백 수
        this.wordIndex = intonation.getWordIndex(); // 문제 발생 단어 인덱스 리스트
        this.intonationFeedbacks = intonation.getIntonationFeedbacks(); // 발음 피드백 리스트
        this.feedbackImageUrls = intonation.getFeedbackImageUrls(); // 피드백 이미지 이름 리스트
        this.intonationScore = intonation.getIntonationScore(); // 발음 점수
    }
}