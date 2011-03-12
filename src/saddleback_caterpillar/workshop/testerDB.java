package saddleback_caterpillar.workshop;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import local.bouncycastle.asn1.nist.NISTNamedCurves;
import local.bouncycastle.asn1.x9.X9ECParameters;
import local.bouncycastle.math.ec.ECPoint;
import Exceptions.ReinstallException;
import crypt.ECDSAVerifier;
import crypt.Point;

public class testerDB {
	String sCurveName;
	String sHashName;
	private static testerDB instance = null;

	private class Signature {
		private BigInteger privateKey;
		private Point publicKey;

		public Signature(BigInteger privKey) {
			this.privateKey = privKey;
			X9ECParameters ecParams = NISTNamedCurves.getByName(sCurveName);
			ECPoint pKey = ecParams.getG().multiply(privKey);
			this.publicKey = new Point(pKey.getX().toBigInteger(), pKey.getY()
					.toBigInteger());
		}

		Point getPublicKey() {
			return publicKey;
		}

		BigInteger getPrivateKey() {
			return privateKey;
		}
	}

	private Signature generateRandomSignature() {
		BigInteger privateKey = new BigInteger(64, new SecureRandom());
		return new Signature(privateKey);
	}

	public Boolean signatureTester(String sCurve, String sHash)
			throws ReinstallException, IOException {
		this.sCurveName = sCurve;
		this.sHashName = sHash;
		Signature sig = generateRandomSignature();
		String testStr = barcodeRandomizer();
		Point signed = ECDSAVerifier.signString(testStr, sig.getPrivateKey(),
				this.sCurveName, this.sHashName);
		Random generator = new Random();
		Boolean isSigned = true;
		if (0 == generator.nextInt() % 2) {/*
											 * change signature and make sure it
											 * failes
											 */
			isSigned = false;
			signed.x = signed.x.add(new BigInteger(64, new SecureRandom()));
			signed.y = signed.y.add(new BigInteger(64, new SecureRandom()));
		}

		return (ECDSAVerifier.checkStringVerification(testStr, signed,
				sig.getPublicKey(), this.sCurveName, this.sHashName) == isSigned);
	}

	public static testerDB getInstance() {
		if (null == instance)
			instance = new testerDB();
		return instance;
	}

	static public String barcodeRandomizer() {
		char[] text = new char[1024];
		Random generator = new Random();
		String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789=/";
		int length = generator.nextInt(1024);
		for (int i = 0; i < length; i++) {
			text[i] = characters.charAt(generator.nextInt(characters.length()));
			if ((0 == i % 10) && (0 == generator.nextInt() % 2)) {
				text[++i] = '@';
			}
		}
		return new String(text);
	}
}
