package com.example.frontend_mobileapptraffic.model;

public class AccidentPostRequest {
    private String content;
    private String location;
    private String createdAt;

    public AccidentPostRequest() {
    }

    public AccidentPostRequest(String content, String location, String createdAt) {
        this.content = content;
        this.location = location;
        this.createdAt = createdAt;
    }

    public String getContent() {
        return content;
    }

    public String getLocation() {
        return location;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
