package com.example.frontend_mobileapptraffic;

public class KeyProvider {
    // Load native library
    static {
        System.loadLibrary("native-keys");
    }

    // Native method declaration
    public native String getOpenCageApiKey();
}