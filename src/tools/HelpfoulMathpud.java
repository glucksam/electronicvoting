package tools;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class HelpfoulMathpud {
	public  static void moveActivtyWitheParam(Class<?> subActivity,Activity fatherActivity,Map<String,String> parmters){
		Bundle bundle = new Bundle();
		for(String s : parmters.keySet()){
			bundle.putString(s,parmters.get(s));
		}
		Intent newIntent = new Intent(fatherActivity.getApplicationContext(), subActivity);
		newIntent.putExtras(bundle);
		fatherActivity.startActivity(newIntent);
		fatherActivity.finish();
	}
	public static void Error(Activity active) {
		Intent newIntent = new Intent(active.getApplicationContext(), Error.class);
		active.startActivity(newIntent);
		active.finish();
	}	
}
