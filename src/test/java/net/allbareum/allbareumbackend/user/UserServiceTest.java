package net.allbareum.allbareumbackend.user;

import net.allbareum.allbareumbackend.domain.user.application.UserApplicationService;
import net.allbareum.allbareumbackend.domain.user.application.dto.UserCreateRequestDto;
import net.allbareum.allbareumbackend.domain.user.application.dto.UserResponseDto;
import net.allbareum.allbareumbackend.domain.user.domain.User;
import net.allbareum.allbareumbackend.domain.user.infrastructure.UserRepository;
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
}
