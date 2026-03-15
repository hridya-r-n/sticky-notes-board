package com.stickynotes.model;

public class NoteRequest {

    private String title;
    private String content;
    private String color;

    // Default constructor for JSON deserialization
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
