package net.allbareum.allbareumbackend.domain.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jdk.jfr.Enabled;
import lombok.Getter;
import org.hibernate.annotations.UuidGenerator;

@Enabled
@Getter
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
    private String Nickname;


    private String role;
}
