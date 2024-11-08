package com.trace4eu.tsaClient.domain;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.TimeStampTokenInfo;
import org.bouncycastle.util.encoders.Base64;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimestampISOformat {
    private final String timestamp;

    // ISO 8601 date format
    public TimestampISOformat(String timeStampTokenBase64) throws TSPException, IOException, CMSException {
        byte[] tokenBytes = Base64.decode(timeStampTokenBase64);
        TimeStampToken timeStampToken = new TimeStampToken(new CMSSignedData(tokenBytes));
        TimeStampTokenInfo tokenInfo = timeStampToken.getTimeStampInfo();
        Date genTime = tokenInfo.getGenTime();
        this.timestamp = DateTimeFormatter.ISO_INSTANT.format(genTime.toInstant());
    }

    public String getValue() {
        return timestamp;
    }
}
