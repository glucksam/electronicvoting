package saddleback_caterpillar.activity;

import java.util.HashMap;

import tools.HelpfulMathods;
import tools.IntentIntegrator;
import tools.IntentResult;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ElectronicVoting extends Activity {
	private String I_sFromScan = null;
	private HashMap<String, String> parameters;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parameters = new HashMap<String, String>();
		IntentIntegrator.initiateScan(this);	
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		if (scanResult == null) {
			HelpfulMathods.Error(getActivityInstanc(), "BAD_BALLOT");
			return;
		}else if(scanResult.getContents() == null){/*need to install barcode scanner*/
			/*TODO: take care of this case- when teh scanner is not installed*/
			return;
		}
		else {
			I_sFromScan = scanResult.getContents();
		}
		if (!parameters.containsKey("firstScanReasult")) {/* it's the first scan */
			parameters.put("firstScanReasult", I_sFromScan);
			setContentView(R.layout.isaudit);
			Button yesButton = (Button) findViewById(R.id.Yes);
			yesButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					IntentIntegrator.initiateScan(getActivityInstanc());
				}
			});
			Button noButton = (Button) findViewById(R.id.No);
			noButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					parameters.put("secondScanReasult", "");
					HelpfulMathods.moveActivtyWitheParam(Verifying.class,
							getActivityInstanc(), parameters);
				}
			});
		} else {/* second scan */
			parameters.put("secondScanReasult", I_sFromScan);
			HelpfulMathods.moveActivtyWitheParam(Verifying.class,
					getActivityInstanc(), parameters);
		}
	}

	public Activity getActivityInstanc() {
		return this;
	}

}