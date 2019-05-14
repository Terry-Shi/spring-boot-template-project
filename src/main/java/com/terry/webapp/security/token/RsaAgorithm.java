package com.terry.webapp.security.token;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;

public class RsaAgorithm extends TokenAlgorithm{

    private RSAKeyPairReader rsaKeyPairReader = new RSAKeyPairReader();

    private String publicKeyFile = "keys/public_key.der";
    private String privateKeyFile = "keys/private_key.der";
    
    public void setPublicKeyFile(String publicKeyFile) {
    	this.publicKeyFile = publicKeyFile;
    }
    
    public void setPrivateKeyFile(String privateKeyFile) {
    	this.privateKeyFile = privateKeyFile;
    }
    
    
	@Override
	public void init() {
		// TODO Auto-generated method stub
		verifier = new RSASSAVerifier(rsaKeyPairReader.readPublicKey(publicKeyFile));
		signer = new RSASSASigner(rsaKeyPairReader.readPrivateKey(privateKeyFile));
	}

	@Override
	public JWSAlgorithm getJWSAlgorithm() {
		return JWSAlgorithm.RS256;
	}

}
