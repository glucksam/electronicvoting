package saddleback_caterpillar.workshop;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import local.bouncycastle.math.ec.ECPoint;
import crypt.ECElGamal;
import crypt.ECParams;
import crypt.SmartCard;
import crypt.Utils;
import crypt.Vote;
import crypt.encryptedData;

public class BallotVerifier {

	final public int iCandidateNum = 25;

	private ECElGamal elGamalCurve;
	private ECParams ecParam;
	private Vote vote;
	private SmartCard sc1;
	private SmartCard sc2;

	/* for casting constructor- put null in both biAudit1 & biAudit2 */
	public BallotVerifier(SmartCard sc1, SmartCard sc2, ECParams ecParams,
			Vote vote) throws IOException {
		this.ecParam = ecParams;
		initElGamalCurve();
		this.vote = vote;
		initVote();
		this.sc1 = sc1;
		this.sc2 = sc2;
	}

	private void initElGamalCurve(){
		elGamalCurve = new ECElGamal(ecParam.sElGamasCurveName,
				Utils.translateStringToPointHex(ecParam.sElGamalPublicKey));
	}

	private void initVote() throws IOException {
		String[] vote = this.vote.sVote.split(":");
		ECPoint ecGInr = elGamalCurve.createECPoint(Utils
				.translateStringToPointBase64(vote[0]));
		ECPoint votePoint = elGamalCurve.createECPoint(Utils
				.translateStringToPointBase64(vote[1]));
		this.vote.edVote = new encryptedData(ecGInr, votePoint);
	}

	private Boolean verifyECElGamal() throws IOException {
		ECPoint gInr1 = elGamalCurve.createECPoint(Utils
				.translateStringToPointBase64(sc1.sGInR));
		ECPoint gInr2 = elGamalCurve.createECPoint(Utils
				.translateStringToPointBase64(sc2.sGInR));
		if (Utils.compareECpoints(gInr1.add(gInr2), this.vote.edVote.gInR)) {
			return true;
		} else {
			return false;
		}
	}

	public int getCandidate() {
		if (sc1.biAudit == null || sc2.biAudit == null) {
			return -1;
		}
		return Utils.getCandidate(iCandidateNum, elGamalCurve,
				sc1.biAudit.add(sc2.biAudit), this.vote.edVote.eData);
	}

	private Boolean verifySignatures() throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchProviderException,
			InvalidAlgorithmParameterException, InvalidKeySpecException,
			IOException, SignatureException {
		String voteString = vote.sVote + "~" + sc1.scID + "$" + sc1.iCounter + "~"
				+ sc2.scID + "$" + sc2.iCounter;
		if(sc2.verifySignature(ecParam) && sc1.verifySignature(ecParam) &&
				sc2.verifySignature(voteString, vote.sSignature,ecParam)){
			return true;
		}else{
			return false;
		}
	}

	public Boolean verify() throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchProviderException,
			InvalidAlgorithmParameterException, InvalidKeySpecException,
			SignatureException, IOException {
		return (verifyECElGamal() && verifySignatures());
	}

	public String toString() {
		String cs1_str = this.sc1.toString();
		String cs2_str = this.sc2.toString();
		String ecParams_str = this.ecParam.toString();
		return "cs1:\n" + cs1_str + "\ncs2:\n" + cs2_str + "\n"
				+ ecParams_str.toString() + "\nvote: " + this.vote.toString();
	}

	/* SC1ID~SC1Counter@SC2ID~SC2Counter@vote */
	public String getVoteString() {
		return sc1.scID + "$" + sc1.iCounter + "@" + sc2.scID + "$"
				+ sc2.iCounter + "@" + vote.sVote;
	}

	public String getCountersString() {
		return "sc1: " + sc1.iCounter + ", sc2: " + sc2.iCounter;
	}
}
