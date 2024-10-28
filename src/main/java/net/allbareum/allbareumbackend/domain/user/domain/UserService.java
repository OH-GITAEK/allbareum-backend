package net.allbareum.allbareumbackend.domain.user.domain;


import lombok.RequiredArgsConstructor;
import net.allbareum.allbareumbackend.domain.user.application.dto.UserCreateRequestDto;
import net.allbareum.allbareumbackend.domain.user.application.dto.UserLogInRequestDto;
import net.allbareum.allbareumbackend.domain.user.application.dto.UserLogInResponseDto;
import net.allbareum.allbareumbackend.domain.user.infrastructure.UserRepository;
import net.allbareum.allbareumbackend.global.exception.CustomException;
import net.allbareum.allbareumbackend.global.exception.ErrorCode;
import net.allbareum.allbareumbackend.global.security.jwt.JWTUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;

    public User signUp(UserCreateRequestDto userCreateRequestDto) {
        if (userRepository.existsByEmail(userCreateRequestDto.getEmail())) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXIST);
        }
        if (userRepository.existsByNickname(userCreateRequestDto.getNickname())) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXIST);
        }

        User user = User.builder()
                .email(userCreateRequestDto.getEmail())
                .password(bCryptPasswordEncoder.encode(userCreateRequestDto.getPassword()))
                .username(userCreateRequestDto.getUsername())
                .nickname(userCreateRequestDto.getNickname())
                .role("MEMBER")
                .build();
        return userRepository.save(user);
    }

    public UserLogInResponseDto logIn(UserLogInRequestDto userLogInRequestDto) {
        User user = userRepository.findByEmail(userLogInRequestDto.getEmail()).orElseThrow(
                () -> new CustomException(
                        ErrorCode.USER_NOT_EXIST)
        );
        String accessToken = jwtUtil.createJwt(user.getId(), user.getRole(), 60*60*10L);
        System.out.println("Generated JWT: " + accessToken);
        return new UserLogInResponseDto(accessToken);
    }
}
