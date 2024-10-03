package net.allbareum.allbareumbackend.util;

public class ObjectFixtures {
    // user
    public static User getUser() {
        return new User.builder()
                .username("testUser1")
                .password("qlalfqjsgh1!")
                .Nickname("testNickname1")
                .email("testEmail@gmail.com")
                .role("MEMBER")
                .build();
    }
}
