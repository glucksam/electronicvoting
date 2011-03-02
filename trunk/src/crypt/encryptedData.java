package crypt;

import local.bouncycastle.math.ec.ECPoint;

public class encryptedData {
	
	public final ECPoint gInR;
	public final ECPoint eData;

	public encryptedData(ECPoint gInR, ECPoint eData) {
		this.gInR = gInR;
		this.eData = eData;
	}
}
