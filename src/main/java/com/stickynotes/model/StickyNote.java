package com.stickynotes.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * MODEL — Represents a single sticky note.
 *
 * This is a plain Java class (also called a POJO - Plain Old Java Object).
 * No special annotations needed here. It just holds data.
 *
 * Spring Boot's Jackson library will automatically convert this
 * class to JSON when we return it from a REST endpoint.
 *
 * Example JSON output:
 * {
 *   "id": 1,
 *   "title": "My Note",
 *   "content": "Remember to study!",
 *   "color": "#fef08a",
 *   "pinned": false,
 *   "createdAt": "10 Mar 2025"
 * }
 *
 * Syllabus link: Builder Pattern (self-study)
 */
public class StickyNote {

    private long id;
    private String title;
    private String content;
    private String color;       // hex color e.g. "#fef08a"
    private boolean pinned;
    private String createdAt;   // formatted date string

    // ─── Constructor ───────────────────────────────────────────────────────────
    public StickyNote(long id, String title, String content, String color) {
        this.id        = id;
        this.title     = title;
        this.content   = content;
        this.color     = color;
        this.pinned    = false;
        this.createdAt = LocalDateTime.now()
                            .format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    // Default constructor — required by Jackson (JSON library) to deserialize
    public StickyNote() {}

    // ─── Getters ───────────────────────────────────────────────────────────────
    public long   getId()        { return id; }
    public String getTitle()     { return title; }
    public String getContent()   { return content; }
    public String getColor()     { return color; }
    public boolean isPinned()    { return pinned; }
    public String getCreatedAt() { return createdAt; }

    // ─── Setters ───────────────────────────────────────────────────────────────
    public void setId(long id)           { this.id = id; }
    public void setTitle(String title)   { this.title = title; }
    public void setContent(String c)     { this.content = c; }
    public void setColor(String color)   { this.color = color; }
    public void setPinned(boolean p)     { this.pinned = p; }
    public void setCreatedAt(String d)   { this.createdAt = d; }

    // ─── Builder Pattern ───────────────────────────────────────────────────────
    //
    // The Builder pattern (from your syllabus self-study) lets us create
    // objects step-by-step instead of one giant constructor call.
    //
    // Usage:
    //   StickyNote note = new StickyNote.Builder(1, "Title")
    //                          .content("Some text")
    //                          .color("#fef08a")
    //                          .build();
    //
    public static class Builder {

        // Required fields
        private final long id;
        private final String title;

        // Optional fields with defaults
        private String content = "";
        private String color   = "#fef08a";
        private boolean pinned = false;

        public Builder(long id, String title) {
            this.id    = id;
            this.title = title;
        }

        public Builder content(String content) { this.content = content; return this; }
        public Builder color(String color)     { this.color   = color;   return this; }
        public Builder pinned(boolean pinned)  { this.pinned  = pinned;  return this; }

        public StickyNote build() {
            StickyNote note = new StickyNote(this.id, this.title, this.content, this.color);
            note.setPinned(this.pinned);
            return note;
        }
    }
}
