package com.trace4eu.tsaClient;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.io.FileInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.tsp.*;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TsaController {

    // Replace with your TSA endpoint URL
    private static final String TSA_URL = "https://freetsa.org/tsr";

    private final TsaVerifierService tsaVerifierService;

    public TsaController(TsaVerifierService tsaVerifierService) {
        this.tsaVerifierService = tsaVerifierService;
    }

    @GetMapping("/get-timestamp")
    public ResponseEntity<Map<String, String>> getTimestamp(@RequestParam String data) {
        try {
            // Generate SHA-256 hash of the input data
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes());

            // Create the timestamp request
            TimeStampRequestGenerator requestGenerator = new TimeStampRequestGenerator();
            requestGenerator.setCertReq(true); // Request TSA's certificate
            TimeStampRequest request = requestGenerator.generate(
                    TSPAlgorithms.SHA256, hash, BigInteger.valueOf(System.currentTimeMillis()));

            // Send the request to the TSA
            byte[] responseBytes = sendTsaRequest(request.getEncoded());

            // Parse and validate the TSA response
            TimeStampResponse timeStampResponse = new TimeStampResponse(responseBytes);
            timeStampResponse.validate(request);

            // Extract the timestamp token
            TimeStampToken timeStampToken = timeStampResponse.getTimeStampToken();
            String timestampTokenBase64 = Base64.toBase64String(timeStampToken.getEncoded());
            String timestamp = timeStampToken.getTimeStampInfo().getGenTime().toString();

            // Prepare the response map
            Map<String, String> response = new HashMap<>();
            response.put("timestamp", timestamp);
            response.put("timestampToken", timestampTokenBase64);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyTimestampToken(
            @RequestBody TimestampRequest request
    ) {
        try {
            // Verify the timestamp token
            boolean isValid = tsaVerifierService.verifyTimeStampToken(request.getTimestampToken(), request.getOriginalData());
            if (isValid) {
                return ResponseEntity.ok("Timestamp token is valid.");
            } else {
                return ResponseEntity.status(400).body("Invalid timestamp token.");
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error during verification: " + e.getMessage());
        }
    }

    private byte[] sendTsaRequest(byte[] requestBytes) throws Exception {
        URL url = new URL(TSA_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/timestamp-query");
        connection.setRequestProperty("Content-Length", Integer.toString(requestBytes.length));
        connection.getOutputStream().write(requestBytes);

        try (InputStream responseStream = connection.getInputStream()) {
            return responseStream.readAllBytes();
        }
    }
}
