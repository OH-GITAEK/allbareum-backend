package net.allbareum.allbareumbackend.domain.user.application;

import lombok.RequiredArgsConstructor;
import net.allbareum.allbareumbackend.domain.user.application.dto.UserCreateRequestDto;
import net.allbareum.allbareumbackend.domain.user.application.dto.UserResponseDto;
import net.allbareum.allbareumbackend.domain.user.domain.User;
import net.allbareum.allbareumbackend.domain.user.domain.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserApplicationService {
    private final UserService userService;

    public UserResponseDto signUp(UserCreateRequestDto userCreateRequestDto) {

        User user = userService.signUp(userCreateRequestDto);
        return new UserResponseDto(user);
    }
}
