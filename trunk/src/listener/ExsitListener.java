package listener;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;


public class ExsitListener implements OnClickListener {
	private Activity activity;
	public ExsitListener(Activity activity){
		this.activity=activity;
	}
	
	@Override
	public void onClick(View v) {
		activity.finish();
		
	}
	

}
