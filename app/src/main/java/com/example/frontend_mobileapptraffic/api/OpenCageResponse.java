package com.example.frontend_mobileapptraffic.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OpenCageResponse {
    @SerializedName("results")
    public List<Result> results;

    public static class Result {
        @SerializedName("formatted")
        public String formatted;

        @SerializedName("geometry")
        public Geometry geometry;
    }

    public static class Geometry {
        @SerializedName("lat")
        public double lat;

        @SerializedName("lng")
        public double lng;
    }
}