package com.trace4eu.tsaClient.services;

import com.trace4eu.tsaClient.config.TsaConfigProperties;
import com.trace4eu.tsaClient.controllers.TsaController;
import com.trace4eu.tsaClient.domain.TimestampISOformat;
import com.trace4eu.tsaClient.dtos.TimestampGenerationResponse;
import org.bouncycastle.tsp.*;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

@Service
public class TsaRequestGeneratorService {

    private final TsaConfigProperties tsaConfigProperties;
    private static final Logger logger = LoggerFactory.getLogger(TsaController.class);

    public TsaRequestGeneratorService(TsaConfigProperties tsaConfigProperties) {
        this.tsaConfigProperties = tsaConfigProperties;
    }

    public TimestampGenerationResponse requestTimestampToTsa(String data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes());

        TimeStampRequestGenerator requestGenerator = new TimeStampRequestGenerator();
        requestGenerator.setCertReq(true);
        TimeStampRequest request = requestGenerator.generate(
                TSPAlgorithms.SHA256, hash, BigInteger.valueOf(System.currentTimeMillis()));

        byte[] responseBytes = sendTsaRequest(request.getEncoded());

        TimeStampResponse timeStampResponse = new TimeStampResponse(responseBytes);
        timeStampResponse.validate(request);

        TimeStampToken timeStampToken = timeStampResponse.getTimeStampToken();
        String timestampTokenBase64 = Base64.toBase64String(timeStampToken.getEncoded());
        TimestampISOformat timestamp = new TimestampISOformat(timestampTokenBase64);
        return new TimestampGenerationResponse(timestamp.getValue(), timestampTokenBase64);
    }


    private byte[] sendTsaRequest(byte[] requestBytes) throws Exception {
        HttpURLConnection connection = getHttpURLConnection(requestBytes);
        connection.getOutputStream().write(requestBytes);
        try (InputStream responseStream = connection.getInputStream()) {
            return responseStream.readAllBytes();
        } catch (Exception e) {
            InputStream responseErrorStream = connection.getErrorStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(responseErrorStream));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            logger.error(response.toString());
            throw e;
        }
    }

    private HttpURLConnection getHttpURLConnection(byte[] requestBytes) throws IOException {
        URL url = new URL(tsaConfigProperties.getEndpoint());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/timestamp-query");
        connection.setRequestProperty("Content-Length", Integer.toString(requestBytes.length));

        if (tsaConfigProperties.getAuthentication() != null) {
            connection.setRequestProperty("Authorization", "Basic " + tsaConfigProperties.getAuthentication());
        }
        return connection;
    }
}
