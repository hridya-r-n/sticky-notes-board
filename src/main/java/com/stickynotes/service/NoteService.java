package com.stickynotes.service;

import com.stickynotes.model.NoteRequest;
import com.stickynotes.model.StickyNote;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * SERVICE LAYER — Contains all the business logic.
 *
 * In Spring MVC architecture there are three layers:
 *   Controller  →  Service  →  Data (in our case, a simple List)
 *
 * The Controller handles HTTP requests and delegates to this Service.
 * This Service does the actual work (add, delete, search etc.).
 * Keeping them separate makes the code cleaner and easier to test.
 *
 * @Service tells Spring to manage this class as a "bean" — meaning
 * Spring creates one instance and shares it wherever it's needed.
 *
 * Syllabus link: Spring Framework, Spring MVC
 *
 * ─── Observer Pattern (syllabus self-study) ────────────────────────────────
 * This service acts as the "Subject" in the Observer pattern.
 * Any class that wants to know when notes change can implement NoteObserver
 * and register itself here. We've included a simple logger as an example.
 */
@Service
public class NoteService {

    // ─── In-Memory Storage ─────────────────────────────────────────────────────
    // We use a plain ArrayList instead of a database.
    // Notes live in memory while the app is running.
    private final List<StickyNote> notes = new ArrayList<>();

    // Thread-safe ID generator — gives each note a unique number
    private final AtomicLong idCounter = new AtomicLong(1);

    // ─── Observer Pattern ──────────────────────────────────────────────────────
    // Observers are notified whenever a note is added or deleted.
    private final List<NoteObserver> observers = new ArrayList<>();

    public void addObserver(NoteObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(String event, StickyNote note) {
        for (NoteObserver observer : observers) {
            observer.onNoteChanged(event, note);
        }
    }

    // ─── Constructor: seed sample notes ────────────────────────────────────────
    public NoteService() {
        // Register a simple console logger as an observer
        addObserver((event, note) ->
            System.out.println("[NoteService] " + event + " → \"" + note.getTitle() + "\"")
        );

        // Add a few sample notes using the Builder pattern
        notes.add(new StickyNote.Builder(idCounter.getAndIncrement(), "Welcome! 👋")
                .content("This is your sticky notes board. Create, edit, pin and delete notes!")
                .color("#fef08a")
                .pinned(true)
                .build());

        notes.add(new StickyNote.Builder(idCounter.getAndIncrement(), "Spring Boot")
                .content("This app uses Spring Boot with an embedded Tomcat server — no setup needed!")
                .color("#86efac")
                .build());

        notes.add(new StickyNote.Builder(idCounter.getAndIncrement(), "REST API")
                .content("Try the API: GET /api/notes, POST /api/notes, DELETE /api/notes/{id}")
                .color("#93c5fd")
                .build());
    }

    // ─── CRUD Operations ───────────────────────────────────────────────────────

    /**
     * Returns all notes (pinned ones first).
     */
    public List<StickyNote> getAllNotes() {
        List<StickyNote> sorted = new ArrayList<>();
        // Pinned notes come first
        for (StickyNote note : notes) {
            if (note.isPinned()) sorted.add(note);
        }
        for (StickyNote note : notes) {
            if (!note.isPinned()) sorted.add(note);
        }
        return sorted;
    }

    /**
     * Finds a single note by its ID.
     * Returns Optional.empty() if not found (safer than returning null).
     */
    public Optional<StickyNote> getNoteById(long id) {
        for (StickyNote note : notes) {
            if (note.getId() == id) {
                return Optional.of(note);
            }
        }
        return Optional.empty();
    }

    /**
     * Creates a new note from the request data.
     * Uses the Builder pattern to construct the StickyNote object.
     */
    public StickyNote createNote(NoteRequest request) {
        // Validate: title must not be blank
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        String color = (request.getColor() != null) ? request.getColor() : "#fef08a";

        StickyNote note = new StickyNote.Builder(idCounter.getAndIncrement(), request.getTitle().trim())
                .content(request.getContent() != null ? request.getContent() : "")
                .color(color)
                .build();

        notes.add(note);
        notifyObservers("CREATED", note);
        return note;
    }

    /**
     * Updates the title, content, and color of an existing note.
     */
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

    /**
     * Toggles the pinned state of a note (pinned ↔ unpinned).
     * This is a simple example of the Observer pattern in action —
     * the console logs every pin/unpin event.
     */
    public Optional<StickyNote> togglePin(long id) {
        Optional<StickyNote> found = getNoteById(id);
        if (found.isPresent()) {
            StickyNote note = found.get();
            note.setPinned(!note.isPinned());
            notifyObservers(note.isPinned() ? "PINNED" : "UNPINNED", note);
        }
        return found;
    }

    /**
     * Deletes a note by ID. Returns true if deleted, false if not found.
     */
    public boolean deleteNote(long id) {
        Optional<StickyNote> found = getNoteById(id);
        if (found.isPresent()) {
            notes.remove(found.get());
            notifyObservers("DELETED", found.get());
            return true;
        }
        return false;
    }

    /**
     * Searches notes by title or content (case-insensitive).
     */
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

    /**
     * Returns total note count — useful for the frontend counter.
     */
    public int getTotalCount() {
        return notes.size();
    }
}
