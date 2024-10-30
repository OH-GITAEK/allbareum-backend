package net.allbareum.allbareumbackend.domain.feedback.infrastructure;

import net.allbareum.allbareumbackend.domain.feedback.domain.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback,String> {
}
