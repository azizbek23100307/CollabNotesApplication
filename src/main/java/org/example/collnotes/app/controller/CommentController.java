package org.example.collnotes.app.controller;


import jakarta.validation.Valid;
import org.example.collnotes.app.dto.CommentDTO;
import org.example.collnotes.app.service.CommentService;
import org.example.collnotes.app.service.UserService;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes/{noteId}/comments")
public class CommentController extends BaseController {

    private final CommentService commentService;

    public CommentController(UserService userService, CommentService commentService) {
        super(userService);
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentDTO.Response> add(
            @PathVariable Long noteId,
            @Valid @RequestBody CommentDTO.CreateRequest request,
            Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(noteId, request, getAuthUser(auth)));
    }

    @GetMapping
    public ResponseEntity<List<CommentDTO.Response>> getAll(
            @PathVariable Long noteId, Authentication auth) {
        return ResponseEntity.ok(commentService.getComments(noteId, getAuthUser(auth)));
    }

    @PatchMapping("/{commentId}/resolve")
    public ResponseEntity<CommentDTO.Response> resolve(
            @PathVariable Long noteId,
            @PathVariable Long commentId,
            Authentication auth) {
        return ResponseEntity.ok(commentService.resolveComment(commentId, getAuthUser(auth)));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long noteId,
            @PathVariable Long commentId,
            Authentication auth) {
        commentService.deleteComment(commentId, getAuthUser(auth));
        return ResponseEntity.noContent().build();
    }
}
