package ElectronicVoting.Workshop;

import java.util.List;

import ElectronicVoting.Workshop.Tools.Parser;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ElectronicVoting extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        Parser p =new Parser();
		List<String> l=p.parseString("1#2#3#4#","#");
		   tv.setText("Hello, Android\n");
		for(String s : l){
			tv.append(";"+s);
		       
		}
		 setContentView(tv);
    }
}