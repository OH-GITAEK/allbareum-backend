package net.allbareum.allbareumbackend.domain.feedback.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.allbareum.allbareumbackend.domain.feedback.domain.Pronunciation;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PronunciationFeedbackResponseDto {
    private String id;
    private String userId;
    private String textSentence;

    private int status; // 발음 상태
    private String pronounced_text; //올바르게 발음한 문장
    private String transcription; // 음성 텍스트 전사
    private int feedbackCount; // 피드백 수
    private List<Integer> wordIndex; // 문제 발생 단어 인덱스 리스트
    private List<String> pronunciationFeedbacks; // 발음 피드백 리스트
    private List<String> feedbackImageUrls; // 피드백 이미지 이름 리스트
    private List<String> wrongSpellings; // 잘못된 발음 리스트
    private double pronunciationScore; // 발음 점수

    @Builder
    public PronunciationFeedbackResponseDto(Pronunciation pronunciation) {
        this.id = pronunciation.getId();
        this.userId = pronunciation.getUser().getId();
        this.textSentence = pronunciation.getTextSentence();
        this.status = pronunciation.getStatus(); // 발음 상태
        this.pronounced_text = pronunciation.getPronounced_text(); //올바르게 발음한 문장
        this.transcription = pronunciation.getTranscription(); // 음성 텍스트 전사
        this.feedbackCount = pronunciation.getFeedbackCount(); // 피드백 수
        this.wordIndex = pronunciation.getWordIndex(); // 문제 발생 단어 인덱스 리스트
        this.pronunciationFeedbacks = pronunciation.getPronunciationFeedbacks(); // 발음 피드백 리스트
        this.feedbackImageUrls = pronunciation.getFeedbackImageUrls(); // 피드백 이미지 이름 리스트
        this.wrongSpellings = pronunciation.getWrongSpellings(); // 잘못된 발음 리스트
        this.pronunciationScore = pronunciation.getPronunciationScore(); // 발음 점수
    }
}
