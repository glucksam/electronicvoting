package listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class goToAntherActivtiyListener implements OnClickListener{
	private Class<?> class1;
	private Activity fatherActivity;
	
	public goToAntherActivtiyListener(Class<?> class1, Activity fatherActivity) {
		this.class1=class1;
		this.fatherActivity=fatherActivity;
	}
	@Override
	public void onClick(View arg0) {
		Intent newIntent = new Intent(fatherActivity.getApplicationContext(), class1);
		fatherActivity.startActivity(newIntent);
		fatherActivity.finish();
		
	}

}
