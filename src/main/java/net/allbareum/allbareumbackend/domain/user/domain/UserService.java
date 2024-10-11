package net.allbareum.allbareumbackend.domain.user.domain;


import lombok.RequiredArgsConstructor;
import net.allbareum.allbareumbackend.domain.user.application.dto.UserCreateRequestDto;
import net.allbareum.allbareumbackend.domain.user.infrastructure.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public User signUp(UserCreateRequestDto userCreateRequestDto) {
        User user = User.builder()
                .email(userCreateRequestDto.getEmail())
                .password(bCryptPasswordEncoder.encode(userCreateRequestDto.getPassword()))
                .username(userCreateRequestDto.getUsername())
                .nickname(userCreateRequestDto.getNickname())
                .role("MEMBER")
                .build();
        return userRepository.save(user);
    }
}
