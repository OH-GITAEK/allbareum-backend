package net.allbareum.allbareumbackend.user;

import net.allbareum.allbareumbackend.domain.user.application.UserApplicationService;
import net.allbareum.allbareumbackend.domain.user.application.dto.UserCreateRequestDto;
import net.allbareum.allbareumbackend.domain.user.application.dto.UserResponseDto;
import net.allbareum.allbareumbackend.domain.user.domain.User;
import net.allbareum.allbareumbackend.domain.user.infrastructure.UserRepository;
import net.allbareum.allbareumbackend.global.exception.CustomException;
import net.allbareum.allbareumbackend.global.exception.ErrorCode;
import net.allbareum.allbareumbackend.util.ObjectFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserApplicationService userApplicationService;

    @Autowired
    UserRepository userRepository;

    User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        user = ObjectFixtures.getUser();
        userRepository.save(user);  // 실제로 메모리 DB에 유저를 저장
    }

    @DisplayName("회원 가입")
    @Test
    void signUp() {
        // Given
        UserCreateRequestDto userCreateRequestDto = new UserCreateRequestDto("signUpEmail@gmail.com", "qlalfqjsgh1!", "testUser2", "testNickname2");

        // When
        UserResponseDto result = userApplicationService.signUp(userCreateRequestDto);

        // Then
        User createdUser = userRepository.findById(result.getId()).orElseThrow();  // 메모리 DB에서 유저 조회
        assertNotNull(result.getId());
        assertEquals(result.getEmail(), userCreateRequestDto.getEmail());
        assertEquals(result.getNickname(), userCreateRequestDto.getNickname());
    }

    @DisplayName("중복 이메일로 회원가입 시 예외 발생")
    @Test
    void signUpWithDuplicateEmail() {
        // Given
        UserCreateRequestDto userCreateRequestDto = new UserCreateRequestDto("duplicateEmail@gmail.com", "qlalfqjsgh1!", "testUser2", "testNickname2");
        userApplicationService.signUp(userCreateRequestDto);  // 첫 번째 회원가입

        // When & Then
        UserCreateRequestDto duplicateEmailRequestDto = new UserCreateRequestDto("duplicateEmail@gmail.com", "qlalfqjsgh1!", "testUser3", "testNickname3");

        CustomException exception = assertThrows(CustomException.class, () -> {
            userApplicationService.signUp(duplicateEmailRequestDto);  // 중복된 이메일로 회원가입 시도
        });

        assertEquals(ErrorCode.USER_ALREADY_EXIST, exception.getErrorCode());
    }

    @DisplayName("중복 닉네임으로 회원가입 시 예외 발생")
    @Test
    void signUpWithDuplicateNickname() {
        // Given
        UserCreateRequestDto userCreateRequestDto = new UserCreateRequestDto("test1@gmail.com", "qlalfqjsgh1!", "testUser2", "testNickname");
        userApplicationService.signUp(userCreateRequestDto);  // 첫 번째 회원가입

        // When & Then
        UserCreateRequestDto duplicateNicknameRequestDto = new UserCreateRequestDto("test2@gmail.com", "qlalfqjsgh1!", "testUser3", "testNickname");

        CustomException exception = assertThrows(CustomException.class, () -> {
            userApplicationService.signUp(duplicateNicknameRequestDto);  // 중복된 닉네임으로 회원가입 시도
        });

        assertEquals(ErrorCode.NICKNAME_ALREADY_EXIST, exception.getErrorCode());
    }

}
