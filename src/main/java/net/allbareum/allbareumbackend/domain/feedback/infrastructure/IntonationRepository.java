package net.allbareum.allbareumbackend.domain.feedback.infrastructure;

import net.allbareum.allbareumbackend.domain.feedback.domain.Intonation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntonationRepository extends JpaRepository<Intonation,String> {
}
