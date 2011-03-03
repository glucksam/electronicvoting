package saddleback_caterpillar.activity;

import listener.ExsitListener;
import listener.goToAntherActivtiyListener;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class Reasult extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resolt);
		Bundle bundle =getIntent().getExtras();
		String is_Audit = bundle.getString("isAoudit");
		TextView tilte =(TextView) findViewById(R.id.title);
		tilte.setText(bundle.getString("tiltes"));
		TextView text =(TextView) findViewById(R.id.text);
		text.setText(bundle.getString("text"));
		Button newBallotButton=(Button) findViewById(R.id.newBallot);
		Button Exitbutton= (Button) findViewById(R.id.Exit);
		Exitbutton.setOnClickListener(new ExsitListener(this));
		if (0==is_Audit.compareTo("No")) {
			newBallotButton.setVisibility(8);
		} else {
			newBallotButton.setOnClickListener(new goToAntherActivtiyListener(ElectronicVoting.class, this));
		}
	}

}
