package crypt;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import local.bouncycastle.asn1.nist.NISTNamedCurves;
import local.bouncycastle.asn1.x9.X9ECParameters;
import local.bouncycastle.math.ec.ECPoint;
import Exceptions.ReinstallException;

public class ECDSAVerifier {
	X9ECParameters ecParams;
	ECPoint pubKey;
	BigInteger privKey;

	private ECDSAVerifier(Point pKey, String sCurve)
			throws NoSuchAlgorithmException, NoSuchProviderException,
			InvalidKeySpecException {
		ecParams = NISTNamedCurves.getByName(sCurve);
		this.pubKey = ecParams.getCurve().createPoint(pKey.x, pKey.y, false);
	}

	private ECDSAVerifier(BigInteger biKey, String sCurve)
			throws NoSuchAlgorithmException, NoSuchProviderException,
			InvalidKeySpecException {
		ecParams = NISTNamedCurves.getByName(sCurve);
		this.privKey = biKey;
	}

	private Boolean isKosherValue(BigInteger N, BigInteger x) {
		if (x.equals(x.mod(N))) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * Reject the signature if either 0 < r <q or 0 < s < q is not satisfied.
	 * Calculate w = (s)^−1 mod q Calculate u1 = (H(m)*w) mod q Calculate u2 =
	 * (r*w) mod q Calculate v = ((gu1*yu2) mod p) mod q The signature is valid
	 * if v = r
	 */
	private Boolean verify(BigInteger message, BigInteger r, BigInteger s) {
		BigInteger N = ecParams.getN();
		ECPoint p;
		if (!isKosherValue(N, r) || !isKosherValue(N, s)) {
			return false;
		} else {
			ECPoint generator = ecParams.getG();
			/*Calculate w = (s)^−1 mod q Calculate u1 = (H(m)*w) mod q*/
			BigInteger u1 = message.multiply(s.modInverse(N)).mod(N);
			/*Calculate u2 = (r*w) mod q*/
			BigInteger u2 = r.multiply(s.modInverse(N)).mod(N);
			/*Calculate v = ((g^u1*yu2) mod p) mod q*/
			p = generator.multiply(u1).add(pubKey.multiply(u2));
			if (BigInteger.ZERO == p.getX().toBigInteger()
					&& BigInteger.ZERO == p.getY().toBigInteger()) {
				return false;
			}
		}
		return (r.equals(p.getX().toBigInteger().mod(N)));
	}

	/*
	 * Generate a random per-message value k where 0 < k < q Calculate r = (gk
	 * mod p) mod q Calculate s = (k−1(H(m) + x*r)) mod q Recalculate the
	 * signature in the unlikely case that r = 0 or s = 0 The signature is (r,
	 * s)
	 */
	public String sign(BigInteger message) {
		BigInteger r = BigInteger.ZERO;
		BigInteger s = BigInteger.ZERO;
		BigInteger N = this.ecParams.getN();
		BigInteger k;
		while (r.equals(BigInteger.ZERO) || s.equals(BigInteger.ZERO)) {
			/* random per-message value k where 0 < k < q */
			k = new BigInteger(64, new SecureRandom());
			k = k.mod(N.subtract(BigInteger.ONE)).add(BigInteger.ONE);
			/* Calculate r = (g^k mod p) mod q */
			r = ecParams.getG().multiply(k).getX().toBigInteger().mod(N);
			if (r.equals(BigInteger.ZERO)) {
				continue;
			}
			/* Calculate s = (k^(−1)(H(m) + x*r)) mod q */
			s = (k.modInverse(N).multiply(message.add(privKey.multiply(r))))
					.mod(N);
		}
		return Utils.translatePointToStringBase64(new Point(r,s));
	}

	public static String signString(String strToSign, BigInteger privKey,
			String curveName, String hashName) throws ReinstallException {
		ECDSAVerifier verifier;
		try {
			verifier = new ECDSAVerifier(privKey, curveName);
		} catch (GeneralSecurityException e) {
			throw new ReinstallException(
					"could not create wanted signing environment");
		}

		BigInteger message;
		try {
			message = Utils.getHash(strToSign, hashName);
		} catch (GeneralSecurityException e) {
			throw new ReinstallException("signString could not create hash: "
					+ e.getMessage());
		} catch (UnsupportedEncodingException e) {
			throw new ReinstallException(
					"checkStringVerification could not create hash: "
							+ e.getMessage());
		}
		return verifier.sign(message);
	}

	public static Boolean checkStringVerification(String strTooVefiry,
			Point pSig, Point pKey, String curveName, String hashName)
			throws ReinstallException {
		ECDSAVerifier verifier;
		try {
			verifier = new ECDSAVerifier(pKey, curveName);
		} catch (GeneralSecurityException e) {
			throw new ReinstallException(
					"checkStringVerification failed initializing verifier: "
							+ e.getMessage());
		}

		BigInteger message;
		try {
			message = Utils.getHash(strTooVefiry, hashName);
		} catch (GeneralSecurityException e) {
			throw new ReinstallException(
					"checkStringVerification could not create hash: "
							+ e.getMessage());
		} catch (UnsupportedEncodingException e) {
			throw new ReinstallException(
					"checkStringVerification could not create hash: "
							+ e.getMessage());
		}
		return verifier.verify(message, pSig.x, pSig.y);
	}
}
