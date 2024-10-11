package net.allbareum.allbareumbackend.domain.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @UuidGenerator
    @Column(name = "user_id", nullable = false, unique = true)
    private String id;

    @Column(unique = true)
    private String email;
    private String password;
    private String username;
    @Column(unique = true)
    private String nickname;


    private String role;
}
