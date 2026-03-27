package org.example.collnotes.app.repository;

import org.example.collnotes.app.entity.Note;
import org.example.collnotes.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByOwnerOrderByUpdatedAtDesc(User owner);

    @Query("SELECT n FROM Note n JOIN n.collaborators c WHERE c = :user ORDER BY n.updatedAt DESC")
    List<Note> findSharedNotesForUser(@Param("user") User user);

    @Query("SELECT n FROM Note n WHERE n.owner = :user OR :user MEMBER OF n.collaborators ORDER BY n.updatedAt DESC")
    List<Note> findAllAccessibleByUser(@Param("user") User user);

    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM Note n WHERE n.id = :noteId AND (n.owner = :user OR :user MEMBER OF n.collaborators)")
    boolean hasAccess(@Param("noteId") Long noteId, @Param("user") User user);

    Optional<Note> findByIdAndOwner(Long id, User owner);
}
