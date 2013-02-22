package com.example.androidhive;


import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import com.nostra13.socialsharing.common.AuthListener;
import com.nostra13.socialsharing.facebook.FacebookFacade;

public class FacebookActivity extends Activity {
	
private FacebookFacade facebook;
	
	public static final String FACEBOOK_APP_ID = "211619498871712";
	public static String FACEBOOK_SHARE_MESSAGE = "Tis is app check \n :p";
	public static final String FACEBOOK_SHARE_LINK = "https://www.google.co.in/";
	public static final String FACEBOOK_SHARE_LINK_NAME = "Post Complaints";
	public static final String FACEBOOK_SHARE_LINK_DESCRIPTION = "Get the details of this complaint and more such complaints from all over the country here. Want to post your compatints simply use this android app :) ";
	public static final String FACEBOOK_SHARE_PICTURE = "http://blog.internshala.com/wp-content/uploads/2012/10/rakshak_logo.jpg";
	private Map<String, String> actionsMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ac_facebook);
		postOnFacebook() ;
	}
	public void postOnFacebook () {

				facebook = new FacebookFacade(this, FACEBOOK_APP_ID);
				actionsMap = new HashMap<String, String>();
				actionsMap.put("Rakshak Foundation", "http://www.rakshakfoundation.org/");
				if (facebook.isAuthorized()) {
					publishMessage();
					finish();
				} else {
					// Start authentication dialog and publish message after successful authentication
					facebook.authorize(new AuthListener() {
						public void onAuthSucceed() {
								publishMessage();
								finish();
						}
						public void onAuthFail(String error) { // Do noting
						}
					});
				}
	}
	private void publishMessage() {
			facebook.publishMessage(FACEBOOK_SHARE_MESSAGE, FACEBOOK_SHARE_LINK, FACEBOOK_SHARE_LINK_NAME, FACEBOOK_SHARE_LINK_DESCRIPTION, FACEBOOK_SHARE_PICTURE, actionsMap ) ;
	}
}