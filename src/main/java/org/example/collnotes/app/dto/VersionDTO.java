package org.example.collnotes.app.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class VersionDTO {
    private Long id;
    private String title;
    private String content;
    private UserDTO editedBy;
    private LocalDateTime createdAt;
}
