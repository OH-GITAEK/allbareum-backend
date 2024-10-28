package net.allbareum.allbareumbackend.domain.user.application;

import lombok.RequiredArgsConstructor;
import net.allbareum.allbareumbackend.domain.user.application.dto.UserCreateRequestDto;
import net.allbareum.allbareumbackend.domain.user.application.dto.UserLogInRequestDto;
import net.allbareum.allbareumbackend.domain.user.application.dto.UserLogInResponseDto;
import net.allbareum.allbareumbackend.domain.user.application.dto.UserResponseDto;
import net.allbareum.allbareumbackend.domain.user.domain.User;
import net.allbareum.allbareumbackend.domain.user.domain.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserApplicationService {
    private final UserService userService;

    @Transactional
    public UserResponseDto signUp(UserCreateRequestDto userCreateRequestDto) {

        User user = userService.signUp(userCreateRequestDto);
        return new UserResponseDto(user);
    }
}
