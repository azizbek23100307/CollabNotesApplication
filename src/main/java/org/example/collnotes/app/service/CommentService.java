package org.example.collnotes.app.service;


import lombok.RequiredArgsConstructor;
import org.example.collnotes.app.dto.CommentDTO;
import org.example.collnotes.app.dto.UserDTO;
import org.example.collnotes.app.entity.Comment;
import org.example.collnotes.app.entity.Note;
import org.example.collnotes.app.entity.User;
import org.example.collnotes.app.exception.AppException;
import org.example.collnotes.app.repository.CommentRepository;
import org.example.collnotes.app.repository.NoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final NoteRepository noteRepository;

    @Transactional
    public CommentDTO.Response addComment(Long noteId, CommentDTO.CreateRequest request, User author) {
        if (!noteRepository.hasAccess(noteId, author)) {
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        }
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new AppException("Note not found", HttpStatus.NOT_FOUND));

        Comment comment = Comment.builder()
                .note(note)
                .author(author)
                .text(request.getText())
                .build();
        comment = commentRepository.save(comment);
        return toResponse(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentDTO.Response> getComments(Long noteId, User user) {
        if (!noteRepository.hasAccess(noteId, user)) {
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        }
        return commentRepository.findByNoteIdOrderByCreatedAtDesc(noteId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public CommentDTO.Response resolveComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException("Comment not found", HttpStatus.NOT_FOUND));
        comment.setResolved(true);
        return toResponse(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException("Comment not found", HttpStatus.NOT_FOUND));
        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new AppException("Not authorized", HttpStatus.FORBIDDEN);
        }
        commentRepository.delete(comment);
    }

    private CommentDTO.Response toResponse(Comment c) {
        UserDTO author = UserDTO.builder()
                .id(c.getAuthor().getId())
                .username(c.getAuthor().getUsername())
                .email(c.getAuthor().getEmail())
                .displayName(c.getAuthor().getDisplayName())
                .avatarColor(c.getAuthor().getAvatarColor())
                .build();
        return CommentDTO.Response.builder()
                .id(c.getId())
                .text(c.getText())
                .author(author)
                .resolved(c.isResolved())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
