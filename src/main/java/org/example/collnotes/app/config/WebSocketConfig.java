package org.example.collnotes.app.config;

import lombok.RequiredArgsConstructor;
import org.example.collnotes.app.security.JwtUtil;
import org.example.collnotes.app.websocket.NoteWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final NoteWebSocketHandler noteWebSocketHandler;
    private final JwtUtil jwtUtil;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(noteWebSocketHandler, "/ws/notes/{noteId}")
                .setAllowedOrigins("*")
                .addInterceptors(new JwtHandshakeInterceptor(jwtUtil));
    }
}
