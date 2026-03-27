package org.example.collnotes.app.controller;

import jakarta.validation.Valid;
import org.example.collnotes.app.dto.NoteDTO;
import org.example.collnotes.app.dto.VersionDTO;
import org.example.collnotes.app.service.NoteService;
import org.example.collnotes.app.service.UserService;
import org.example.collnotes.app.websocket.NoteWebSocketHandler;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/notes")
public class NoteController extends BaseController {

    private final NoteService noteService;
    private final NoteWebSocketHandler wsHandler;

    public NoteController(UserService userService, NoteService noteService, NoteWebSocketHandler wsHandler) {
        super(userService);
        this.noteService = noteService;
        this.wsHandler = wsHandler;
    }

    @PostMapping
    public ResponseEntity<NoteDTO.Response> create(
            @Valid @RequestBody NoteDTO.CreateRequest request,
            Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noteService.createNote(request, getAuthUser(auth)));
    }

    @GetMapping
    public ResponseEntity<List<NoteDTO.Summary>> getAll(Authentication auth) {
        return ResponseEntity.ok(noteService.getAllNotes(getAuthUser(auth)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteDTO.Response> getById(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(noteService.getNoteById(id, getAuthUser(auth)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteDTO.Response> update(
            @PathVariable Long id,
            @RequestBody NoteDTO.UpdateRequest request,
            Authentication auth) {
        return ResponseEntity.ok(noteService.updateNote(id, request, getAuthUser(auth)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        noteService.deleteNote(id, getAuthUser(auth));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/collaborators")
    public ResponseEntity<NoteDTO.Response> addCollaborator(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication auth) {
        return ResponseEntity.ok(noteService.addCollaborator(id, body.get("usernameOrEmail"), getAuthUser(auth)));
    }

    @DeleteMapping("/{id}/collaborators/{userId}")
    public ResponseEntity<NoteDTO.Response> removeCollaborator(
            @PathVariable Long id,
            @PathVariable Long userId,
            Authentication auth) {
        return ResponseEntity.ok(noteService.removeCollaborator(id, userId, getAuthUser(auth)));
    }

    @GetMapping("/{id}/versions")
    public ResponseEntity<List<VersionDTO>> getVersions(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(noteService.getVersions(id, getAuthUser(auth)));
    }

    @GetMapping("/{id}/online")
    public ResponseEntity<List<String>> getOnlineUsers(@PathVariable Long id, Authentication auth) {
        noteService.getNoteById(id, getAuthUser(auth));
        return ResponseEntity.ok(wsHandler.getOnlineUsers(String.valueOf(id)));
    }
}
