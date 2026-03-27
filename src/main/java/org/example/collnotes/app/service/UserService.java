package org.example.collnotes.app.service;

import lombok.RequiredArgsConstructor;
import org.example.collnotes.app.dto.UserDTO;
import org.example.collnotes.app.entity.User;
import org.example.collnotes.app.exception.AppException;
import org.example.collnotes.app.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
    }

    public UserDTO getProfile(String email) {
        User user = getCurrentUser(email);
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatarColor(user.getAvatarColor())
                .build();
    }
}
