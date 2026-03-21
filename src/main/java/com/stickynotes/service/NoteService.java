package com.stickynotes.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stickynotes.model.NoteRequest;
import com.stickynotes.model.StickyNote;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;


@Service
public class NoteService {

    //  File path 
    private static final String FILE_PATH = "notes.json";

    //  Jackson ObjectMapper 
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    //  In-memory list 
    private final List<StickyNote> notes = new ArrayList<>();

    //  ID counter 
    private final AtomicLong idCounter = new AtomicLong(1);

    //  Observer pattern 
    private final List<NoteObserver> observers = new ArrayList<>();

    public void addObserver(NoteObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(String event, StickyNote note) {
        for (NoteObserver observer : observers) {
            observer.onNoteChanged(event, note);
        }
    }

    //  Constructor 
    public NoteService() {
        // Register console logger as observer
        addObserver((event, note) ->
            System.out.println("[NoteService] " + event + " → \"" + note.getTitle() + "\"")
        );

        // Load existing notes from file
        loadFromFile();

        // If no notes were loaded add sample notes
        if (notes.isEmpty()) {
            notes.add(new StickyNote.Builder(idCounter.getAndIncrement(), "Welcome! ")
                    .content("This is your sticky notes board. Create, edit, pin and delete notes!")
                    .color("#3d2e3a").pinned(true).build());
            saveToFile();
        }
    }

    //  FILE OPERATIONS 

    private void loadFromFile() {
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            System.out.println("[NoteService] No notes.json found — starting fresh.");
            return;
        }

        try {
            // Jackson reads the JSON array and converts each object to StickyNote
            List<StickyNote> loaded = objectMapper.readValue(
                file,
                new TypeReference<List<StickyNote>>() {}
            );

            notes.addAll(loaded);

            // Set ID counter to max existing ID + 1 so new notes get unique IDs
            long maxId = loaded.stream()
                    .mapToLong(StickyNote::getId)
                    .max()
                    .orElse(0);
            idCounter.set(maxId + 1);

            System.out.println("[NoteService] Loaded " + loaded.size() + " notes from notes.json");

        } catch (IOException e) {
            System.out.println("[NoteService] Could not read notes.json: " + e.getMessage());
        }
    }

    // Writes the current ArrayList to notes.json.
    // Called after every create, update, delete, or pin operation.
    
    private void saveToFile() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValue(new File(FILE_PATH), notes);
        } catch (IOException e) {
            System.out.println("[NoteService] Could not save notes.json: " + e.getMessage());
        }
    }

    //  CRUD OPERATIONS 

    
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

   
    //  Finds a single note by ID.
     
    public Optional<StickyNote> getNoteById(long id) {
        for (StickyNote note : notes) {
            if (note.getId() == id) return Optional.of(note);
        }
        return Optional.empty();
    }

    
    //  Creates a new note, adds it to the list, and saves to file.
     
    public StickyNote createNote(NoteRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        String color = (request.getColor() != null) ? request.getColor() : "#1a2d4a";

        StickyNote note = new StickyNote.Builder(idCounter.getAndIncrement(), request.getTitle().trim())
                .content(request.getContent() != null ? request.getContent() : "")
                .color(color)
                .build();

        notes.add(note);
        saveToFile();  
        notifyObservers("CREATED", note);
        return note;
    }

    
    //  Updates an existing note and saves to file.
    
    public Optional<StickyNote> updateNote(long id, NoteRequest request) {
        Optional<StickyNote> found = getNoteById(id);
        if (found.isPresent()) {
            StickyNote note = found.get();
            if (request.getTitle() != null && !request.getTitle().trim().isEmpty())
                note.setTitle(request.getTitle().trim());
            if (request.getContent() != null)
                note.setContent(request.getContent());
            if (request.getColor() != null)
                note.setColor(request.getColor());
            saveToFile(); 
            notifyObservers("UPDATED", note);
        }
        return found;
    }

    
    //  Toggles pin state and saves to file.
    
    public Optional<StickyNote> togglePin(long id) {
        Optional<StickyNote> found = getNoteById(id);
        if (found.isPresent()) {
            StickyNote note = found.get();
            note.setPinned(!note.isPinned());
            saveToFile();  // ← persist immediately
            notifyObservers(note.isPinned() ? "PINNED" : "UNPINNED", note);
        }
        return found;
    }

    
    //  Deletes a note and saves to file.
    
    public boolean deleteNote(long id) {
        Optional<StickyNote> found = getNoteById(id);
        if (found.isPresent()) {
            notes.remove(found.get());
            saveToFile();  // ← persist immediately
            notifyObservers("DELETED", found.get());
            return true;
        }
        return false;
    }

    
    //  Searches notes by keyword in title or content.
    
    public List<StickyNote> searchNotes(String query) {
        List<StickyNote> results = new ArrayList<>();
        String q = query.toLowerCase();
        for (StickyNote note : notes) {
            boolean titleMatch   = note.getTitle().toLowerCase().contains(q);
            boolean contentMatch = note.getContent() != null &&
                                   note.getContent().toLowerCase().contains(q);
            if (titleMatch || contentMatch) results.add(note);
        }
        return results;
    }

    public int getTotalCount() { return notes.size(); }
}