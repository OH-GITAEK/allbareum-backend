package net.allbareum.allbareumbackend.domain.user.presentation;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.allbareum.allbareumbackend.domain.user.application.UserApplicationService;
import net.allbareum.allbareumbackend.domain.user.application.dto.UserCreateRequestDto;
import net.allbareum.allbareumbackend.domain.user.application.dto.UserLogInRequestDto;
import net.allbareum.allbareumbackend.domain.user.application.dto.UserLogInResponseDto;
import net.allbareum.allbareumbackend.domain.user.application.dto.UserResponseDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserApplicationService userApplicationService;

    @PostMapping("/sign-up")
    @Operation(summary = "회원가입")
    public UserResponseDto signUp(@RequestBody @Valid UserCreateRequestDto userCreateRequestDto) {
        UserResponseDto creaeteUser = userApplicationService.signUp(userCreateRequestDto);
        return creaeteUser;
    }

}
