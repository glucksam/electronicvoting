package crypt;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import local.bouncycastle.asn1.nist.NISTNamedCurves;
import local.bouncycastle.asn1.x9.X9ECParameters;
import local.bouncycastle.math.ec.ECPoint;

public class ECDSAVerifier {
	X9ECParameters ecParams;
	ECPoint pubKey;

	public ECDSAVerifier(Point pKey, String sCurve)
			throws NoSuchAlgorithmException, NoSuchProviderException,
			InvalidKeySpecException {
		ecParams = NISTNamedCurves.getByName(sCurve);
		this.pubKey = ecParams.getCurve().createPoint(pKey.x, pKey.y, false);
	}

	public Boolean verify(byte[] message, BigInteger r, BigInteger s) {
		BigInteger N = ecParams.getN();
		ECPoint generator = ecParams.getG();
		BigInteger biMsg = new BigInteger(message);
		BigInteger u1 = biMsg.multiply(s.modInverse(N)).mod(N);
		BigInteger u2 = r.multiply(s.modInverse(N)).mod(N);

		ECPoint p = generator.multiply(u1).add(pubKey.multiply(u2));

		if (BigInteger.ZERO == p.getX().toBigInteger()
				&& BigInteger.ZERO == p.getY().toBigInteger())
			return false;
		return (r.equals(p.getX().toBigInteger().mod(N)));
	}
}
