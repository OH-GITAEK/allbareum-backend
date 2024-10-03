package net.allbareum.allbareumbackend.domain.user.infrastructure;

import net.allbareum.allbareumbackend.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String> {

}
