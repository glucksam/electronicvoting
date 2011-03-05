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
	private String  I_sFromScan=null;
	private HashMap<String, String> parmters;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		parmters = new HashMap<String, String>();
		/*TODO:un-comment when go to android*/
    	//IntentIntegrator.initiateScan(this);
//		if(I_sFromScan == null){
//			HelpfulMathods.Error(getActivityInstanc(), "BAD_BALLOT");
//		}
    	parmters.put("firstScanReasult", I_sFromScan);
    	I_sFromScan=null;
    	setContentView(R.layout.isaudit);
		Button yesButton=(Button) findViewById(R.id.Yes);
		yesButton.setOnClickListener(new  OnClickListener(){

			@Override
			public void onClick(View v) {
				/*TODO:un-comment when go to android*/
				//IntentIntegrator.initiateScan(getActivityInstanc());
				if(I_sFromScan == null){
					HelpfulMathods.Error(getActivityInstanc(), "BAD_BALLOT");
				}
				parmters.put("secondScanReasult", I_sFromScan);
				HelpfulMathods.moveActivtyWitheParam(Verifying.class, getActivityInstanc(), parmters);
			}
			
		});
		Button noButton= (Button) findViewById(R.id.No);
		noButton.setOnClickListener(new  OnClickListener(){

			@Override
			public void onClick(View v) {
				parmters.put("secondScanReasult","");
				HelpfulMathods.moveActivtyWitheParam(Verifying.class, getActivityInstanc(), parmters);
			}
		});
    }
    
    
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	 IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
    	 //if the scan secced the sFromScan will get the value
    	  if (scanResult != null) {
    		  I_sFromScan=scanResult.getContents();
    	  	}
    	  I_sFromScan=null;
    }
    
    public Activity getActivityInstanc(){
		 return this;
	 }

}