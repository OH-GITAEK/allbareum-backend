package net.allbareum.allbareumbackend.domain.feedback.domain;

import jakarta.persistence.*;
import net.allbareum.allbareumbackend.domain.user.domain.User;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;

@Entity
public class Feedback {

    @Id
    @UuidGenerator
    @Column(name = "feedback_id", nullable = false, unique = true)
    private String id;

    private String textSentence; // 프론트에서 전달받은 텍스트 문장

    @ElementCollection
    private List<Integer> incorrectWordIndices;

    private Double accuracyScore;

    private String speechFeedback;

    private String frequencyFeedback;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
