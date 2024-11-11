package net.allbareum.allbareumbackend.domain.feedback.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.allbareum.allbareumbackend.domain.user.domain.User;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pronunciation {

    @Id
    @UuidGenerator
    @Column(name = "feedback_id", nullable = false, unique = true)
    private String id;

    private String textSentence; // 프론트에서 전달받은 텍스트 문장

    private int status; // 발음 상태
    private String transcription; // 음성 텍스트 전사
    private int feedbackCount; // 피드백 수
    @ElementCollection
    @CollectionTable(name = "pronunciation_word_index", joinColumns = @JoinColumn(name = "pronunciation_id"))
    @Column(name = "word_index")
    private List<Integer> wordIndex; // 문제 발생 단어 인덱스 리스트
    @ElementCollection
    @CollectionTable(name = "pronunciation_feedbacks", joinColumns = @JoinColumn(name = "pronunciation_id"))
    @Column(name = "pronunciation_feedbacks")
    private List<String> pronunciationFeedbacks; // 발음 피드백 리스트
    @ElementCollection
    @CollectionTable(name = "feedback_image_names", joinColumns = @JoinColumn(name = "pronunciation_id"))
    @Column(name = "pronunciation_image_name")
    private List<String> feedbackImageUrls; // 피드백 이미지 경로 리스트
    @ElementCollection
    @CollectionTable(name = "wrong_spellings", joinColumns = @JoinColumn(name = "pronunciation_id"))
    @Column(name = "wrong_spelling")
    private List<String> wrongSpellings; // 잘못된 발음
    private double pronunciationScore; // 발음 점수

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
