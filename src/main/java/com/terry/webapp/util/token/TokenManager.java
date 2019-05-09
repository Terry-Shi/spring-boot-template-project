package com.terry.webapp.util.token;

import java.security.SecureRandom;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.terry.webapp.common.exception.AppException;

/**
 * 所有nimbusds相关的api能在此类中调用，隔绝jwt的工具类选型带来的影响
 */
@Component
public class TokenManager {
    private static final Logger logger = LoggerFactory.getLogger(TokenManager.class);
    public static final String USER_ID = "uid";
    public static final String WRAPPED_TOKEN = "wtk";

    public static final String ISSUER = "gateway";
    private static final String algorithm = "RSA"; // HMAC|RSA
    private byte[] sharedSecret; // only for MACSigner

    private RSAKeyPairReader rsaKeyPairReader = new RSAKeyPairReader();

    private JWSSigner signer;
    private JWSVerifier verifier;

    @PostConstruct
    public void init() throws JOSEException {
        // Generate random 256-bit (32 bytes) shared secret for HMAC algorithm
        SecureRandom random = new SecureRandom();
        sharedSecret = new byte[32];
        random.nextBytes(sharedSecret);
        // TODO: for test 使用固定的字符串产生byte数组，至少32位
        sharedSecret = ("1234567890"+"1234567890"+"1234567890"+"12") .getBytes();

        verifier = createVerifier();
        signer = createSigner();
    }

    private JWSVerifier createVerifier() throws JOSEException {
        if ("RSA".equals(algorithm)) {
            return new RSASSAVerifier(rsaKeyPairReader.readPublicKey("keys/public_key.der"));
        } else {
            return new MACVerifier(sharedSecret);
        }

    }

    public Token decodeToken(String jwtToken) {
        if (jwtToken == null) {
            return null;
        }
        Token token = new Token();
        try {
            SignedJWT signedJWT = SignedJWT.parse(jwtToken);
            boolean verified = signedJWT.verify(verifier);
            token.setValid(verified);
            if (verified) {
                JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
                token.setUserId((String) claimsSet.getClaim(USER_ID));
                token.setWrappedToken((String) claimsSet.getClaim(WRAPPED_TOKEN));
                token.setExpirationTime(claimsSet.getExpirationTime());
            }
        } catch (ParseException | JOSEException e) {
            logger.error("Failed to verify token: {}", jwtToken, e);
        }
        return token;
    }

    public String extractToken(String header) {
        if (header == null) {
            return null;
        }
        return header.length() > "Bearer ".length() ? header.substring("Bearer ".length()) : null;
    }

    public String generateToken(String userId, Date expiredDate) {
        try {
            // Prepare JWT with claims set
            // JWT time claim precision is seconds
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .claim(USER_ID, userId)
                    .issuer(ISSUER)
                    .expirationTime(expiredDate)
                    .build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader("RSA".equals(algorithm) ? JWSAlgorithm.RS256 : JWSAlgorithm.HS256), claimsSet);
            signedJWT.sign(signer);

            // Serialize to compact form, produces something like
            // eyJhbGciOiJIUzI1NiJ9.SGVsbG8sIHdvcmxkIQ.onO9Ihudz3WkiauDO2Uhyuz0Y18UASXlSc1eS0NkWyA
            return signedJWT.serialize();
        } catch (JOSEException e) {
            logger.error("failed to generateToken for user: {}", userId, e);
            throw new AppException("failed to generateToken for user: " + userId);
        }
    }

    private JWSSigner createSigner() {
        if ("RSA".equals(algorithm)) {
            return createRSASignerFromFile();
        } else {
            return createHMACSigner();
        }
    }

    private JWSSigner createHMACSigner() {
        try {
            return new MACSigner(sharedSecret);
        } catch (KeyLengthException e) {
            logger.error("failed to createHMACSigner", e);
            throw new AppException("failed to createHMACSigner.");
        }
    }

    private JWSSigner createRSASignerFromFile() {
        return new RSASSASigner(rsaKeyPairReader.readPrivateKey("keys/private_key.der"));
    }
    
    public static void main(String[] args) throws JOSEException {
        TokenManager tokenManager = new TokenManager();
        tokenManager.init();
         // token 的有效时间可以配置
        Instant expiredTime = Instant.now().plus(3650, ChronoUnit.DAYS);
        Instant refreshTokenExpiredTime = Instant.now().plus(3650, ChronoUnit.DAYS);
        String token = tokenManager.generateToken("admin", Date.from(expiredTime));
        String refreshToken = tokenManager.generateToken("admin", Date.from(refreshTokenExpiredTime));
        System.out.println(token);
        System.out.println(refreshToken);
    }
}
