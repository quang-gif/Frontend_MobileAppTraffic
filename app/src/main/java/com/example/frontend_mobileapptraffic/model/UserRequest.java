package com.example.frontend_mobileapptraffic.model;

public class UserRequest {
    private String email;
    private String password;
    private String userName;

    public UserRequest(String email, String password, String userName) {
        this.email = email;
        this.password = password;
        this.userName = userName;
    }
}
