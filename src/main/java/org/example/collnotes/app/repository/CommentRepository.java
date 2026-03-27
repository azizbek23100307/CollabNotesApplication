package org.example.collnotes.app.repository;

import org.example.collnotes.app.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByNoteIdOrderByCreatedAtDesc(Long noteId);
}
