package com.trace4eu.tsaClient.dtos;

public class TimestampDataResponse {
    private String timestampToken;
    private String timestamp;

    public TimestampDataResponse(String timestamp, String timestampToken) {
        this.timestamp = timestamp;
        this.timestampToken = timestampToken;
    }

    public String getTimestampToken() {
        return timestampToken;
    }

    public void setTimestampToken(String timestampToken) {
        this.timestampToken = timestampToken;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
