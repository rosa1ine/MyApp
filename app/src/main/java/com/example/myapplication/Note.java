package com.example.myapplication;

import com.google.firebase.Timestamp;

public class Note {
    private String title;
    private String content;
    private Timestamp timestamp;
    private Timestamp deadline;

    public Note() {
        // Default constructor required for calls to DataSnapshot.getValue(Note.class)
    }

    public Timestamp getDeadline() {
        return deadline;
    }

    public void setDeadline(Timestamp deadline) {
        this.deadline = deadline;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
