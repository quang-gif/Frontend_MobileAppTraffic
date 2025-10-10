package com.example.frontend_mobileapptraffic.violation;

import java.util.List;

public class Violation {
    public String licensePlate;
    public String plateColor;
    public String vehicleType;
    public String violationTime;
    public String violationLocation;
    public String violationBehavior;
    public String status;
    public String detectedBy;
    public List<String> paymentPlaces;

    public String getStatus() {
        return status;
    }
}
