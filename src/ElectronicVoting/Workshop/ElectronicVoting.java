package ElectronicVoting.Workshop;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import ElectronicVoting.Workshop.Tools.*;

public class ElectronicVoting extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);
    	 IntentIntegrator.initiateScan(this);
    }
}