package net.allbareum.allbareumbackend.domain.user.application.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequestDto {

    private String email;
    private String password;
    private String username;
    private String nickname;
}
