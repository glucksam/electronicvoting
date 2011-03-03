package tools;

import java.util.HashMap;
import java.util.Map;

import saddleback_caterpillar.activity.Erorr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
	public static void Error(Activity active,String problemType ) {
		Map<String,String> parmters = new HashMap<String, String>();
		String problem = "";
		if(problemType.equals("NO_CONNECTION") ){
			problem = "We have comunication problems, please try again later";
		}else if(problemType.equals("BAD_BALLOT")){
			problem = "Your Ballot is corrupted";
		}else if(problemType.equals("REINSTALL")){
			problem = "Your program is corrupted, please reinstall";
		}else{
			problem = "An unknown error has occured, please try to restart your android";
		}
		parmters.put("problem", problem);
		moveActivtyWitheParam(Erorr.class , active ,parmters);
	}
}
