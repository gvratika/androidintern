package com.example.androidhive;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import com.example.androidhive.library.UserFunctions;

public class MainActivity extends ListActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		String AndroidId = Settings.Secure.getString(getContentResolver(),
		         Settings.Secure.ANDROID_ID);
		//check if already registered 
		
		boolean registered = UserFunctions.deviceStatus(AndroidId, "reg") ;
		if (!(registered)) {	//go to home page
			Intent i = new Intent(getApplicationContext(),UserActivity.class);
			startActivity(i);
			finish();
		}
		else {		//proceed to registration
			Intent i = new Intent(getApplicationContext(),RegisterActivity.class);
			startActivity(i);
			finish();
		}
	}
}