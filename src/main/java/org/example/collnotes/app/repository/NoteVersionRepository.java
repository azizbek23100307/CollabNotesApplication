package org.example.collnotes.app.repository;

import org.example.collnotes.app.entity.NoteVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface NoteVersionRepository extends JpaRepository<NoteVersion, Long> {
    List<NoteVersion> findByNoteIdOrderByCreatedAtDesc(Long noteId, Pageable pageable);
    long countByNoteId(Long noteId);
}
