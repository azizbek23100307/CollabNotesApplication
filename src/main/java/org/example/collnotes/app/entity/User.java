package org.example.collnotes.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "avatar_color", length = 7)
    private String avatarColor;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToMany(mappedBy = "collaborators", fetch = FetchType.LAZY)
    private Set<Note> sharedNotes = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (avatarColor == null) {
            String[] colors = {"#6366f1", "#ec4899", "#14b8a6", "#f59e0b", "#3b82f6", "#10b981"};
            avatarColor = colors[new Random().nextInt(colors.length)];
        }
    }
}
