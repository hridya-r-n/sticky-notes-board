package com.stickynotes.service;

import com.stickynotes.model.StickyNote;


public interface NoteObserver {

    /**
     * Called automatically whenever a note changes.
     *
     * @param event  "CREATED", "UPDATED", "DELETED", "PINNED"
     * @param note   The note that was affected
     */
    void onNoteChanged(String event, StickyNote note);
}
