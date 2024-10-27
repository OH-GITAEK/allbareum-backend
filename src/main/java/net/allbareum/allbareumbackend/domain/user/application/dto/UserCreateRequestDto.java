package net.allbareum.allbareumbackend.domain.user.application.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequestDto {

    @Email(message = "올바른 이메일 형식이어야 합니다.")
    @NotEmpty(message = "이메일은 필수 입력 사항입니다.")
    private String email;

    @NotEmpty(message = "비밀번호는 필수 입력 사항입니다.")
    private String password;

    @NotEmpty(message = "사용자명은 필수 입력 사항입니다.")
    private String username;

    @NotEmpty(message = "닉네임은 필수 입력 사항입니다.")
    private String nickname;

}
