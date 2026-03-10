package com.stickynotes.service;

import com.stickynotes.model.StickyNote;

/**
 * OBSERVER PATTERN — Interface for the Observer pattern (syllabus self-study).
 *
 * The Observer pattern lets objects "subscribe" to events.
 * When something changes (a note is added/deleted), all observers are told.
 *
 * Real-world analogy: YouTube subscriptions.
 * - YouTube channel = Subject (NoteService)
 * - Subscribers     = Observers (NoteObserver)
 * - New video       = Event (note created/deleted)
 *
 * How it works in this project:
 *   1. NoteService is the "Subject" — it holds the list of notes
 *   2. NoteObserver is the interface observers must implement
 *   3. Any class that wants to react to note changes implements this interface
 *   4. Currently used for console logging — could easily be extended
 *      to send emails, update a counter, etc.
 */
public interface NoteObserver {

    /**
     * Called automatically whenever a note changes.
     *
     * @param event  What happened — "CREATED", "UPDATED", "DELETED", "PINNED"
     * @param note   The note that was affected
     */
    void onNoteChanged(String event, StickyNote note);
}
