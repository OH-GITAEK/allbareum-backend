package net.allbareum.allbareumbackend.domain.user.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserLogInResponseDto {
    private String accessToken;
}
