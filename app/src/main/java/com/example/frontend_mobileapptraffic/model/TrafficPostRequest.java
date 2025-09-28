package com.example.frontend_mobileapptraffic.model;

public class TrafficPostRequest {
    private String content;
    private String location;
    private String timestamp;

    public TrafficPostRequest() {
    }

    public TrafficPostRequest(String content, String location, String timestamp) {
        this.content = content;
        this.location = location;
        this.timestamp = timestamp;
    }

    // Getter & Setter
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
