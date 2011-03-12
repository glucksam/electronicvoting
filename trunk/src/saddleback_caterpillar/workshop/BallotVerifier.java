package saddleback_caterpillar.workshop;

import java.io.IOException;

import local.bouncycastle.math.ec.ECPoint;
import Exceptions.BadBallotException;
import Exceptions.NoConnectionException;
import Exceptions.ReinstallException;
import android.util.Log;
import crypt.ECElGamal;
import crypt.ECParams;
import crypt.SmartCard;
import crypt.Utils;
import crypt.Vote;
import crypt.encryptedData;

public class BallotVerifier {

	final public int iCandidateNum = 25;

	private final ECElGamal elGamalCurve;
	private final ECParams ecParam;
	private final Vote vote;
	private final SmartCard sc1;
	private final SmartCard sc2;

	/* for casting constructor- put null in both biAudit1 & biAudit2 */
	public BallotVerifier(SmartCard i_sc1, SmartCard i_sc2,
			ECParams i_ecParams, Vote i_vote) throws BadBallotException,
			ReinstallException {
		this.ecParam = i_ecParams;
		try {
			elGamalCurve = new ECElGamal(ecParam.sElGamasCurveName,
					Utils.translateStringToPointHex(ecParam.sElGamalPublicKey));
		} catch (IOException e) {
			throw new ReinstallException(e.getMessage());
		}
		this.vote = i_vote;
		initVote();
		this.sc1 = i_sc1;
		this.sc2 = i_sc2;
	}

	private void initVote() throws BadBallotException {
		String[] vote = this.vote.sVote.split(":");
		ECPoint ecGInr;
		ECPoint votePoint;
		try {
			ecGInr = elGamalCurve.createECPoint(Utils
					.translateStringToPointBase64(vote[0]));
			votePoint = elGamalCurve.createECPoint(Utils
					.translateStringToPointBase64(vote[1]));
		} catch (IOException e) {
			throw new BadBallotException("could not initialize vote");
		}
		this.vote.edVote = new encryptedData(ecGInr, votePoint);
	}

	private Boolean verifyECElGamal() throws BadBallotException {
		ECPoint gInr1;
		ECPoint gInr2;
		try {
			gInr1 = elGamalCurve.createECPoint(Utils
					.translateStringToPointBase64(sc1.sGInR));
			gInr2 = elGamalCurve.createECPoint(Utils
					.translateStringToPointBase64(sc2.sGInR));
		} catch (IOException e) {
			throw new BadBallotException("could not verify elGamal");
		}
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

	private Boolean verifySignatures() throws BadBallotException,
			ReinstallException, NoConnectionException {
		String voteString = vote.sVote + "~" + sc1.scID + "$" + sc1.iCounter
				+ "~" + sc2.scID + "$" + sc2.iCounter;
		
		return (sc2.verifySignature(ecParam) && sc1.verifySignature(ecParam)
				&& sc2.verifySignature(voteString, vote.sSignature, ecParam));
	}

	public Boolean verify() throws BadBallotException, ReinstallException,
			NoConnectionException {
		if(!verifyECElGamal()){
			Log.d("WORKSHOP", "could not verify elgamal");
			return false;
		}else if(!verifySignatures()){
			Log.d("WORKSHOP", "could not verify singnatures");
			return false;
		}
		return true;
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

	public ECParams getECParams() {
		return ecParam;
	}
}
