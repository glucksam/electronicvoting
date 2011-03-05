package listener;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;


public class ExitListener implements OnClickListener {
	private Activity activity;
	public ExitListener(Activity activity){
		this.activity=activity;
	}
	
	@Override
	public void onClick(View v) {
		activity.finish();
		
	}
	

}
