package com.trace4eu.tsaClient.controllers;

import com.trace4eu.tsaClient.dtos.*;
import com.trace4eu.tsaClient.services.TsaRequestGeneratorService;
import com.trace4eu.tsaClient.services.TsaVerifierService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.Operation;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.bouncycastle.cms.CMSException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;

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
    @Operation(
            summary = "Generate a timestamp using a TSA",
            security = @SecurityRequirement(name = "bearerAuth", scopes = {"qtsp:timestamp"})
    )
    @ApiResponse(
            responseCode = "201",
            description = "Timestamp correctly created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TimestampGenerationResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Error during the process",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
    )
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> getTimestamp(Authentication authentication, @RequestBody TimestampGenerationRequest request) {
        try {
            System.out.println("client: " + authentication.getName());
            TimestampGenerationResponse timestampResponse = tsaRequestGeneratorService.requestTimestampToTsa(request.getData());
            return ResponseEntity.status(201).body(timestampResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(new ErrorResponse(400, e.getMessage()));
        }
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify a timestamp token")
    @ApiResponse(
            responseCode = "200",
            description = "Verified timestamp token",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TimestampVerificationResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Error during the process",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
    )
    public ResponseEntity<Object> verifyTimestampToken(
            @RequestBody TimestampVerificationRequest request
    ) {
        try {
            TimestampVerificationResponse timestampVerificationResponse = tsaVerifierService.verifyTimeStampToken(request.getTimestampToken(), request.getOriginalData());
            if (timestampVerificationResponse.getResult()) {
                return ResponseEntity.ok(timestampVerificationResponse);
            } else {
                return ResponseEntity.status(400).body(Map.of("result", false));
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (e.getClass() == CMSException.class) {
                return ResponseEntity.status(400).body(new ErrorResponse(400, "request contains bad values"));
            }
            return ResponseEntity.status(500).body(new ErrorResponse(500, e.getMessage()));
        }
    }
}
