package net.allbareum.allbareumbackend.domain.user.application.dto;

import lombok.Builder;
import net.allbareum.allbareumbackend.domain.user.domain.User;

public class UserResponseDto {
    private String id;
    private String email;
    private String username;
    private String nickName;
    private String role;


    @Builder
    public UserResponseDto(User user) {
        this.id = user.getId().toString();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.nickName = user.getNickName();
        this.role = user.getRole();
    }
}
