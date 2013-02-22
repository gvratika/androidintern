
package com.example.androidhive;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.androidhive.library.JSONParser;
import com.example.androidhive.library.UserFunctions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends Activity {
	Button btnRegister;
	Button btnLinkToLogin;
	EditText InputFirstName ;
	EditText InputLastName ;
	EditText InputEmail ;
	EditText InputAge ;
	EditText InputCity ;
	EditText InputState ;
	EditText InputPhoneNo ;
	EditText InputOccupation ;
	EditText InputEducationLevel ;
	EditText InputGender ;
	TextView registerErrorMsg;
	
	// JSON Response node names
	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR_MSG = "error_msg";


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		// Importing all assets like buttons, text fields
		InputFirstName = (EditText) findViewById(R.id.FirstName);
		InputLastName = (EditText) findViewById(R.id.lastName);
		InputEmail = (EditText) findViewById(R.id.email);
		InputAge = (EditText) findViewById(R.id.age);
		InputCity = (EditText) findViewById(R.id.city);
		InputState = (EditText) findViewById(R.id.state);
		InputPhoneNo  = (EditText) findViewById(R.id.phoneNo);
		InputOccupation = (EditText) findViewById(R.id.occupation);
		InputEducationLevel = (EditText) findViewById(R.id.educationLevel);
		InputGender = (EditText) findViewById(R.id.gender);
		registerErrorMsg = (TextView) findViewById(R.id.register_error);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		
		// Register Button Click event
		btnRegister.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View view) {
				String firstName = InputFirstName.getText().toString();;
				String lastName = InputLastName.getText().toString();
				String email = InputEmail.getText().toString();
				String age = InputAge.getText().toString();
				String city = InputCity.getText().toString();
				String state = InputState.getText().toString();
				String phoneNo = InputPhoneNo.getText().toString();
				String occupation = InputOccupation.getText().toString();
				String educationLevel = InputEducationLevel.getText().toString();
				String gender = InputGender.getText().toString();
				String AndroidId = Settings.Secure.getString(getContentResolver(),
				         Settings.Secure.ANDROID_ID);
				if ( (( ( (firstName.matches("")) || (lastName.matches("")) ) || ((email.matches("")) || (age.matches("") )) ) ||
				(((city.matches("") ) || (state.matches("") )) || ((phoneNo.matches("") ) || (occupation.matches("") )) )) || 
				((educationLevel.matches("") ) || (gender.matches("") )) ) {
					registerErrorMsg.setText("All fields are Required");
				}	
				else {	//register user
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("tag", "register"));
					params.add(new BasicNameValuePair("firstName", firstName));
					params.add(new BasicNameValuePair("lastName", lastName));
					params.add(new BasicNameValuePair("email", email));
					params.add(new BasicNameValuePair("age", age));
					params.add(new BasicNameValuePair("city", city));
					params.add(new BasicNameValuePair("state", state));
					params.add(new BasicNameValuePair("phoneNo", phoneNo));
					params.add(new BasicNameValuePair("occupation", occupation));
					params.add(new BasicNameValuePair("educationLevel", educationLevel));
					params.add(new BasicNameValuePair("gender", gender));
					params.add(new BasicNameValuePair("AndroidId", AndroidId));
					
					JSONParser jParser = new JSONParser();
					JSONObject json = jParser.makeHttpRequest(UserFunctions.url_register, "GET", params);
					// check for login response
					try {
						if (json.getString(KEY_SUCCESS) != null) {
							registerErrorMsg.setText("");
							String res = json.getString(KEY_SUCCESS); 
							if(Integer.parseInt(res) == 1){
								// user successfully registred
								Intent main = new Intent(getApplicationContext(), MainActivity.class);
								// Close all views before launching main
								main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								// Set current user
								UserFunctions.user = email ;
								startActivity(main);
								// Close Registration Screen
								finish();
							}else{
								// Error in registration
								registerErrorMsg.setText(json.getString(KEY_ERROR_MSG));
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
				}
				
				
			}
		});

		// Link to Login Screen
		/*btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(),
						LoginActivity.class);
				startActivity(i);
				// Close Registration View
				finish();
			}
		}); */
	}
}
