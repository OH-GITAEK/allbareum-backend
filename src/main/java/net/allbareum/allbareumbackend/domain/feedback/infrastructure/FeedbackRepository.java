package net.allbareum.allbareumbackend.domain.feedback.infrastructure;

import net.allbareum.allbareumbackend.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<User,String> {
}
