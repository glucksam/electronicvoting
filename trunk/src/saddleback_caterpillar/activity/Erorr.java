package saddleback_caterpillar.activity;

import listener.ExsitListener;
import listener.goToAntherActivtiyListener;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class Erorr extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.error);
		Bundle bundle =getIntent().getExtras();
		TextView problemTextView =(TextView) findViewById(R.id.textView);
		problemTextView.setText(bundle.getString("problem"));
		Button Backbutton=(Button) findViewById(R.id.Back);
		Backbutton.setOnClickListener(new goToAntherActivtiyListener(ElectronicVoting.class, this));
		Button Exitbutton= (Button) findViewById(R.id.Exit);
		Exitbutton.setOnClickListener(new ExsitListener(this));
	}
}
