package com.trace4eu.tsaClient.dtos;

public class TimestampVerificationResponse {
    private Boolean result;
    private String timestamp;

    public TimestampVerificationResponse(Boolean result, String timestamp) {
        this.result = result;
        this.timestamp = timestamp;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
