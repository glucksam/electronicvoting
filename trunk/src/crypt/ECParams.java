package crypt;

public class ECParams {

	final public String sDSACurveName;
	final public String sDSAHashName;
	final public String sElGamasCurveName;
	final public String sElGamalPublicKey;

	public ECParams(String sDSACurveName, String sDSAHash,
			String sElGamalCurveName, String sElGamalPubKey) {
		this.sDSACurveName = sDSACurveName;
		this.sDSAHashName = sDSAHash;
		this.sElGamalPublicKey = sElGamalPubKey;
		this.sElGamasCurveName = sElGamalCurveName;
	}

	public String toString() {
		return "ECDSACurve parameters:\n" + "curve name: " + this.sDSACurveName
				+ "\nhash name: " + this.sDSAHashName
				+ "\nECElGamal parameters:\n" + "curve name: "
				+ this.sElGamasCurveName + "\npublic key: "
				+ this.sElGamalPublicKey;
	}
}