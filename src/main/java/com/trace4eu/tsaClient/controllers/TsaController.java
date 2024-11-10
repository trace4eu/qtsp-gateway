package com.trace4eu.tsaClient.controllers;

import com.trace4eu.tsaClient.dtos.TimestampGenerationRequest;
import com.trace4eu.tsaClient.dtos.TimestampVerificationRequest;
import com.trace4eu.tsaClient.dtos.TimestampGenerationResponse;
import com.trace4eu.tsaClient.dtos.TimestampVerificationResponse;
import com.trace4eu.tsaClient.services.TsaRequestGeneratorService;
import com.trace4eu.tsaClient.services.TsaVerifierService;
import org.bouncycastle.cms.CMSException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
public class TsaController {

    private final TsaVerifierService tsaVerifierService;
    private final TsaRequestGeneratorService tsaRequestGeneratorService;

    public TsaController(TsaVerifierService tsaVerifierService, TsaRequestGeneratorService tsaRequestGeneratorService) {
        this.tsaVerifierService = tsaVerifierService;
        this.tsaRequestGeneratorService = tsaRequestGeneratorService;
    }

    @PostMapping("/timestamp")
    public ResponseEntity<Object> getTimestamp(@RequestBody TimestampGenerationRequest request) {
        try {
            TimestampGenerationResponse timestampResponse = tsaRequestGeneratorService.requestTimestampToTsa(request.getData());
            return ResponseEntity.ok(timestampResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Object> verifyTimestampToken(
            @RequestBody TimestampVerificationRequest request
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
            e.printStackTrace();
            if (e.getClass() == CMSException.class) {
                return ResponseEntity.status(400).body(Map.of("error", "request contains bad values"));
            }
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
