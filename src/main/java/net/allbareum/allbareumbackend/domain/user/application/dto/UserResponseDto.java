package net.allbareum.allbareumbackend.domain.user.application.dto;

import lombok.Builder;
import lombok.Getter;
import net.allbareum.allbareumbackend.domain.user.domain.User;

@Getter
public class UserResponseDto {
    private String id;
    private String email;
    private String username;
    private String nickname;
    private String role;


    @Builder
    public UserResponseDto(User user) {
        this.id = user.getId().toString();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.role = user.getRole();
    }
}
