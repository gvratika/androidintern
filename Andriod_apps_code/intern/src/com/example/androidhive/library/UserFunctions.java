
package com.example.androidhive.library;

import java.util.ArrayList;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.ViewDebug.IntToString;


public class UserFunctions {
	
	private static JSONParser jsonParser;
	static JSONParser jParser = new JSONParser();
	public static String user = null;
	private static String url = "117.196.201.141" ;
	private static String loginURL = "http://"+ url +"/android_login_api/";
	private static String registerURL = "http://"+ url +"/android_login_api/";
	public static String url_register = "http://"+ url +"/android_login_api/register.php";
	public static String url_block_status = "http://"+ url +"/android_login_api/block_status.php";
	public static String url_all_complaints = "http://"+ url +"/android_login_api/get_all_complaints.php";
	public static String complaints_history = "http://"+ url +"/android_login_api/complaints_history.php";
	public static final String url_complaint_detials = "http://"+ url +"/android_login_api/get_complaint_details.php";
	public static final String uploadUrl="http://"+ url +"/android_login_api/uploads/";
	public static final String url_create_complaint="http://"+ url +"/android_login_api/create_complaint.php";
	public static final String upload_Server_url = "http://"+ url +"/android_login_api/uploader.php";
	
	private static String login_tag = "login";
	private static String register_tag = "register";
	
	// constructor
	public UserFunctions(){
		jsonParser = new JSONParser();
	}
	
	/* * function to get device block status * */
	
	public static boolean deviceStatus (String ANDROID_ID, String tag ){
		// Building Parameters
		boolean check = false ;		// initial assumption device is blocked or device is not registered
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", tag));
		params.add(new BasicNameValuePair("id", ANDROID_ID));
		JSONObject json = jParser.makeHttpRequest(url_block_status, "GET", params);
		Log.i("device Status ", json.toString());
		String status = null ;
		try {
			status = json.getString("blocked");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (status.matches("false")) {
			check = true ;	//implies device is not blocked/registered
		}
		else {
			if (tag.matches("reg") ) {
				try {
					user = json.getString("email") ;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return check ;
	}
	
	/* * function to get user block status * */
	
	public static boolean userStatus () {
		// Building Parameters
		boolean check = false ;		// initial assumption device is blocked
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "user"));
		params.add(new BasicNameValuePair("id", user));
		JSONObject json = jParser.makeHttpRequest(url_block_status, "GET", params);
		Log.i("user Status ", json.toString());
		String status = null ;
		try {
			status = json.getString("blocked");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (status.matches("false")) {
			check = true ;	//implies user is not blocked
		}
		return check ;
	}
	
	/**
	 * function make Login Request
	 * @param email
	 * @param password
	 * */
	public JSONObject loginUser(String email, String password){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", login_tag));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));
		JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
		// return json
		// Log.e("JSON", json.toString());
		return json;
	}
	
	/**
	 * function make Login Request
	 * @param name
	 * @param email
	 * @param password
	 * */
	public JSONObject registerUser(String firstName, String lastName, String email, String age, 
	String city, String state, String phoneNo, String occupation, String educationLevel, String gender, String AndroidId){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", register_tag));
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
		
		// getting JSON Object
		JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
		// return json
		return json;
	}
	
	/**
	 * Function get Login status
	 * */
	public boolean isUserLoggedIn(Context context){
		DatabaseHandler db = new DatabaseHandler(context);
		int count = db.getRowCount();
		if(count > 0){
			// user logged in
			return true;
		}
		return false;
	}
	
	/**
	 * Function to logout user
	 * Reset Database
	 * */
	public boolean logoutUser(Context context){
		DatabaseHandler db = new DatabaseHandler(context);
		db.resetTables();
		return true;
	}
	
}
