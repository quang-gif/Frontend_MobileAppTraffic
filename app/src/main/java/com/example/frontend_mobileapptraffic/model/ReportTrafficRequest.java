package com.example.frontend_mobileapptraffic.model;

public class ReportTrafficRequest {
    private long trafficPostId;
    private String reason;

    public ReportTrafficRequest(long trafficPostId, String reason) {
        this.trafficPostId = trafficPostId;
        this.reason = reason;
    }

    public long getTrafficPostId() {
        return trafficPostId;
    }

    public void setTrafficPostId(long trafficPostId) {
        this.trafficPostId = trafficPostId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
