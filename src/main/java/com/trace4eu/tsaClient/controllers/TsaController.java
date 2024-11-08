package com.trace4eu.tsaClient.controllers;

import com.trace4eu.tsaClient.dtos.TimestampDataRequest;
import com.trace4eu.tsaClient.dtos.TimestampDataResponse;
import com.trace4eu.tsaClient.dtos.TimestampVerificationResponse;
import com.trace4eu.tsaClient.services.TsaRequestGenerator;
import com.trace4eu.tsaClient.services.TsaVerifierService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TsaController {

    private final TsaVerifierService tsaVerifierService;
    private final TsaRequestGenerator tsaRequestGenerator;

    public TsaController(TsaVerifierService tsaVerifierService, TsaRequestGenerator tsaRequestGenerator) {
        this.tsaVerifierService = tsaVerifierService;
        this.tsaRequestGenerator = tsaRequestGenerator;
    }

    @GetMapping("/timestamp")
    public ResponseEntity<Object> getTimestamp(@RequestBody String data) {
        try {
            TimestampDataResponse timestampResponse = tsaRequestGenerator.requestTimestampToTsa(data);
            return ResponseEntity.ok(timestampResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Object> verifyTimestampToken(
            @RequestBody TimestampDataRequest request
    ) {
        try {
            // Verify the timestamp token
            TimestampVerificationResponse timestampVerificationResponse = tsaVerifierService.verifyTimeStampToken(request.getTimestampToken(), request.getOriginalData());
            if (timestampVerificationResponse.getResult()) {
                return ResponseEntity.ok(timestampVerificationResponse);
            } else {
                return ResponseEntity.status(400).body(Map.of("result", false));
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
