package net.allbareum.allbareumbackend.domain.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.allbareum.allbareumbackend.global.entity.BaseEntity;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "${custom.table.user:user}")
public class User extends BaseEntity {

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
