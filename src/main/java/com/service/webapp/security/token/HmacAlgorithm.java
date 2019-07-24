package com.service.webapp.security.token;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.service.webapp.common.exception.AppException;

public class HmacAlgorithm extends TokenAlgorithm {

    private byte[] secret; // only for MACSigner
	
    public void setSecret(byte[] secret) {
    	this.secret = secret;
    }
    
    public void setSecret(String secretString) {
    	this.secret = secretString.getBytes();
    }
    
    
	@Override
	public void init() {
		try {
			this.verifier = new MACVerifier(secret);
			this.signer = new MACSigner(secret);
		} catch (JOSEException e) {
			throw new AppException("Failed to init verifier or signer", e);
		}
	}

	@Override
	public JWSAlgorithm getJWSAlgorithm() {
		return JWSAlgorithm.HS256;
	}

}
