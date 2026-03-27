package org.example.collnotes.app.dto;

import jakarta.validation.constraints.*;
import lombok.*;

public class AuthDTO {

    @Getter @Setter
    public static class RegisterRequest {
        @NotBlank @Size(min = 3, max = 50)
        private String username;

        @NotBlank @Email
        private String email;

        @NotBlank @Size(min = 6)
        private String password;

        private String displayName;
    }

    @Getter @Setter
    public static class LoginRequest {
        @NotBlank
        private String email;

        @NotBlank
        private String password;
    }

    @Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
    public static class AuthResponse {
        private String token;
        private UserDTO user;
    }
}
