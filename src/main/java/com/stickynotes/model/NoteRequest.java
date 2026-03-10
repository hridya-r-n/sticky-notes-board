package com.stickynotes.model;

/**
 * REQUEST MODEL — Used to receive data sent from the browser/frontend.
 *
 * When the user creates or updates a note, the browser sends JSON like:
 * {
 *   "title": "My Note",
 *   "content": "Some text here",
 *   "color": "#86efac"
 * }
 *
 * Spring Boot automatically converts (deserializes) that JSON
 * into this Java object for us. No extra code needed — this is
 * Auto-configuration at work.
 */
public class NoteRequest {

    private String title;
    private String content;
    private String color;

    // Default constructor required for JSON deserialization
    public NoteRequest() {}

    public NoteRequest(String title, String content, String color) {
        this.title   = title;
        this.content = content;
        this.color   = color;
    }

    // Getters
    public String getTitle()   { return title; }
    public String getContent() { return content; }
    public String getColor()   { return color; }

    // Setters
    public void setTitle(String title)     { this.title   = title; }
    public void setContent(String content) { this.content = content; }
    public void setColor(String color)     { this.color   = color; }
}
