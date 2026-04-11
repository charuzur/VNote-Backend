package com.vnote.api.features.notes.repository;

import com.vnote.api.features.notes.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    // Automatically generates a SQL query to find notes by user ID and sort them by newest first
    List<Note> findByUserIdOrderByCreatedAtDesc(Long userId);
}