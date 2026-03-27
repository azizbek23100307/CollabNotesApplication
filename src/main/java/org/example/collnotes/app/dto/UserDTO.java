package org.example.collnotes.app.dto;

import lombok.*;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String displayName;
    private String avatarColor;
}
