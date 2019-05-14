package com.terry.webapp.security.token;

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
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.terry.webapp.common.exception.AppException;

/**
 * 所有nimbusds相关的api能在此类和TokenAlgorithm中调用，隔绝jwt的工具类选型带来的影响
 */
@Component
public class TokenManager {
    private static final Logger logger = LoggerFactory.getLogger(TokenManager.class);
    
    public static final String USER_NAME = "uid";
    public static final String WRAPPED_TOKEN = "wtk";
    public static final String ISSUER = "gateway";
    // TODO: 
    private static final ALGORITHM algorithm = ALGORITHM.RSA; // HMAC|RSA
    public static enum ALGORITHM {
    	RSA, HMAC
    }
    
    private TokenAlgorithm tokenAlgorithm;
    
    @PostConstruct
    public void init() throws JOSEException {
        if (algorithm ==  ALGORITHM.RSA) {
        	tokenAlgorithm = new RsaAgorithm();
        } else {
            // Generate random 256-bit (32 bytes) shared secret for HMAC algorithm
            SecureRandom random = new SecureRandom();
            byte[] sharedSecret = new byte[32];
            random.nextBytes(sharedSecret);
            // TODO: for test 使用固定的字符串产生byte数组，至少32位
            sharedSecret = ("1234567890"+"1234567890"+"1234567890"+"12") .getBytes();
        	
        	tokenAlgorithm = new HmacAlgorithm();
        }
    }

    public Token decodeToken(String jwtToken) {
        if (jwtToken == null) {
            return null;
        }
        Token token = new Token();
        try {
            SignedJWT signedJWT = SignedJWT.parse(jwtToken);
            boolean verified = signedJWT.verify(tokenAlgorithm.getVerifier());
            token.setValid(verified);
            if (verified) {
                JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
                token.setUsername((String) claimsSet.getClaim(USER_NAME));
//                token.setWrappedToken((String) claimsSet.getClaim(WRAPPED_TOKEN));
                token.setExpirationTime(claimsSet.getExpirationTime());
            }
        } catch (ParseException | JOSEException e) {
            logger.error("Failed to verify token: {}", jwtToken, e);
            throw new AppException("Failed to verify token: " + jwtToken, e);
        }
        return token;
    }

    public String extractToken(String header) {
        if (header == null) {
            return null;
        }
        return header.length() > "Bearer ".length() ? header.substring("Bearer ".length()) : null;
    }

    public String generateToken(String username, Date expiredDate) {
        try {
            // Prepare JWT with claims set
            // JWT time claim precision is seconds
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .claim(USER_NAME, username)
                    .issuer(ISSUER)
                    .expirationTime(expiredDate)
                    .build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(tokenAlgorithm.getJWSAlgorithm()), claimsSet);
            signedJWT.sign(tokenAlgorithm.getSigner());

            // Serialize to compact form, produces something like
            // eyJhbGciOiJIUzI1NiJ9.SGVsbG8sIHdvcmxkIQ.onO9Ihudz3WkiauDO2Uhyuz0Y18UASXlSc1eS0NkWyA
            return signedJWT.serialize();
        } catch (JOSEException e) {
            logger.error("failed to generateToken for user: {}", username, e);
            throw new AppException("failed to generateToken for user: " + username, e);
        }
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
