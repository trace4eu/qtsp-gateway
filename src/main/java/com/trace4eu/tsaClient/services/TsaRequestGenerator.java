package com.trace4eu.tsaClient.services;

import com.trace4eu.tsaClient.config.TsaConfigProperties;
import com.trace4eu.tsaClient.domain.TimestampISOformat;
import com.trace4eu.tsaClient.dtos.TimestampDataResponse;
import org.bouncycastle.tsp.*;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

@Service
public class TsaRequestGenerator {

    private final TsaConfigProperties tsaConfigProperties;

    public TsaRequestGenerator(TsaConfigProperties tsaConfigProperties) {
        this.tsaConfigProperties = tsaConfigProperties;
    }

    public TimestampDataResponse requestTimestampToTsa(String data) throws Exception {
        // Generate SHA-256 hash of the input data
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes());

        // Create the timestamp request
        TimeStampRequestGenerator requestGenerator = new TimeStampRequestGenerator();
        requestGenerator.setCertReq(true);
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
        TimestampISOformat timestamp = new TimestampISOformat(timestampTokenBase64);
        return new TimestampDataResponse(timestamp.getValue(), timestampTokenBase64);
    }


    private byte[] sendTsaRequest(byte[] requestBytes) throws Exception {
        URL url = new URL(tsaConfigProperties.getEndpoint());
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
