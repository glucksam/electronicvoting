package crypt;

import java.io.IOException;
import java.math.BigInteger;

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
		p.x = new BigInteger(1, Base64.decode(point[0]));
		p.y = new BigInteger(1, Base64.decode(point[1]));
		return p;
	}

	public static Point translateStringToPointHex(String sRaw) {
		Point p = new Point();
		String sTmp = sRaw.replace(")", "");
		sTmp = sTmp.replace("(", "");
		String[] point = sTmp.split(",");
		p.x = new BigInteger(1, Hex.decode(point[0]));
		p.y = new BigInteger(1, Hex.decode(point[1]));
		return p;
	}
}
