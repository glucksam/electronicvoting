package saddleback_caterpillar.activity;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.Security;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import local.bouncycastle.util.encoders.Base64;
import saddleback_caterpillar.workshop.BallotVerifier;
import tools.HelpfulMathods;
import tools.Parser;
import tools.Server;
import Exceptions.BadBallotException;
import Exceptions.NoConnectionException;
import Exceptions.ReinstallException;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;
import crypt.ECDSAVerifier;
import crypt.ECParams;
import crypt.Point;
import crypt.SmartCard;
import crypt.Utils;
import crypt.Vote;

public class Verifying extends Activity {

	private ProgressBar mProgress;
	private String first_scan;
	private String second_scan;
	private BallotVerifier bv = null;
	private Boolean isVerified = false;
	private Boolean isCasted = false;
	private String sCandidate = "";
	private String sServerUrl = "";
	private String sSchollID = "";
	private Point pGovernmentVKey;
	private Point pCommitteeVKey;
	private Point pBBVKey;
	private ECParams ecParams;

	private Handler mHandler = new Handler() {

		/*
		 * Error messages: 0 everything went 0K 1 no connection 2 bad ballot 3
		 * reinstall
		 */
		@Override
		public void handleMessage(Message msg) {
			HashMap<String, String> parmters = new HashMap<String, String>();
			String sRes = "";
			switch (msg.what) {
			case 0:
				if (!isVerified) {
					parmters.put("text", "Vote was NOT created correctly :(");
				} else {
					sRes = "Your vote was created correctly";
				}
				if (0 == second_scan.compareTo("")) {
					parmters.put("isAudit", "no");
					parmters.put("titles", "Casting");
					if (!parmters.containsKey("text")) {
						if (isCasted) {
							sRes += " and casted properly";
						} else {
							sRes += ", but not casted properly";
						}
						sRes += "\nschool ID = " + sSchollID + "\n counters- " + bv.getCountersString();
						parmters.put("text", sRes);
					}
				} else {
					parmters.put("isAudit", "yes");
					parmters.put("titles", "Audit result");
					if (!parmters.containsKey("text")) {
						parmters.put(
								"text",
								sRes + "\nschool ID = " + sSchollID + "\nThe candidate is: " + sCandidate
										+ "\ncounters- "
										+ bv.getCountersString());
					}
				}
				mProgress.setVisibility(8);
				HelpfulMathods.moveActivtyWitheParam(Result.class,
						getActivityInstanc(), parmters);
				break;
			case 1:
				HelpfulMathods.Error(getActivityInstanc(), "NO_CONNECTION");
				break;
			case 2:
				HelpfulMathods.Error(getActivityInstanc(), "BAD_BALLOT");
				break;
			case 3:
				HelpfulMathods.Error(getActivityInstanc(), "REINSTALL");
				break;
			default:
				HelpfulMathods.Error(getActivityInstanc(), "UNKNOWN");
				break;
			}
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		mProgress = (ProgressBar) findViewById(R.id.progress_bar);

		new Thread(new Runnable() {
			public void run() {
				Bundle bundle = getActivityInstanc().getIntent().getExtras();
				first_scan = bundle.getString("firstScanReasult");
				second_scan = bundle.getString("secondScanReasult");
				Log.d("WORKSHOP", "input: 1- " + first_scan);
				Log.d("WORKSHOP", "input: 2- " + second_scan);

				Security.addProvider(new local.bouncycastle.jce.provider.BouncyCastleProvider());
				try {
					Log.d("WORKSHOP", "starting try");
					verifyBallot();
				} catch (NoConnectionException e) {
					Log.d("WORKSHOP",
							"NoConnectionException: " + e.getMessage());
					mHandler.sendEmptyMessage(1);
					return;
				} catch (BadBallotException e) {
					Log.d("WORKSHOP", "BadBallotException: " + e.getMessage());
					mHandler.sendEmptyMessage(2);
					return;
				} catch (ReinstallException e) {
					Log.d("WORKSHOP", "ReinstallException: " + e.getMessage());
					mHandler.sendEmptyMessage(3);
					return;
				}
				Log.d("WORKSHOP", "DONE!!!");
				mHandler.sendEmptyMessage(0);
			}
		}).start();
	}

	private void verifyBallot() throws ReinstallException,
			NoConnectionException, BadBallotException {
		List<String> parametersFromFile;
		List<String> sigFromBB = null;

		parametersFromFile = hendleFilesFromApplication(R.raw.parmtersfile, "#");
		if (parametersFromFile.size() < 7) {
			throw new ReinstallException("not enough parameters");
		}
		sServerUrl = parametersFromFile.get(0);
		try {
			pBBVKey = Utils
					.translateStringToPointHex(parametersFromFile.get(5));
			pGovernmentVKey = Utils
					.translateStringToPointHex(parametersFromFile.get(6));
		} catch (IOException e) {
			throw new ReinstallException("could not figure signature: "
					+ e.getMessage());
		}
		ecParams = new ECParams(parametersFromFile.get(4),
				parametersFromFile.get(3), parametersFromFile.get(2),
				parametersFromFile.get(1));

		sigFromBB = getFileFromServer("signature.txt", pGovernmentVKey,
				pBBVKey, "#");
		if (null == sigFromBB || sigFromBB.size() < 3) {
			throw new NoConnectionException("signatures file is too short");
		}
		try {
			pCommitteeVKey = Utils.translateStringToPointHex(sigFromBB.get(0));
		} catch (IOException e) {
			throw new NoConnectionException(
					"could figure our committee signature");
		}
		Log.d("WORKSHOP", "setting ballot");
		setBallot(createBalloVerifier(parametersFromFile, sigFromBB));
		Log.d("WORKSHOP", "done creating ballot");
		isVerified = bv.verify();
		Log.d("WORKSHOP", "done verifying ballot");
		if (0 < bv.getCandidate()) {
			sCandidate = parametersFromFile.get(5 + bv.getCandidate());
		}
		if (bv.getCandidate() < 0) {
			isCasted = isCasted();
		}
	}

	private Activity getActivityInstanc() {
		return this;
	}

	/*
	 * sigFromBB: 0- committee 1- sc1 2 sc2
	 */
	private BallotVerifier createBalloVerifier(List<String> dataBase,
			List<String> sigFromBB) throws ReinstallException,
			BadBallotException {
		Log.d("WORKSHOP", "parsing first scan");
		List<String> ballot1 = Parser.parseString(first_scan, "@");
		BigInteger biAudit1 = null;
		BigInteger biAudit2 = null;
		List<String> ballot2 = null;
		if (ballot1.size() < 9) {
			throw new BadBallotException(
					"ballot is too short, some parameters are missing!");
		}
		if (second_scan.compareTo("") != 0) {
			ballot2 = Parser.parseString(second_scan, "@");
			if (ballot2.size() < 2) {
				throw new BadBallotException("second ballot is currapted!");
			}
			try {
				biAudit1 = new BigInteger(Base64.decode(ballot2.get(0)));
				biAudit2 = new BigInteger(Base64.decode(ballot2.get(1)));
			} catch (Exception e) {
				throw new BadBallotException("second ballot is currapted");
			}
		}
		Log.d("WORKSHOP", "setting school ID");
		sSchollID = ballot1.get(2);
		Pattern p = Pattern.compile("$", Pattern.LITERAL);
		String[] tmp = p.split(ballot1.get(0));
		int ID;
		int iCount;
		try {
			ID = Integer.valueOf(tmp[0]).intValue();
			iCount = Integer.valueOf(tmp[1]).intValue();
		} catch (Exception e) {
			throw new BadBallotException(
					"Could not get smart card ID or counter");
		}
		Log.d("WORKSHOP", "initializing first smart card");
		SmartCard sc1 = new SmartCard(biAudit1, ballot1.get(5),
				sigFromBB.get(1), ballot1.get(6), ID, iCount);
		tmp = p.split(ballot1.get(1));
		try {
			ID = Integer.valueOf(tmp[0]).intValue();
			iCount = Integer.valueOf(tmp[1]).intValue();
		} catch (Exception e) {
			throw new BadBallotException(
					"Could not get smart card ID or counter");
		}
		Log.d("WORKSHOP", "initializing second smart card");
		SmartCard sc2 = new SmartCard(biAudit2, ballot1.get(7),
				sigFromBB.get(2), ballot1.get(8), ID, iCount);
		Log.d("WORKSHOP", "initializing vote");
		Vote vote = new Vote(ballot1.get(3), ballot1.get(4));
		BallotVerifier bv = null;
		bv = new BallotVerifier(sc1, sc2, this.ecParams, vote);
		return bv;
	}

	public BallotVerifier getBallot() {
		return this.bv;
	}

	public void setBallot(BallotVerifier i_bv) {
		this.bv = i_bv;
	}

	private Boolean isCasted() throws ReinstallException, NoConnectionException {
		List<String> sVotes = getFileFromServer("votes.txt", pCommitteeVKey,
				pBBVKey, "#");
		if (sVotes == null) {
			throw new NoConnectionException("votes.txt file was not verified");
		}
		if (sVotes.contains(bv.getVoteString())) {
			return true;
		} else {
			return false;
		}
	}

	private List<String> hendleFilesFromApplication(int fileHandler,
			String delmetor) throws ReinstallException {
		byte[] bRaw = readFileFromApplication(fileHandler);

		if (bRaw.equals(null)) {
			return null;
		} else {
			return parseStringFromfiles(bRaw, delmetor);
		}
	}

	/* returns a null string if something went wrong */
	private byte[] readFileFromApplication(int fileHandler)
			throws ReinstallException {
		InputStream is = null;
		byte[] buffer = null;

		try {
			is = getResources().openRawResource(fileHandler);
			int size = is.available();
			buffer = new byte[size];
			is.read(buffer);
		} catch (IOException e) {
			throw new ReinstallException("local bad file");
		} finally {
			try {
				is.close();
			} catch (IOException e) {

			}
		}
		return buffer;
	}

	/*
	 * This file both gets the file, verifies it & return the list extracted
	 * from it
	 */
	private List<String> getFileFromServer(String fileName, Point pFirstSigKey,
			Point pSecondSigKey, String delimiter) throws ReinstallException,
			NoConnectionException {
		byte[] bRaw = downloadFileAndVerify(fileName, pFirstSigKey,
				pSecondSigKey);
		if (bRaw == null) {
			throw new NoConnectionException("file " + fileName
					+ " is not verified");
		} else {
			parseStringFromfiles(bRaw, delimiter);
			return parseStringFromfiles(bRaw, delimiter);
		}
	}

	/*
	 * if the file does not exists or is not signed, then return a null string
	 * we have 2 signature keys, since a file could be verified by 2 optional
	 * signatures
	 */
	private byte[] downloadFileAndVerify(String fileName, Point pFirstSigKey,
			Point pSecondSigKey) throws ReinstallException,
			NoConnectionException {
		int len = 0;
		try {
			len = Server.getFile(sServerUrl, fileName, getActivityInstanc()
					.getApplicationContext());
		} catch (IOException e) {
			throw new NoConnectionException("server failed to get file: "
					+ fileName);
		}
		if (0 >= len) {
			throw new NoConnectionException(fileName
					+ " does not exists or empty");
		}
		byte[] bFile = readFileFromServer(fileName, len);
		String sigFileName = fileName.substring(0, fileName.indexOf('.'))
				+ ".sig";
		try {
			len = Server.getFile(sServerUrl, sigFileName, getActivityInstanc()
					.getApplicationContext());
		} catch (IOException e) {
			throw new NoConnectionException(sigFileName
					+ " does not exists or empty");
		}
		if (0 >= len) {
			throw new NoConnectionException(sigFileName
					+ " does not exists or empty");
		}

		Point pSigString;
		try {
			pSigString = Utils.translateStringToPointBase64(new String(
					readFileFromServer(sigFileName, len)));
		} catch (IOException e) {
			throw new NoConnectionException("could not parse file signature: "
					+ e.getMessage());
		}
		String sFile = new String(bFile);
		/*
		 * if the file was verified by one of the 2 optional signatures then
		 * it's 0K, otherwise, return null
		 */
		if (ECDSAVerifier.checkStringVerification(sFile, pSigString,
				pFirstSigKey, this.ecParams.sDSACurveName,
				this.ecParams.sDSAHashName)
				|| ECDSAVerifier.checkStringVerification(sFile, pSigString,
						pSecondSigKey, this.ecParams.sDSACurveName,
						this.ecParams.sDSAHashName)) {
			return bFile;
		} else {
			return null;
		}
	}

	private byte[] readFileFromServer(String fileName, int len) {
		FileInputStream fis = null;
		byte[] buffer = null;
		try {
			fis = openFileInput(fileName);
			buffer = new byte[len];
			fis.read(buffer, 0, len);
		} catch (IOException e) {

		} finally {
			try {
				if (null != fis) {
					fis.close();
				}
			} catch (IOException e) {
			}
		}
		return buffer;
	}

	private List<String> parseStringFromfiles(byte[] buffer, String delmetor) {
		String text = new String(buffer);
		List<String> ListParm = Parser.parseString(text, delmetor);
		return ListParm;

	}
}
