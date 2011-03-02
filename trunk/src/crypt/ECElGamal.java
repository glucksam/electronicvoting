package crypt;

import java.math.BigInteger;
import java.security.SecureRandom;

import local.bouncycastle.asn1.nist.NISTNamedCurves;
import local.bouncycastle.asn1.x9.X9ECParameters;
import local.bouncycastle.math.ec.ECPoint;

public class ECElGamal {

	final private X9ECParameters ecParams;
	final private ECPoint pubKey;
	final private BigInteger privKey;
	final private String sCurve;

	public ECElGamal(String encCurveName) {
		sCurve = encCurveName;
		this.ecParams = NISTNamedCurves.getByName(sCurve);
		this.privKey = new BigInteger(16, new SecureRandom());
		this.pubKey = this.ecParams.getG().multiply(this.privKey);
	}

	public ECElGamal(String encCurveName, Point pubKey) {
		sCurve = encCurveName;
		this.ecParams = NISTNamedCurves.getByName(sCurve);
		this.privKey = null;
		this.pubKey = this.createECPoint(pubKey);
	}

	/*
	 * methods that need the private key, will return "null" in case of second
	 * constructor
	 */
	public encryptedData encrypt(ECPoint pMsg) {
		if (null == this.privKey) {
			ECPoint x;
			ECPoint y;
			BigInteger random = new BigInteger(16, new SecureRandom());
			/* g^r */
			x = this.ecParams.getG().multiply(random);
			/* pk^r+m */
			y = pMsg.add(this.pubKey.multiply(random));
			return new encryptedData(x, y);
		} else {
			return null;
		}
	}

	public ECPoint decrypt(encryptedData edM) {
		/* b-a^sk */
		if (null == this.privKey) {
			return edM.eData.subtract(edM.gInR.multiply(this.privKey));
		} else {
			return null;
		}
	}

	public ECPoint calculateCandidate(ECPoint pMsg, BigInteger random) {
		/* b/(pk^r) */
		return pMsg.subtract(this.pubKey.multiply(random));
	}

	public ECPoint createECPoint(Point p) {
		return ecParams.getCurve().createPoint(p.x, p.y, false);
	}

	/* return g^pow */
	public ECPoint createECPoint(int pow) {
		return this.createECPoint(BigInteger.valueOf(pow));
	}

	public ECPoint createECPoint(BigInteger pow) {
		return this.ecParams.getG().multiply(pow);
	}

	public String toString() {
		return "ECElGamal:\n curve name: " + sCurve;
	}

	public String printCurveParameters() {
		return "generator: " + ecParams.getG().getX().toBigInteger().toString()
				+ "\n" + ecParams.getG().getY().toBigInteger().toString()
				+ "\nn: " + ecParams.getN().toString();
	}
}
