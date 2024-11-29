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
public class Intonation {

    @Id
    @UuidGenerator
    @Column(name = "feedback_id", nullable = false, unique = true)
    private String id;

    private String sentenceCode; // 프론트에서 전달받은 텍스트 문장

    private int status; // 발음 상태
    @Column(name = "intonation_feedbacks")
    private String intonationFeedbacks; // 발음 피드백 리스트
    @Column(name = "intonation_image_name")
    private String feedbackImageUrls; // 피드백 이미지 경로 리스트
    private double intonationScore; // 발음 점수

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
