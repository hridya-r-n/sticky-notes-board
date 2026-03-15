package com.stickynotes.controller;

import com.stickynotes.model.NoteRequest;
import com.stickynotes.model.StickyNote;
import com.stickynotes.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/notes")
public class NoteController {

    
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    // Returns all notes as a JSON array
    @GetMapping
    public ResponseEntity<List<StickyNote>> getAllNotes() {
        List<StickyNote> notes = noteService.getAllNotes();
        return ResponseEntity.ok(notes);   // HTTP 200 + JSON body
    }

    // Returns one note by its ID
    @GetMapping("/{id}")
    public ResponseEntity<StickyNote> getNoteById(@PathVariable long id) {
        Optional<StickyNote> note = noteService.getNoteById(id);
        if (note.isPresent()) {
            return ResponseEntity.ok(note.get());          // HTTP 200
        } else {
            return ResponseEntity.notFound().build();       // HTTP 404
        }
    }

    // Creates a new note. Browser sends JSON, Spring converts it to NoteRequest.
    @PostMapping
    public ResponseEntity<Object> createNote(@RequestBody NoteRequest request) {
        try {
            StickyNote created = noteService.createNote(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);  // HTTP 201
        } catch (IllegalArgumentException e) {
            // If validation fails, send back a helpful error message
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);   // HTTP 400
        }
    }

    // Updates an existing note's title, content, or color
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateNote(@PathVariable long id,
                                             @RequestBody NoteRequest request) {
        Optional<StickyNote> updated = noteService.updateNote(id, request);
        if (updated.isPresent()) {
            return ResponseEntity.ok(updated.get());          // HTTP 200
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Note with id " + id + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);  // HTTP 404
        }
    }

    // Toggles pin/unpin on a note
    @PatchMapping("/{id}/pin")
    public ResponseEntity<Object> togglePin(@PathVariable long id) {
        Optional<StickyNote> note = noteService.togglePin(id);
        if (note.isPresent()) {
            return ResponseEntity.ok(note.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Note with id " + id + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // Deletes a note by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteNote(@PathVariable long id) {
        boolean deleted = noteService.deleteNote(id);
        if (deleted) {
            Map<String, String> msg = new HashMap<>();
            msg.put("message", "Note deleted successfully");
            return ResponseEntity.ok(msg);                    // HTTP 200
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Note with id " + id + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // Searches notes by keyword
    @GetMapping("/search")
    public ResponseEntity<List<StickyNote>> searchNotes(@RequestParam String q) {
        List<StickyNote> results = noteService.searchNotes(q);
        return ResponseEntity.ok(results);
    }
}
