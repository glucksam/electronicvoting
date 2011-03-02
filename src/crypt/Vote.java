package crypt;

public class Vote {
	final public String sVote;
	final public String sSignature;
	public encryptedData edVote;

	public Vote(String sVote, String sSignature) {
		this.sVote = sVote;
		this.sSignature = sSignature;
	}
	
	public String toString(){
		return "vote: " + sVote + "\nsignature: " + sSignature;
	}
}
