package org.example.collnotes.app.dto;

import lombok.*;

public class WsMessage {

    public enum Type {
        CONTENT_CHANGE, CURSOR_MOVE, USER_JOINED, USER_LEFT, COMMENT_ADDED, TITLE_CHANGE
    }

    @Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
    public static class NoteUpdate {
        private Type type;
        private Long noteId;
        private String content;
        private String title;
        private UserDTO user;
        private CursorPosition cursor;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    public static class CursorPosition {
        private int position;
        private int line;
    }
}
