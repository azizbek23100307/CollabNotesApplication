package org.example.collnotes.app.controller;


import lombok.RequiredArgsConstructor;
import org.example.collnotes.app.entity.User;
import org.example.collnotes.app.service.UserService;
import org.springframework.security.core.Authentication;

@RequiredArgsConstructor
public abstract class BaseController {

    protected final UserService userService;

    protected User getAuthUser(Authentication auth) {
        return userService.getCurrentUser(auth.getName());
    }
}
