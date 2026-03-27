package org.example.collnotes.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

public class CommentDTO {

    @Getter @Setter
    public static class CreateRequest {
        @NotBlank
        private String text;
    }

    @Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
    public static class Response {
        private Long id;
        private String text;
        private UserDTO author;
        private boolean resolved;
        private LocalDateTime createdAt;
    }
}
