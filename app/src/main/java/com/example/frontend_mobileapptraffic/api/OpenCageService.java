package com.example.frontend_mobileapptraffic.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenCageService {
    @GET("geocode/v1/json")
    Call<OpenCageResponse> searchAddress(
            @Query("q") String query,
            @Query("key") String apiKey,
            @Query("countrycode") String countryCode,
            @Query("limit") int limit
    );
}