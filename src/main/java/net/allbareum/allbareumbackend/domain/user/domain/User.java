package net.allbareum.allbareumbackend.domain.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jdk.jfr.Enabled;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Enabled
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
    private String nickName;


    private String role;
}
