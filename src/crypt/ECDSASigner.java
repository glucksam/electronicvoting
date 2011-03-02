package crypt;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;

public class ECDSASigner {
	final private PrivateKey key;
	private Signature signer;
	final private String hashName;

	public ECDSASigner(PrivateKey privateKey, String sHash) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException {
		this.key = privateKey;
		this.hashName = sHash;
		ECDSAGenerateSigner();
	}

	private void ECDSAGenerateSigner() throws NoSuchAlgorithmException,
			NoSuchProviderException, InvalidKeyException {
		this.signer = Signature.getInstance(hashName, "LOCAL_BC");
		this.signer.initSign(this.key, new SecureRandom());
	}

	public byte[] sign(byte[] message) throws SignatureException,
			InvalidKeyException, NoSuchAlgorithmException,
			NoSuchProviderException {
		this.signer.update(message);
		return this.signer.sign();
	}

	@Override
	public String toString() {
		return this.signer.toString();
	}
}
