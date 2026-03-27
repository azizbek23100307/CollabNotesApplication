package org.example.collnotes.app.service;

import lombok.RequiredArgsConstructor;
import org.example.collnotes.app.dto.NoteDTO;
import org.example.collnotes.app.dto.UserDTO;
import org.example.collnotes.app.dto.VersionDTO;
import org.example.collnotes.app.entity.Note;
import org.example.collnotes.app.entity.NoteVersion;
import org.example.collnotes.app.entity.User;
import org.example.collnotes.app.exception.AppException;
import org.example.collnotes.app.repository.NoteRepository;
import org.example.collnotes.app.repository.NoteVersionRepository;
import org.example.collnotes.app.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final NoteVersionRepository versionRepository;

    @Transactional
    public NoteDTO.Response createNote(NoteDTO.CreateRequest request, User owner) {
        Note note = Note.builder()
                .title(request.getTitle())
                .content(request.getContent() != null ? request.getContent() : "")
                .owner(owner)
                .build();
        note = noteRepository.save(note);
        return toResponse(note, owner);
    }

    @Transactional(readOnly = true)
    public List<NoteDTO.Summary> getAllNotes(User user) {
        return noteRepository.findAllAccessibleByUser(user).stream()
                .map(n -> toSummary(n, user))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NoteDTO.Response getNoteById(Long id, User user) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new AppException("Note not found", HttpStatus.NOT_FOUND));
        if (!noteRepository.hasAccess(id, user)) {
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        }
        return toResponse(note, user);
    }

    @Transactional
    public NoteDTO.Response updateNote(Long id, NoteDTO.UpdateRequest request, User user) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new AppException("Note not found", HttpStatus.NOT_FOUND));
        if (!noteRepository.hasAccess(id, user)) {
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        }

        saveVersion(note, user);

        if (request.getTitle() != null) note.setTitle(request.getTitle());
        if (request.getContent() != null) note.setContent(request.getContent());

        note = noteRepository.save(note);
        return toResponse(note, user);
    }

    @Transactional
    public void deleteNote(Long id, User user) {
        Note note = noteRepository.findByIdAndOwner(id, user)
                .orElseThrow(() -> new AppException("Note not found or you are not the owner", HttpStatus.FORBIDDEN));
        noteRepository.delete(note);
    }

    @Transactional
    public NoteDTO.Response addCollaborator(Long noteId, String usernameOrEmail, User owner) {
        Note note = noteRepository.findByIdAndOwner(noteId, owner)
                .orElseThrow(() -> new AppException("Note not found or you are not the owner", HttpStatus.FORBIDDEN));

        User collaborator = userRepository.findByEmail(usernameOrEmail)
                .or(() -> userRepository.findByUsername(usernameOrEmail))
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        if (collaborator.getId().equals(owner.getId())) {
            throw new AppException("Cannot add yourself as collaborator", HttpStatus.BAD_REQUEST);
        }

        note.getCollaborators().add(collaborator);
        note = noteRepository.save(note);
        return toResponse(note, owner);
    }

    @Transactional
    public NoteDTO.Response removeCollaborator(Long noteId, Long userId, User owner) {
        Note note = noteRepository.findByIdAndOwner(noteId, owner)
                .orElseThrow(() -> new AppException("Note not found or you are not the owner", HttpStatus.FORBIDDEN));

        note.getCollaborators().removeIf(u -> u.getId().equals(userId));
        note = noteRepository.save(note);
        return toResponse(note, owner);
    }

    @Transactional(readOnly = true)
    public List<VersionDTO> getVersions(Long noteId, User user) {
        if (!noteRepository.hasAccess(noteId, user)) {
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        }
        return versionRepository.findByNoteIdOrderByCreatedAtDesc(noteId, PageRequest.of(0, 5))
                .stream()
                .map(this::toVersionDTO)
                .collect(Collectors.toList());
    }

    private void saveVersion(Note note, User editor) {
        long count = versionRepository.countByNoteId(note.getId());
        if (count >= 5) {
            List<NoteVersion> versions = versionRepository.findByNoteIdOrderByCreatedAtDesc(
                    note.getId(), PageRequest.of(0, 5));
            if (!versions.isEmpty()) {
                versionRepository.delete(versions.get(versions.size() - 1));
            }
        }
        NoteVersion version = NoteVersion.builder()
                .note(note)
                .title(note.getTitle())
                .content(note.getContent())
                .editedBy(editor)
                .build();
        versionRepository.save(version);
    }

    private NoteDTO.Response toResponse(Note note, User currentUser) {
        return NoteDTO.Response.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .owner(toUserDTO(note.getOwner()))
                .collaborators(note.getCollaborators().stream().map(this::toUserDTO).collect(Collectors.toList()))
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .isOwner(note.getOwner().getId().equals(currentUser.getId()))
                .build();
    }

    private NoteDTO.Summary toSummary(Note note, User currentUser) {
        return NoteDTO.Summary.builder()
                .id(note.getId())
                .title(note.getTitle())
                .owner(toUserDTO(note.getOwner()))
                .collaboratorCount(note.getCollaborators().size())
                .updatedAt(note.getUpdatedAt())
                .isOwner(note.getOwner().getId().equals(currentUser.getId()))
                .build();
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

    private VersionDTO toVersionDTO(NoteVersion v) {
        return VersionDTO.builder()
                .id(v.getId())
                .title(v.getTitle())
                .content(v.getContent())
                .editedBy(v.getEditedBy() != null ? toUserDTO(v.getEditedBy()) : null)
                .createdAt(v.getCreatedAt())
                .build();
    }
}
