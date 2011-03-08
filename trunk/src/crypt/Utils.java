package crypt;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import local.bouncycastle.math.ec.ECPoint;
import local.bouncycastle.util.encoders.Base64;
import local.bouncycastle.util.encoders.Hex;

public class Utils {

	public static int getCandidate(int iCandidateCount, ECElGamal elCurve,
			BigInteger random, ECPoint vote) {
		ECPoint ecCand;
		ECPoint ecVote = elCurve.calculateCandidate(vote, random);

		for (int i = 2; i <= iCandidateCount; i++) {
			ecCand = elCurve.createECPoint(i);
			if (compareECpoints(ecCand, ecVote)) {
				return i;
			}
		}
		/* no candidate was found a match false vote!!! */
		return 0;
	}

	public static Boolean compareECpoints(ECPoint p, ECPoint q) {
		if (!p.getCurve().equals(q.getCurve())) {
			return false;
		}
		if (p.isCompressed() != q.isCompressed()) {
			return false;
		}
		if (BigInteger.ZERO.equals(p.getX().toBigInteger()
				.subtract(q.getX().toBigInteger()))) {
			if (BigInteger.ZERO.equals(p.getY().toBigInteger()
					.subtract(q.getY().toBigInteger()))) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static Point translateStringToPointBase64(String sRaw)
			throws IOException {
		Point p = new Point();
		String sTmp = sRaw.replace(")", "");
		sTmp = sTmp.replace("(", "");
		String[] point = sTmp.split(",");
		if (point.length > 1) {
			p.x = new BigInteger(1, Base64.decode(point[0]));
			p.y = new BigInteger(1, Base64.decode(point[1]));
			return p;
		} else {
			throw new IOException("could not parse string");
		}
	}

	public static String translatePointToStringBase64(Point p){
		String x = new String(Base64.encode(p.x.toByteArray()));
		String y = new String(Base64.encode(p.y.toByteArray()));
		return "(" + x + "," + y + ")";
	}

	public static Point translateStringToPointHex(String sRaw)
			throws IOException {
		Point p = new Point();
		String sTmp = sRaw.replace(")", "");
		sTmp = sTmp.replace("(", "");
		String[] point = sTmp.split(",");
		if (point.length > 1) {
			p.x = new BigInteger(1, Hex.decode(point[0]));
			p.y = new BigInteger(1, Hex.decode(point[1]));
			return p;
		} else {
			throw new IOException("could not parse string: " + sRaw);
		}
	}

	public static BigInteger getHash(String data, String hashType)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest hash = MessageDigest.getInstance(hashType);
		hash.reset();
		hash.update(data.getBytes());
		byte[] bDigest = hash.digest();
		return new BigInteger(1, bDigest);
	}
}
