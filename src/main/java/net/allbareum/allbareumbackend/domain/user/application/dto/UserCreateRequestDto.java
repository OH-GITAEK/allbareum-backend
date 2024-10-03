package net.allbareum.allbareumbackend.domain.user.application.dto;

import jakarta.persistence.Column;
import lombok.Getter;

@Getter
public class UserCreateRequestDto {

    private String email;
    private String password;
    private String username;
    private String nickName;
    private String role;
}
