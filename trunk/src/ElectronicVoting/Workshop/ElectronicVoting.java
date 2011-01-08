package ElectronicVoting.Workshop;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import ElectronicVoting.Workshop.Tools.*;

public class ElectronicVoting extends Activity {
	String  I_sFromScan=null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);
    	 IntentIntegrator.initiateScan(this);
    }
    
    /**
     * Handling a scan resolt 
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	 IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
    	 //if the scan secced the sFromScan will get the value
    	  if (scanResult != null) {
    		  I_sFromScan=scanResult.getContents();
    	  	}
    	  //else sFromScan= null
    	  I_sFromScan=null;
    	   
    }
}