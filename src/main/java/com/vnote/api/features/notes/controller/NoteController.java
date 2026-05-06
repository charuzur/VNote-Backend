package com.vnote.api.features.notes.controller;

import com.vnote.api.features.notes.model.Note;
import com.vnote.api.features.notes.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/notes")
@CrossOrigin("*")
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    // Fetch all notes for a specific user to display on the Dashboard
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Note>> getUserNotes(@PathVariable Long userId) {
        List<Note> notes = noteRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return ResponseEntity.ok(notes);
    }

    // Create a new note
    @PostMapping
    public ResponseEntity<?> createNote(@RequestBody Note note) {
        Note savedNote = noteRepository.save(note);
        return ResponseEntity.ok(savedNote);
    }

    // Edit/Update an existing note
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNote(@PathVariable Long id, @RequestBody Note noteDetails) {
        Optional<Note> optionalNote = noteRepository.findById(id);

        if (optionalNote.isPresent()) {
            Note existingNote = optionalNote.get();
            existingNote.setTitle(noteDetails.getTitle());
            existingNote.setContent(noteDetails.getContent());

            noteRepository.save(existingNote);
            return ResponseEntity.ok(Map.of("message", "Note updated successfully"));
        }
        return ResponseEntity.status(404).body("Note not found");
    }

    // Delete a note
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable Long id) {
        if (noteRepository.existsById(id)) {
            noteRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Note deleted successfully"));
        }
        return ResponseEntity.status(404).body("Note not found");
    }
}