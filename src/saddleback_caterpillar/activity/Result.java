package saddleback_caterpillar.activity;

import listener.ExitListener;
import listener.goToAntherActivtiyListener;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class Result extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);
		Bundle bundle =getIntent().getExtras();
		String is_Audit = bundle.getString("isAudit");
		TextView tilte =(TextView) findViewById(R.id.title);
		tilte.setText(bundle.getString("titles"));
		TextView text =(TextView) findViewById(R.id.text);
		text.setText(bundle.getString("text"));
		Button newBallotButton=(Button) findViewById(R.id.newBallot);
		Button Exitbutton= (Button) findViewById(R.id.Exit);
		Exitbutton.setOnClickListener(new ExitListener(this));
		if (0==is_Audit.compareTo("No")) {
			newBallotButton.setVisibility(8);
		} else {
			newBallotButton.setOnClickListener(new goToAntherActivtiyListener(ElectronicVoting.class, this));
		}
	}

}
