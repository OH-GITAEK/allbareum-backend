package net.allbareum.allbareumbackend.util;

import net.allbareum.allbareumbackend.domain.user.domain.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class ObjectFixtures {

    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    // user
    public static User getUser() {
        return User.builder()
                .username("testUser1")
                .password(bCryptPasswordEncoder.encode("qlalfqjsgh1!"))
                .nickname("testNickname1")
                .email("testEmail@gmail.com")
                .role("MEMBER")
                .build();
    }
}
