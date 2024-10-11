package net.allbareum.allbareumbackend.util;

import net.allbareum.allbareumbackend.domain.user.domain.User;

public class ObjectFixtures {
    // user
    public static User getUser() {
        return User.builder()
                .username("testUser1")
                .password("qlalfqjsgh1!")
                .nickname("testNickname1")
                .email("testEmail@gmail.com")
                .role("MEMBER")
                .build();
    }
}
