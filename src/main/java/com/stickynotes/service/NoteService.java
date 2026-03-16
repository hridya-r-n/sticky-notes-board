package com.stickynotes.service;

import com.stickynotes.model.NoteRequest;
import com.stickynotes.model.StickyNote;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;


@Service
public class NoteService {

    private final List<StickyNote> notes = new ArrayList<>();

    private final AtomicLong idCounter = new AtomicLong(1);

    private final List<NoteObserver> observers = new ArrayList<>();

    public void addObserver(NoteObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(String event, StickyNote note) {
        for (NoteObserver observer : observers) {
            observer.onNoteChanged(event, note);
        }
    }

    // Constructor: seed sample notes
    public NoteService() {
        // Register a simple console logger as an observer
        addObserver((event, note) ->
            System.out.println("[NoteService] " + event + " → \"" + note.getTitle() + "\"")
        );

        // Add a few sample notes using the Builder pattern
        notes.add(new StickyNote.Builder(idCounter.getAndIncrement(), "Welcome! ")
                .content("This is your sticky notes board. Create, edit, pin and delete notes!")
                .color("#6b3d5a")
                .pinned(true)
                .build());

        notes.add(new StickyNote.Builder(idCounter.getAndIncrement(), "Sample Note")
                .content("This is a sample note")
                .color("#3a5c38")
                .build());

        notes.add(new StickyNote.Builder(idCounter.getAndIncrement(), "Test Note ")
                .content("These are some notes")
                .color("#2a4a6b")
                .build());
    }

    //CRUD Operations
    
    public List<StickyNote> getAllNotes() {
        List<StickyNote> sorted = new ArrayList<>();
        for (StickyNote note : notes) {
            if (note.isPinned()) sorted.add(note);
        }
        for (StickyNote note : notes) {
            if (!note.isPinned()) sorted.add(note);
        }
        return sorted;
    }

    //  Finds a single note by its ID.
    
    public Optional<StickyNote> getNoteById(long id) {
        for (StickyNote note : notes) {
            if (note.getId() == id) {
                return Optional.of(note);
            }
        }
        return Optional.empty();
    }

    // Create new note
    public StickyNote createNote(NoteRequest request) {
        // Title not blank
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        String color = (request.getColor() != null) ? request.getColor() : "#6b3d5a";

        StickyNote note = new StickyNote.Builder(idCounter.getAndIncrement(), request.getTitle().trim())
                .content(request.getContent() != null ? request.getContent() : "")
                .color(color)
                .build();

        notes.add(note);
        notifyObservers("CREATED", note);
        return note;
    }

    //   Updates the title, content, and color of an existing note.

    public Optional<StickyNote> updateNote(long id, NoteRequest request) {
        Optional<StickyNote> found = getNoteById(id);
        if (found.isPresent()) {
            StickyNote note = found.get();
            if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
                note.setTitle(request.getTitle().trim());
            }
            if (request.getContent() != null) {
                note.setContent(request.getContent());
            }
            if (request.getColor() != null) {
                note.setColor(request.getColor());
            }
            notifyObservers("UPDATED", note);
        }
        return found;
    }

    
    public Optional<StickyNote> togglePin(long id) {
        Optional<StickyNote> found = getNoteById(id);
        if (found.isPresent()) {
            StickyNote note = found.get();
            note.setPinned(!note.isPinned());
            notifyObservers(note.isPinned() ? "PINNED" : "UNPINNED", note);
        }
        return found;
    }

    //  Deletes a note by ID
    
    public boolean deleteNote(long id) {
        Optional<StickyNote> found = getNoteById(id);
        if (found.isPresent()) {
            notes.remove(found.get());
            notifyObservers("DELETED", found.get());
            return true;
        }
        return false;
    }

    
    //  Searches notes by title or content (case-insensitive).
     
    public List<StickyNote> searchNotes(String query) {
        List<StickyNote> results = new ArrayList<>();
        String q = query.toLowerCase();
        for (StickyNote note : notes) {
            boolean titleMatch   = note.getTitle().toLowerCase().contains(q);
            boolean contentMatch = note.getContent() != null &&
                                   note.getContent().toLowerCase().contains(q);
            if (titleMatch || contentMatch) {
                results.add(note);
            }
        }
        return results;
    }

    
    // Returns total note count
     
    public int getTotalCount() {
        return notes.size();
    }
}
