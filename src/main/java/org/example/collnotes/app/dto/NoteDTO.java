package org.example.collnotes.app.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

public class NoteDTO {

    @Getter @Setter
    public static class CreateRequest {
        @NotBlank
        private String title;
        private String content;
    }

    @Getter @Setter
    public static class UpdateRequest {
        private String title;
        private String content;
    }

    @Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
    public static class Response {
        private Long id;
        private String title;
        private String content;
        private UserDTO owner;
        private List<UserDTO> collaborators;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private boolean isOwner;
    }

    @Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
    public static class Summary {
        private Long id;
        private String title;
        private UserDTO owner;
        private int collaboratorCount;
        private LocalDateTime updatedAt;
        private boolean isOwner;
    }
}
