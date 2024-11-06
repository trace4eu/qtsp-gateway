package com.trace4eu.tsaClient;

public class TimestampRequest {
    private String originalData;
    private String timestampToken;

    // Getters and Setters
    public String getOriginalData() {
        return originalData;
    }

    public void setOriginalData(String originalData) {
        this.originalData = originalData;
    }

    public String getTimestampToken() {
        return timestampToken;
    }

    public void setTimestampToken(String timestampToken) {
        this.timestampToken = timestampToken;
    }
}