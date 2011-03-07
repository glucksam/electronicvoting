package crypt;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

public class SmartCard {
	public final BigInteger biAudit;
	public final String sGInR;
	private final String sVerificationKey;
	private final String sSignature;
	public final int scID;
	public final int iCounter;

	public SmartCard(BigInteger biAudit, String sGInR, String sVerificationKey,
			String sSignature, int iID, int iCounter) {
		this.biAudit = biAudit;
		this.sGInR = sGInR;
		this.sVerificationKey = sVerificationKey;
		this.sSignature = sSignature;
		this.scID = iID;
		this.iCounter = iCounter;
	}

	/* sign_sc1(g^r1 | sn1) = sign_sc1((x,y)~SC1ID$counter1) */
	public String generateMessage() throws IOException {
		return this.sGInR + "~" + this.scID + "$" + this.iCounter;
	}

	public Boolean verifySignature(ECParams ecParam)
			throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchProviderException, InvalidKeySpecException, IOException,
			SignatureException, InvalidAlgorithmParameterException {
		return verifySignature(this.generateMessage(), this.sSignature, ecParam);
	}

	public Boolean verifySignature(String msg, String sig, ECParams ecParam)
			throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchProviderException, InvalidKeySpecException, IOException,
			SignatureException, InvalidAlgorithmParameterException {
		ECDSAVerifier verifier = new ECDSAVerifier(
				Utils.translateStringToPointHex(sVerificationKey),
				ecParam.sDSACurveName);
		Point pSig = Utils.translateStringToPointBase64(sig);
		byte[] message = Utils.getSHA(msg, ecParam.sDSAHashName);
		return verifier.verify(message, pSig.x, pSig.y);
	}

	public String toString() {
		return "SC ID: " + this.scID + "\nSC counter: " + this.iCounter
				+ "\n g^r: " + this.sGInR + "\nverification key: "
				+ this.sVerificationKey + "\nSignature: " + this.sSignature;
	}
}