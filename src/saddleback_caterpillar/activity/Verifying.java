package saddleback_caterpillar.activity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import local.bouncycastle.util.encoders.Base64;
import saddleback_caterpillar.workshop.BallotVerifier;
import tools.HelpfoulMathpud;
import tools.Parser;
import tools.Server;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.ProgressBar;
import crypt.ECParams;
import crypt.SmartCard;
import crypt.Vote;

public class Verifying extends Activity {

	private ProgressBar mProgress;

	// private String first_scan;
	// private String second_scan;

	private String first_scan = "1111$336@2222$301@113@(hgFYk9v192wdah1qkCIA6KwmmlMnsMASCAFL14iKIrg=,jAwd+/4nth7wkoBQUAuT2xbf9LSU1FF0MT/L1sE7OZs=):(7QTT5KNsNc7dXeJ4pFzYnso8X99/q3dAzzH0pIiwnfg=,HrfJm/W/TcV3p7Z7qEaKJzUuxHRmQqqMyH7fLR9U9iQ=)@(AMxrkqu8pGWY2tOeH6nG7NJd2YzppHK0tA==,APspsWJk1PkPlSwhEX+NLZ1/bb/J4ge2tg==)@(CWpplNQDw2FQ8c/qZDrHnrkdzRB+qo2dMdSYLx6kchs=,eRViFxjjOKp6UGWQWby69r+bx62I8jHxVnx5ermKuIo=)@(AJmYn0p64t36qmLCSekBZqApV9cqVp+FxA==,YgbhMP5DbyCbgc9/ttZQkpf/L+MIY/wr)@(wqyzZwul53JkCsn57N5imLhcRSieXh8fAioNu/brqO0=,Dg9TxjF2JXEB9Jt0dlzgtrTz9PbwqM9BxhNkqMf9rY8=)@(DcQU9YE/bU7jbF8fidV47tSzeTFm6TSo,CFAIzl1Bcna4rWyjGKtxZt2hT6gZ8qgX)";
	private String second_scan = "Vy/c2VFsBq5ZfOGKWkL+AJDXpfNig0VaCiZtqrNxKhw=@JiQR3fmWPTkwReAHTAoRDDY7C0wTTcl15XHTpK8msoU=";

	private BallotVerifier bv = null;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			HashMap<String, String> parmters = new HashMap<String, String>();
			if (0 == second_scan.compareTo("")) {
				parmters.put("isAoudit", "no");
				parmters.put("tiltes", "Costing");
				parmters.put("text",
						"Vote is created correctly.\nThe serial numberrs are\n");
			} else {
				parmters.put("is Aoudit", "yes");
				parmters.put("tiltes", "Audit resalt");
				parmters.put("text", "The vote is.\nThe serial numberrs are\n");
			}
			mProgress.setVisibility(8);
			HelpfoulMathpud.moveActivtyWitheParam(Reasult.class,
					getActivityInstanc(), parmters);
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		mProgress = (ProgressBar) findViewById(R.id.progress_bar);

		new Thread(new Runnable() {
			public void run() {
				Log.d("WORKSHOP", "run 1");
				Bundle bundle = getActivityInstanc().getIntent().getExtras();
				// first_scan = bundle.getString("firstScanReasult");
				// second_scan = bundle.getString("secondScanReasult");
				Log.d("WORKSHOP", "run 2");
				List<String> parmtersFromFIle = hendlFielsFromApplication(
						R.raw.parmtersfile, "#");
				List<String> sigFromBB = null;

				try {
					Log.d("WORKSHOP", "run 3");
					Log.d("WORKSHOP", parmtersFromFIle.get(0)
							+ "/signature.txt");
					int len = Server.getFile(parmtersFromFIle.get(0),
							"signature.txt", getActivityInstanc()
									.getApplicationContext());
					Log.d("WORKSHOP", "run 3.5");
					sigFromBB = handleFielsFromServer("signature.txt", len, "#");
					for (String s : sigFromBB) {
						Log.d("WORKSHOP", s + "\n");
					}
					Log.d("WORKSHOP", "run 4");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (null == sigFromBB || null == parmtersFromFIle) {
					HelpfoulMathpud.Error(getActivityInstanc());
				}
				setBallot(createBalloVerifier(parmtersFromFIle, sigFromBB));
				if (null == bv) {
					HelpfoulMathpud.Error(getActivityInstanc());
				}
				Log.d("WORKSHOP", bv.toString());
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
			List<String> sigFromBB) {
		List<String> ballot1 = Parser.parseString(first_scan, "@");
		BigInteger biAudit1 = null;
		BigInteger biAudit2 = null;
		List<String> ballot2 = null;
		Log.d("WORKSHOP", "run 5");
		if (second_scan.compareTo("") != 0) {
			ballot2 = Parser.parseString(second_scan, "@");
			biAudit1 = new BigInteger(Base64.decode(ballot2.get(0)));
			biAudit2 = new BigInteger(Base64.decode(ballot2.get(1)));
		}

		Pattern p = Pattern.compile("$", Pattern.LITERAL);
		String[] tmp = p.split(ballot1.get(0));
		Parser.parseString(ballot1.get(0), "$");
		int ID = Integer.valueOf(tmp[0]).intValue();
		int iCount = Integer.valueOf(tmp[1]).intValue();
		SmartCard sc1 = new SmartCard(biAudit1, ballot1.get(5),
				sigFromBB.get(1), ballot1.get(6), ID, iCount);

		tmp = p.split(ballot1.get(1));
		ID = Integer.valueOf(tmp[0]).intValue();
		iCount = Integer.valueOf(tmp[1]).intValue();
		SmartCard sc2 = new SmartCard(biAudit2, ballot1.get(7),
				sigFromBB.get(2), ballot1.get(8), ID, iCount);
		Log.d("WORKSHOP", "run 6");
		ECParams ecParams = new ECParams(dataBase.get(4), dataBase.get(3),
				dataBase.get(2), dataBase.get(1));
		Vote vote = new Vote(ballot1.get(3), ballot1.get(4));
		BallotVerifier bv = null;
		Log.d("WORKSHOP", "run 7");
		try {
			bv = new BallotVerifier(sc1, sc2, ecParams, vote);
		} catch (IOException e) {
			Log.d("WORKSHOP", "EXCEPTION!!!" + e.getMessage());
		}

		return bv;
	}

	public BallotVerifier getBallot() {
		return this.bv;
	}

	public void setBallot(BallotVerifier i_bv) {
		this.bv = i_bv;
	}

	private List<String> hendlFielsFromApplication(int fileHandler,
			String delmetor) {
		List<String> ListParm;
		InputStream is = null;
		byte[] buffer = null;

		try {
			is = getResources().openRawResource(fileHandler);
			int size = is.available();
			buffer = new byte[size];
			is.read(buffer);
			ListParm = parrsStringFromfiles(buffer, delmetor);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ListParm;
	}

	private List<String> handleFielsFromServer(String fileName, int len,
			String delmetor) {
		List<String> ListParm = null;
		FileInputStream fis = null;
		try {
			Log.d("WORKSHOP", "run 3.1");
			fis = openFileInput(fileName);
			Log.d("WORKSHOP", "run 3.2");
			byte[] buffer = new byte[len];
			Log.d("WORKSHOP", "run 3.4");
			fis.read(buffer, 0, len);
			Log.d("WORKSHOP", "run 3.6");
			ListParm = parrsStringFromfiles(buffer, delmetor);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (null != fis) {
					fis.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ListParm;
	}

	private List<String> parrsStringFromfiles(byte[] buffer, String delmetor) {
		String text = new String(buffer);
		List<String> ListParm = Parser.parseString(text, delmetor);
		return ListParm;

	}
}
