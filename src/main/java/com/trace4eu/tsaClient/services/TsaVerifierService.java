package com.trace4eu.tsaClient.services;

import com.trace4eu.tsaClient.domain.TimestampISOformat;
import com.trace4eu.tsaClient.dtos.TimestampVerificationResponse;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.cms.jcajce.JcaSignerInfoVerifierBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.TimeStampTokenInfo;
import org.bouncycastle.util.Store;
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
import java.util.*;

@Service
public class TsaVerifierService {


    public TimestampVerificationResponse verifyTimeStampToken(
            String timeStampTokenBase64,
            String originalData
    ) throws Exception {
        byte[] tokenBytes = Base64.decode(timeStampTokenBase64);
        TimeStampToken timeStampToken = new TimeStampToken(new CMSSignedData(tokenBytes));
        TimeStampTokenInfo tokenInfo = timeStampToken.getTimeStampInfo();

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] originalDataHash = digest.digest(originalData.getBytes());
        byte[] var1 = tokenInfo.getMessageImprintDigest();
        if (!MessageDigest.isEqual(tokenInfo.getMessageImprintDigest(), originalDataHash)) {
            return new TimestampVerificationResponse(false, null);
        }

        List<X509Certificate> x509Certificates = convertToX509Certificates(timeStampToken.getCertificates());
        X509Certificate rootCA = getRootCA(x509Certificates);
        X509Certificate intermediate = getTsaCertificate(x509Certificates, rootCA);

        if (!verifyCertificateChain(x509Certificates.get(0), intermediate, rootCA)) {
            throw new CertificateException("TSA certificate is not trusted.");
        }

        CMSSignedData cmsSignedData = new CMSSignedData(tokenBytes);

        // Get signer information
        SignerInformation signerInfo = cmsSignedData.getSignerInfos().getSigners().iterator().next();

        // Build SignerInformationVerifier using TSA certificate
        SignerInformationVerifier verifier = new JcaSignerInfoVerifierBuilder(
                new JcaDigestCalculatorProviderBuilder().build())
                .build(x509Certificates.get(0));

        // Perform the verification
        Boolean isValid = signerInfo.verify(verifier);
        TimestampISOformat timestamp = new TimestampISOformat(timeStampTokenBase64);
        return new TimestampVerificationResponse(isValid, timestamp.getValue());

    }

    private boolean verifyCertificateChain(X509Certificate tsaCertificate, X509Certificate intermediateCertificate, X509Certificate caCertificate) throws Exception {
        // Set up the trust anchor (CA certificate)
        Set<TrustAnchor> trustAnchors = Collections.singleton(new TrustAnchor(caCertificate, null));
        PKIXParameters params = new PKIXParameters(trustAnchors);
        params.setRevocationEnabled(false); // Disable CRL/OCSP for simplicity here

        // Create the certification path
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        List<X509Certificate> certChain = Arrays.asList(tsaCertificate, intermediateCertificate, caCertificate);
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

    public static List<X509Certificate> convertToX509Certificates(Store<X509CertificateHolder> certificateHolderStore) throws CertificateException {
        List<X509Certificate> certificates = new ArrayList<>();
        JcaX509CertificateConverter certificateConverter = new JcaX509CertificateConverter();

        // Iterate over X509CertificateHolders in the Store
        Collection<X509CertificateHolder> certificateHolders = certificateHolderStore.getMatches(null);
        for (X509CertificateHolder certificateHolder : certificateHolders) {
            // Convert X509CertificateHolder to X509Certificate
            X509Certificate certificate = certificateConverter.getCertificate(certificateHolder);
            certificates.add(certificate);
        }
        return certificates;
    }

    private X509Certificate getRootCA(List<X509Certificate> x509Certificates) throws CertificateException {
        for (X509Certificate x509Certificate : x509Certificates) {
            if(Objects.equals(x509Certificate.getSubjectX500Principal().getName(), x509Certificate.getIssuerX500Principal().getName())) {
                return x509Certificate;
            }
        }
        throw new CertificateException("Root cert not found in tst");
    }

    private X509Certificate getTsaCertificate(List<X509Certificate> x509Certificates, X509Certificate rootCA) throws CertificateException {
        for (X509Certificate x509Certificate : x509Certificates) {
            if(Objects.equals(rootCA.getSubjectX500Principal().getName(), x509Certificate.getIssuerX500Principal().getName()) & !Objects.equals(x509Certificate.getIssuerX500Principal().getName(), x509Certificate.getSubjectX500Principal().getName())) {
                return x509Certificate;
            }
        }
        throw new CertificateException("Intermediate cert not found in tst");
    }
}
