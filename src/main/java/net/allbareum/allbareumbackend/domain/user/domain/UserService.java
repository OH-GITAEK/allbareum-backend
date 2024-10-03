package net.allbareum.allbareumbackend.domain.user.domain;


import lombok.RequiredArgsConstructor;
import net.allbareum.allbareumbackend.domain.user.application.dto.UserCreateRequestDto;
import net.allbareum.allbareumbackend.domain.user.infrastructure.UserRepository;

@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User signUp(UserCreateRequestDto userCreateRequestDto) {
        User user = User.builder()
                .email(userCreateRequestDto.getEmail())
                .password(userCreateRequestDto.getPassword())
                .username(userCreateRequestDto.getUsername())
                .nickName(userCreateRequestDto.getNickName())
                .build();
        return userRepository.save(user);
    }
}
