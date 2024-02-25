package com.example.namespace;

public class Session {
    private String title;
    private String date;
    private String time;

    public Session() {
        // Empty constructor required for Firestore
    }

    public Session(String title, String date, String time) {
        this.title = title;
        this.date = date;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
