package net.allbareum.allbareumbackend.user;

import net.allbareum.allbareumbackend.domain.user.domain.User;
import net.allbareum.allbareumbackend.util.ObjectFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserService userService;

    @MockBean
    UserRepository userRepository;

    User user;

    @BeforeEach
    void setUp(){
        user = ObjectFixtures.getUser();
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
    }

    @DisplayName("회원 가입")
    @Test
    void signUp(){
        //Given
        UserCreateRequestDto userCreateRequestDto = new UserCreateRequestDto("signUpEmail@gmail.com","qlalfqjsgh1!","tsetUser2","testNickname2","MEMBER");

        //When
        UserResponseDto result = userService.signUp(userCreateRequestDto);

        //Then
        User createdUser = userRepository.findById(result.getId()).orElseThrow();

        // UserResponseDto와 User 엔티티의 특정 속성을 비교하여 일치하는지 확인
        assertEquals(result.getEmail(), createdUser.getEmail());
        assertEquals(result.getNickname(), createdUser.getNickname());
    }
}
