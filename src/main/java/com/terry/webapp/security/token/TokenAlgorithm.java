package com.terry.webapp.security.token;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;

/**
 * JWT中签名和验证用algorithm
 * @author Terry
 *
 */
public abstract class TokenAlgorithm {
	
    protected JWSSigner signer;
    protected JWSVerifier verifier;	
	/**
	 * 初始化singer和verifier
	 */
    abstract public void init();
    
    abstract public JWSAlgorithm getJWSAlgorithm(); 
	
	public JWSVerifier getVerifier() {
		return verifier;
	}
	
     public JWSSigner getSigner() {
		return signer;
	}
     
}
