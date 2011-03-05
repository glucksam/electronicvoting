package saddleback_caterpillar.activity;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import local.bouncycastle.util.encoders.Base64;
import saddleback_caterpillar.workshop.BallotVerifier;
import tools.HelpfulMathods;
import tools.Parser;
import tools.Server;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;
import crypt.ECParams;
import crypt.SmartCard;
import crypt.Vote;

public class Verifying extends Activity {

	private ProgressBar mProgress;

	/* TODO: remove when done debuging */
	// private String first_scan;
	// private String second_scan;


	private String first_scan = "1111$336@2222$301@113@(hgFYk9v192wdah1qkCIA6KwmmlMnsMASCAFL14iKIrg=,jAwd+/4nth7wkoBQUAuT2xbf9LSU1FF0MT/L1sE7OZs=):(7QTT5KNsNc7dXeJ4pFzYnso8X99/q3dAzzH0pIiwnfg=,HrfJm/W/TcV3p7Z7qEaKJzUuxHRmQqqMyH7fLR9U9iQ=)@(AMxrkqu8pGWY2tOeH6nG7NJd2YzppHK0tA==,APspsWJk1PkPlSwhEX+NLZ1/bb/J4ge2tg==)@(CWpplNQDw2FQ8c/qZDrHnrkdzRB+qo2dMdSYLx6kchs=,eRViFxjjOKp6UGWQWby69r+bx62I8jHxVnx5ermKuIo=)@(AJmYn0p64t36qmLCSekBZqApV9cqVp+FxA==,YgbhMP5DbyCbgc9/ttZQkpf/L+MIY/wr)@(wqyzZwul53JkCsn57N5imLhcRSieXh8fAioNu/brqO0=,Dg9TxjF2JXEB9Jt0dlzgtrTz9PbwqM9BxhNkqMf9rY8=)@(DcQU9YE/bU7jbF8fidV47tSzeTFm6TSo,CFAIzl1Bcna4rWyjGKtxZt2hT6gZ8qgX)";
	private String second_scan = "Vy/c2VFsBq5ZfOGKWkL+AJDXpfNig0VaCiZtqrNxKhw=@JiQR3fmWPTkwReAHTAoRDDY7C0wTTcl15XHTpK8msoU=";

	private BallotVerifier bv = null;
	private Boolean isVerified = false;
	private Boolean isCasted = false;
	private String sCandidate = "";
	private String sServerUrl = "";

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
						sRes += "\ncounters- " + bv.getCountersString();
						parmters.put("text", sRes);
					}
				} else {
					parmters.put("isAudit", "yes");
					parmters.put("titles", "Audit result");
					if (!parmters.containsKey("text")) {
						parmters.put("text",
								"Vote was created correctly\nThe candidate is: "
										+ sCandidate + "\ncounters- " + bv.getCountersString());
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
				/* TODO: un-comment when done testing */
				// first_scan = bundle.getString("firstScanReasult");
				// second_scan = bundle.getString("secondScanReasult");
				List<String> parametersFromFile;
				parametersFromFile = hendleFilesFromApplication(
						R.raw.parmtersfile, "#");
				if (parametersFromFile.size() < 5) {
					mHandler.sendEmptyMessage(1);
					return;
				}
				sServerUrl = parametersFromFile.get(0);
				List<String> sigFromBB = null;
				int len = 0;
				try {
					len = Server.getFile(sServerUrl, "signature.txt",
							getActivityInstanc().getApplicationContext());
				} catch (IOException e) {
					mHandler.sendEmptyMessage(1);
					return;
				}
				sigFromBB = handleFilesFromServer("signature.txt", len, "#");
				if (null == sigFromBB || sigFromBB.equals("")) {
					mHandler.sendEmptyMessage(1);
					return;
				} else if (parametersFromFile == null
						|| parametersFromFile.equals("")) {
					mHandler.sendEmptyMessage(3);
					return;
				}
				try {
					setBallot(createBalloVerifier(parametersFromFile, sigFromBB));
					isVerified = bv.verify();
					if (0 != bv.getCandidate()) {
						sCandidate = parametersFromFile.get(5 + bv
								.getCandidate());
					}
				} catch (IOException e) {
					mHandler.sendEmptyMessage(2);
					return;
				} catch (GeneralSecurityException e) {
					mHandler.sendEmptyMessage(3);
					return;
				}
				try {
					if (bv.getCandidate() < 0) {
						isCasted = isCasted();
					}
				} catch (IOException e) {
					mHandler.sendEmptyMessage(1);
					return;
				}
				Log.d("WORKSHOP", "DONE!!!");
				mHandler.sendEmptyMessage(0);
			}
		}).start();
	}

	private Activity getActivityInstanc() {
		return this;
	}

	/*
	 * sigFrpmBB 0- committee 1- sc1 2 sc2
	 */
	private BallotVerifier createBalloVerifier(List<String> dataBase,
			List<String> sigFromBB) throws IOException {
		Log.d("WORKSHOP", "start create ballot");
		List<String> ballot1 = Parser.parseString(first_scan, "@");
		Log.d("WORKSHOP", "done parsing");
		BigInteger biAudit1 = null;
		BigInteger biAudit2 = null;
		List<String> ballot2 = null;
		Log.d("WORKSHOP", "before if second scan");
		if (second_scan.compareTo("") != 0) {
			ballot2 = Parser.parseString(second_scan, "@");
			biAudit1 = new BigInteger(Base64.decode(ballot2.get(0)));
			biAudit2 = new BigInteger(Base64.decode(ballot2.get(1)));
		}
		Log.d("WORKSHOP", "after if second scan");
		Pattern p = Pattern.compile("$", Pattern.LITERAL);
		String[] tmp = p.split(ballot1.get(0));
		Parser.parseString(ballot1.get(0), "$");
		int ID = Integer.valueOf(tmp[0]).intValue();
		int iCount = Integer.valueOf(tmp[1]).intValue();
		SmartCard sc1 = new SmartCard(biAudit1, ballot1.get(5),
				sigFromBB.get(1), ballot1.get(6), ID, iCount);
		Log.d("WORKSHOP", "after init sc1");
		tmp = p.split(ballot1.get(1));
		ID = Integer.valueOf(tmp[0]).intValue();
		iCount = Integer.valueOf(tmp[1]).intValue();
		SmartCard sc2 = new SmartCard(biAudit2, ballot1.get(7),
				sigFromBB.get(2), ballot1.get(8), ID, iCount);
		Log.d("WORKSHOP", "after init sc2");
		ECParams ecParams = new ECParams(dataBase.get(4), dataBase.get(3),
				dataBase.get(2), dataBase.get(1));
		Log.d("WORKSHOP", "after init ecParams");
		Vote vote = new Vote(ballot1.get(3), ballot1.get(4));
		Log.d("WORKSHOP", "after init vote");
		BallotVerifier bv = null;
		bv = new BallotVerifier(sc1, sc2, ecParams, vote);
		return bv;
	}

	public BallotVerifier getBallot() {
		return this.bv;
	}

	public void setBallot(BallotVerifier i_bv) {
		this.bv = i_bv;
	}

	private Boolean isCasted() throws IOException {
		/* download file */
		int len = 0;
		List<String> sVotes = null;
		len = Server.getFile(sServerUrl, "votes.txt", getActivityInstanc()
				.getApplicationContext());
		if (len <= 0) {
			throw new IOException("empty file");
		} else {
			sVotes = handleFilesFromServer("votes.txt", len, "#");
			/* look for the vote string in file */
			if (sVotes.contains(bv.getVoteString())) {
				return true;
			} else {
				return false;
			}
		}
	}

	private List<String> hendleFilesFromApplication(int fileHandler,
			String delmetor) {
		List<String> ListParm = null;
		InputStream is = null;
		byte[] buffer = null;

		try {
			is = getResources().openRawResource(fileHandler);
			int size = is.available();
			buffer = new byte[size];
			is.read(buffer);
			ListParm = parseStringFromfiles(buffer, delmetor);
		} catch (IOException e) {

		} finally {
			try {
				is.close();
			} catch (IOException e) {
				ListParm = null;
			}
		}
		return ListParm;
	}

	private List<String> handleFilesFromServer(String fileName, int len,
			String delmetor) {
		List<String> ListParm = null;
		FileInputStream fis = null;
		try {
			fis = openFileInput(fileName);
			byte[] buffer = new byte[len];
			fis.read(buffer, 0, len);
			ListParm = parseStringFromfiles(buffer, delmetor);
		} catch (IOException e) {

		} finally {
			try {
				if (null != fis) {
					fis.close();
				}
			} catch (IOException e) {
				ListParm = null;
			}
		}
		return ListParm;
	}

	private List<String> parseStringFromfiles(byte[] buffer, String delmetor) {
		String text = new String(buffer);
		List<String> ListParm = Parser.parseString(text, delmetor);
		return ListParm;

	}
}