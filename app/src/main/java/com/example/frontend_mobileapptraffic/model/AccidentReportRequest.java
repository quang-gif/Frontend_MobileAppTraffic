package com.example.frontend_mobileapptraffic.model;

public class AccidentReportRequest {
    private long accidentPostId;
    private String reason;

    public AccidentReportRequest(long accidentPostId, String reason) {
        this.accidentPostId = accidentPostId;
        this.reason = reason;
    }

    public long getAccidentPostId() {
        return accidentPostId;
    }

    public void setAccidentPostId(long accidentPostId) {
        this.accidentPostId = accidentPostId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
