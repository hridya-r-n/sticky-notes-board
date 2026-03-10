package com.stickynotes;

import com.stickynotes.model.NoteRequest;
import com.stickynotes.model.StickyNote;
import com.stickynotes.service.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UNIT TESTS — Verify that NoteService works correctly.
 *
 * @SpringBootTest loads the full Spring application context.
 * @BeforeEach runs before every test to start with a clean state.
 *
 * Each test follows the AAA pattern:
 *   Arrange → Act → Assert
 */
@SpringBootTest
class StickyNotesApplicationTests {

    @Autowired
    private NoteService noteService;

    // Clear all notes before each test so tests don't affect each other
    @BeforeEach
    void setUp() {
        // Delete all notes from the store
        List<StickyNote> all = noteService.getAllNotes();
        for (StickyNote note : all) {
            noteService.deleteNote(note.getId());
        }
    }

    // ── Test 1: App context loads ──────────────────────────────────────────────
    @Test
    void contextLoads() {
        // If the app starts without errors, this test passes
        assertNotNull(noteService);
    }

    // ── Test 2: Create a note ──────────────────────────────────────────────────
    @Test
    void createNote_shouldAddNoteToList() {
        // Arrange
        NoteRequest request = new NoteRequest("Test Title", "Test Content", "#fef08a");

        // Act
        StickyNote created = noteService.createNote(request);

        // Assert
        assertNotNull(created.getId());
        assertEquals("Test Title", created.getTitle());
        assertEquals("Test Content", created.getContent());
        assertFalse(created.isPinned());
    }

    // ── Test 3: Get all notes ──────────────────────────────────────────────────
    @Test
    void getAllNotes_shouldReturnAllCreatedNotes() {
        // Arrange: create two notes
        noteService.createNote(new NoteRequest("Note A", "Content A", "#fef08a"));
        noteService.createNote(new NoteRequest("Note B", "Content B", "#86efac"));

        // Act
        List<StickyNote> all = noteService.getAllNotes();

        // Assert
        assertEquals(2, all.size());
    }

    // ── Test 4: Toggle pin ─────────────────────────────────────────────────────
    @Test
    void togglePin_shouldSwitchPinnedState() {
        // Arrange
        StickyNote note = noteService.createNote(new NoteRequest("Pin Test", "", "#fef08a"));
        assertFalse(note.isPinned()); // starts unpinned

        // Act: pin it
        Optional<StickyNote> pinned = noteService.togglePin(note.getId());

        // Assert: now pinned
        assertTrue(pinned.isPresent());
        assertTrue(pinned.get().isPinned());

        // Act: unpin it
        Optional<StickyNote> unpinned = noteService.togglePin(note.getId());

        // Assert: now unpinned again
        assertFalse(unpinned.get().isPinned());
    }

    // ── Test 5: Delete a note ──────────────────────────────────────────────────
    @Test
    void deleteNote_shouldRemoveFromList() {
        // Arrange
        StickyNote note = noteService.createNote(new NoteRequest("Delete Me", "", "#fef08a"));
        assertEquals(1, noteService.getAllNotes().size());

        // Act
        boolean deleted = noteService.deleteNote(note.getId());

        // Assert
        assertTrue(deleted);
        assertEquals(0, noteService.getAllNotes().size());
        assertTrue(noteService.getNoteById(note.getId()).isEmpty());
    }

    // ── Test 6: Search notes ───────────────────────────────────────────────────
    @Test
    void searchNotes_shouldFindMatchingNotes() {
        // Arrange
        noteService.createNote(new NoteRequest("Spring Boot Guide", "REST API stuff", "#fef08a"));
        noteService.createNote(new NoteRequest("Grocery List",      "Milk and eggs", "#86efac"));

        // Act: search for "spring"
        List<StickyNote> results = noteService.searchNotes("spring");

        // Assert: only one result
        assertEquals(1, results.size());
        assertEquals("Spring Boot Guide", results.get(0).getTitle());
    }

    // ── Test 7: Blank title validation ────────────────────────────────────────
    @Test
    void createNote_withBlankTitle_shouldThrowException() {
        // Arrange
        NoteRequest badRequest = new NoteRequest("", "Some content", "#fef08a");

        // Act & Assert: expect an exception
        assertThrows(IllegalArgumentException.class, () -> {
            noteService.createNote(badRequest);
        });
    }
}
