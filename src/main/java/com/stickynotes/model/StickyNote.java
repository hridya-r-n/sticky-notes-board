package com.stickynotes.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class StickyNote {

    private long id;
    private String title;
    private String content;
    private String color;       
    private boolean pinned;
    private String createdAt;  

    // Constructor
    public StickyNote(long id, String title, String content, String color) {
        this.id        = id;
        this.title     = title;
        this.content   = content;
        this.color     = color;
        this.pinned    = false;
        this.createdAt = LocalDateTime.now()
                            .format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    // Default constructor 
    public StickyNote() {}

    // Getters
    public long   getId()        { return id; }
    public String getTitle()     { return title; }
    public String getContent()   { return content; }
    public String getColor()     { return color; }
    public boolean isPinned()    { return pinned; }
    public String getCreatedAt() { return createdAt; }

    // Setters 
    public void setId(long id)           { this.id = id; }
    public void setTitle(String title)   { this.title = title; }
    public void setContent(String c)     { this.content = c; }
    public void setColor(String color)   { this.color = color; }
    public void setPinned(boolean p)     { this.pinned = p; }
    public void setCreatedAt(String d)   { this.createdAt = d; }

    
    public static class Builder {

        // Required fields
        private final long id;
        private final String title;

        // Optional fields with defaults
        private String content = "";
        private String color   = "#6b3d5a";
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
