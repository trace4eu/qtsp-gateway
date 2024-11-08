package com.trace4eu.tsaClient.dtos;

public class TimestampDataRequest {
    private String originalData;
    private String timestampToken;

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