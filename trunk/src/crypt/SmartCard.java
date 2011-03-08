package crypt;

import java.io.IOException;
import java.math.BigInteger;

import Exceptions.BadBallotException;
import Exceptions.NoConnectionException;
import Exceptions.ReinstallException;

public class SmartCard {
	public final BigInteger biAudit;
	public final String sGInR;
	private final Point pVerificationKey;
	private final Point pSignature;
	public final int scID;
	public final int iCounter;

	public SmartCard(BigInteger biAudit, String sGInR, String sVerificationKey,
			String sSignature, int iID, int iCounter) throws BadBallotException {
		this.biAudit = biAudit;
		this.sGInR = sGInR;
		try {
			this.pVerificationKey = Utils
					.translateStringToPointHex(sVerificationKey);
			this.pSignature = Utils.translateStringToPointBase64(sSignature);
		} catch (IOException e) {
			throw new BadBallotException(
					"could not initialyze signature or verification key: "
							+ e.getMessage());
		}
		this.scID = iID;
		this.iCounter = iCounter;
	}

	/* sign_sc1(g^r1 | sn1) = sign_sc1((x,y)~SC1ID$counter1) */
	public String generateMessage() {
		return this.sGInR + "~" + this.scID + "$" + this.iCounter;
	}

	public Boolean verifySignature(ECParams ecParam) throws ReinstallException,
			NoConnectionException, BadBallotException {
		return ECDSAVerifier.checkStringVerification(this.generateMessage(),
				this.pSignature, this.pVerificationKey, ecParam.sDSACurveName,
				ecParam.sDSAHashName);
	}

	public Boolean verifySignature(String msg, String sig, ECParams ecParam)
			throws ReinstallException, NoConnectionException,
			BadBallotException {
		Point pSig;
		try {
			pSig = Utils.translateStringToPointBase64(sig);
		} catch (IOException e) {
			throw new BadBallotException("could not translate signature: "
					+ e.getMessage());
		}
		return ECDSAVerifier.checkStringVerification(msg, pSig,
				this.pVerificationKey, ecParam.sDSACurveName,
				ecParam.sDSAHashName);
	}

	public String toString() {
		return "SC ID: " + this.scID + "\nSC counter: " + this.iCounter
				+ "\n g^r: " + this.sGInR + "\nverification key: "
				+ this.pVerificationKey.toString() + "\nSignature: "
				+ this.pSignature.toString();
	}
}