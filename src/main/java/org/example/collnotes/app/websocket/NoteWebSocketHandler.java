package org.example.collnotes.app.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.example.collnotes.app.dto.UserDTO;
import org.example.collnotes.app.dto.WsMessage;
import org.example.collnotes.app.entity.User;
import org.example.collnotes.app.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class NoteWebSocketHandler extends TextWebSocketHandler {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    // noteId -> set of sessions
    private final Map<String, Set<WebSocketSession>> noteRooms = new ConcurrentHashMap<>();
    // sessionId -> noteId
    private final Map<String, String> sessionNoteMap = new ConcurrentHashMap<>();
    // sessionId -> user email
    private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String noteId = extractNoteId(session);
        String userEmail = (String) session.getAttributes().get("userEmail");

        sessionNoteMap.put(session.getId(), noteId);
        sessionUserMap.put(session.getId(), userEmail);

        noteRooms.computeIfAbsent(noteId, k -> ConcurrentHashMap.newKeySet()).add(session);

        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user != null) {
            WsMessage.NoteUpdate joinMsg = WsMessage.NoteUpdate.builder()
                    .type(WsMessage.Type.USER_JOINED)
                    .noteId(Long.parseLong(noteId))
                    .user(toUserDTO(user))
                    .build();
            broadcastToNote(noteId, joinMsg, session.getId());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String noteId = sessionNoteMap.get(session.getId());
        if (noteId == null) return;

        try {
            WsMessage.NoteUpdate update = objectMapper.readValue(message.getPayload(), WsMessage.NoteUpdate.class);
            broadcastToNote(noteId, update, session.getId());
        } catch (Exception e) {
            // malformed message - ignore
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String noteId = sessionNoteMap.remove(session.getId());
        String userEmail = sessionUserMap.remove(session.getId());

        if (noteId != null) {
            Set<WebSocketSession> sessions = noteRooms.get(noteId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    noteRooms.remove(noteId);
                }
            }
        }

        if (noteId != null && userEmail != null) {
            User user = userRepository.findByEmail(userEmail).orElse(null);
            if (user != null) {
                WsMessage.NoteUpdate leaveMsg = WsMessage.NoteUpdate.builder()
                        .type(WsMessage.Type.USER_LEFT)
                        .noteId(Long.parseLong(noteId))
                        .user(toUserDTO(user))
                        .build();
                broadcastToNote(noteId, leaveMsg, session.getId());
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        session.close(CloseStatus.SERVER_ERROR);
    }

    private void broadcastToNote(String noteId, WsMessage.NoteUpdate message, String senderSessionId) throws IOException {
        Set<WebSocketSession> sessions = noteRooms.get(noteId);
        if (sessions == null) return;

        String json = objectMapper.writeValueAsString(message);
        TextMessage textMessage = new TextMessage(json);

        for (WebSocketSession s : sessions) {
            if (s.isOpen() && !s.getId().equals(senderSessionId)) {
                try {
                    s.sendMessage(textMessage);
                } catch (IOException e) {
                    // session broken - skip
                }
            }
        }
    }

    public List<String> getOnlineUsers(String noteId) {
        Set<WebSocketSession> sessions = noteRooms.getOrDefault(noteId, Collections.emptySet());
        List<String> emails = new ArrayList<>();
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                String email = sessionUserMap.get(s.getId());
                if (email != null) emails.add(email);
            }
        }
        return emails;
    }

    private String extractNoteId(WebSocketSession session) {
        String path = session.getUri().getPath();
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }

    private UserDTO toUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatarColor(user.getAvatarColor())
                .build();
    }
}
