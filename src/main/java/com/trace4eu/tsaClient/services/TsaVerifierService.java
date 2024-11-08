package com.trace4eu.tsaClient.services;

import com.trace4eu.tsaClient.config.TsaConfigProperties;
import com.trace4eu.tsaClient.domain.TimestampISOformat;
import com.trace4eu.tsaClient.dtos.TimestampVerificationResponse;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.cms.jcajce.JcaSignerInfoVerifierBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.TimeStampTokenInfo;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Collections;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Set;

@Service
public class TsaVerifierService {

    private final TsaConfigProperties tsaConfigProperties;

    public TsaVerifierService(TsaConfigProperties tsaConfigProperties) {
        this.tsaConfigProperties = tsaConfigProperties;
    }

    public TimestampVerificationResponse verifyTimeStampToken(
            String timeStampTokenBase64,
            String originalData
    ) throws Exception {
        X509Certificate tsaCertificate = loadCertificateFromString(this.tsaConfigProperties.getCert());
        X509Certificate caCertificate = loadCertificateFromString(this.tsaConfigProperties.getCaCert());

        byte[] tokenBytes = Base64.decode(timeStampTokenBase64);
        TimeStampToken timeStampToken = new TimeStampToken(new CMSSignedData(tokenBytes));
        TimeStampTokenInfo tokenInfo = timeStampToken.getTimeStampInfo();

        // Hash original data and compare with token imprint
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] originalDataHash = digest.digest(originalData.getBytes());
        if (!MessageDigest.isEqual(tokenInfo.getMessageImprintDigest(), originalDataHash)) {
            return new TimestampVerificationResponse(false, null);
        }

        // Verify the certificate chain: TSA -> CA
        if (!verifyCertificateChain(tsaCertificate, caCertificate)) {
            throw new CertificateException("TSA certificate is not trusted.");
        }

        CMSSignedData cmsSignedData = new CMSSignedData(tokenBytes);

        // Get signer information
        SignerInformation signerInfo = cmsSignedData.getSignerInfos().getSigners().iterator().next();

        // Build SignerInformationVerifier using TSA certificate
        SignerInformationVerifier verifier = new JcaSignerInfoVerifierBuilder(
                new JcaDigestCalculatorProviderBuilder().build())
                .build(tsaCertificate);

        // Perform the verification
        Boolean isValid = signerInfo.verify(verifier);
        TimestampISOformat timestamp = new TimestampISOformat(timeStampTokenBase64);
        return new TimestampVerificationResponse(isValid, timestamp.toString());

    }

    private boolean verifyCertificateChain(X509Certificate tsaCertificate, X509Certificate caCertificate) throws Exception {
        // Set up the trust anchor (CA certificate)
        Set<TrustAnchor> trustAnchors = Collections.singleton(new TrustAnchor(caCertificate, null));
        PKIXParameters params = new PKIXParameters(trustAnchors);
        params.setRevocationEnabled(false); // Disable CRL/OCSP for simplicity here

        // Create the certification path
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        List<X509Certificate> certChain = Arrays.asList(tsaCertificate, caCertificate);
        CertPath certPath = certFactory.generateCertPath(certChain);

        // Validate the certificate path
        CertPathValidator certPathValidator = CertPathValidator.getInstance("PKIX");
        try {
            certPathValidator.validate(certPath, params);
            return true;
        } catch (CertPathValidatorException e) {
            return false; // Chain is not trusted
        }
    }

    private X509Certificate loadCertificateFromString(String certString) throws CertificateException {
        String cleanCert = certString
                .replace("-----BEGIN CERTIFICATE-----", "")
                .replace("-----END CERTIFICATE-----", "")
                .replaceAll("\\s+", "");
        byte[] certBytes = java.util.Base64.getDecoder().decode(cleanCert);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(certBytes);
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) certificateFactory.generateCertificate(inputStream);
    }
}
