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
public class Feedback {

    @Id
    @UuidGenerator
    @Column(name = "feedback_id", nullable = false, unique = true)
    private String id;

    private String textSentence; // 프론트에서 전달받은 텍스트 문장

    private String transcription;
    private String pronunciation_feedback;
    private Double pronunciation_score;
//    private String pronunciation_feedback_image;

    private String intonation_feedback;
    private Double intonation_score;
//    private String intonation_feedback_image;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
