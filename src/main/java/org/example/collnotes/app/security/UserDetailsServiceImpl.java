package org.example.collnotes.app.security;

import lombok.RequiredArgsConstructor;
import org.example.collnotes.app.entity.User;
import org.example.collnotes.app.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. O'zingizning bazangizdan foydalanuvchini qidirasiz
        org.example.collnotes.app.entity.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // 2. Spring Security uchun UserDetails ob'ektini qaytarasiz
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList() // Bu yerda foydalanuvchi huquqlari (authorities) bo'lishi kerak
        );
    }
}
