package net.allbareum.allbareumbackend.domain.user.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLogInRequestDto {
    @Email(message = "이메일 형식에 맞지 않습니다.")
    @NotBlank(message = "이메일을 입력해 주세요.")
    @Schema(description = "이메일", example = "cau@cau.ac.kr")
    private String email;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    @Schema(description = "비밀번호", example = "password12!")
    private String password;
}
