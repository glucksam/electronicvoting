package crypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import local.bouncycastle.asn1.nist.NISTNamedCurves;
import local.bouncycastle.asn1.x9.X9ECParameters;
import local.bouncycastle.jce.spec.ECParameterSpec;
import local.bouncycastle.jce.spec.ECPublicKeySpec;
import local.bouncycastle.math.ec.ECPoint;
import android.util.Log;

public class ECDSAVerifier {
	final private PublicKey key;
	private Signature verifier;
	final private String hashName;

	private PublicKey generatePublicKey(Point pPublicKey, String sCurve)
			throws IOException, NoSuchAlgorithmException,
			NoSuchProviderException, InvalidKeySpecException,
			InvalidAlgorithmParameterException {
		X9ECParameters ecParams = NISTNamedCurves.getByName(sCurve);
		ECPoint pPublicPoint = ecParams.getCurve().createPoint(pPublicKey.x,
				pPublicKey.y, false);
		ECParameterSpec spec = new ECParameterSpec(ecParams.getCurve(),
				ecParams.getG(), ecParams.getN());
		ECPublicKeySpec publicSpec = new ECPublicKeySpec(pPublicPoint, spec);
		KeyFactory keyfac = KeyFactory.getInstance("ECDSA", "LOCAL_BC");
		return keyfac.generatePublic(publicSpec);
	}

	public ECDSAVerifier(Point pKey, String sHash, String sCurve)
			throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchProviderException, InvalidKeySpecException, IOException,
			InvalidAlgorithmParameterException {
		this.key = generatePublicKey(pKey, sCurve);
		Log.d("WORKSHOP", "Public key = " + key.toString());
		this.hashName = sHash;
		ECDSAGenerateVerifier();
	}

	private void ECDSAGenerateVerifier() throws NoSuchAlgorithmException,
			NoSuchProviderException, InvalidKeyException {
		this.verifier = Signature.getInstance(hashName, "LOCAL_BC");
		this.verifier.initVerify(this.key);
	}

	public Boolean verify(byte[] message, byte[] signature)
			throws SignatureException, InvalidKeyException {
		Log.d("WORKSHOP", "verifying...");
		this.verifier.update(message);
		Log.d("WORKSHOP", "done update!");
		Boolean verified = false;
		try {
			verified = this.verifier.verify(signature);
		} catch (Exception e) {
			Log.d("WORKSHOP", "Exception: " + e.getMessage());
		}
		Log.d("WORKSHOP", "done verfying!");
		return verified;
	}

	public Boolean verifyFile(byte[] signature, String fileName)
			throws SignatureException, IOException, InvalidKeyException {
		File file = new File(fileName);
		int len;
		StringBuffer strContent = new StringBuffer("");
		FileInputStream fin = null;
		fin = new FileInputStream(file);
		while ((len = fin.read()) != -1)
			strContent.append((char) len);
		fin.close();
		String tmp = strContent.toString();
		return this.verify(tmp.getBytes(), signature);
	}

	public String toString() {
		return this.verifier.toString();
	}
}
