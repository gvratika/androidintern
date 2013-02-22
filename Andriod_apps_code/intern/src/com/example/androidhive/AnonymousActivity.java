package com.example.androidhive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidhive.library.JSONParser;
import com.example.androidhive.library.UserFunctions;

public class AnonymousActivity extends ListActivity {
	Button btnLinkToLogin;
	Button searchByName;
	Button searchByIssue;
	Button search;
	TextView inputSearch ;
	// Progress Dialog
	private ProgressDialog pDialog;
	//action id
	private static final int ID_COMP_HIS  = 1;
	private static final int ID_COMP   = 2;
	private static final int ID_LOGOUT = 3;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	ArrayList<HashMap<String, String>> complaintsList;

	ListAdapter adapter ;

	// JSON Node names
	private static String searchBy = "name" ;
	private static String keyword = "" ;
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_complaintS = "complaints";
	private static final String TAG_PID = "pid";
	private static String TAG_NAME = searchBy ;
    private String AndroidId ; 
    
//	Context c = this ;
	//private ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(c.CONNECTIVITY_SERVICE);
	//private android.net.NetworkInfo wifi ;
	// complaints JSONArray
	JSONArray complaints = null;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
		searchByName = (Button) findViewById(R.id.name);		
		searchByIssue = (Button) findViewById(R.id.issue);
		search = (Button) findViewById(R.id.search);
		inputSearch = (TextView) findViewById(R.id.inputSearch);
		complaintsList = new ArrayList<HashMap<String, String>>();
		ActionItem newComplaint 	= new ActionItem(ID_COMP, "New Complaint", null);
        ActionItem logout 	= new ActionItem(ID_LOGOUT, "Logout", null);
        
		
      //create QuickAction. Use QuickAction.VERTICAL or QuickAction.HORIZONTAL param to define layout 
        //orientation
		final QuickAction quickAction = new QuickAction(this, QuickAction.VERTICAL);
		
		//add action items into QuickAction
        quickAction.addActionItem(newComplaint);
        quickAction.addActionItem(logout);

		// Loading complaints in Background Thread
		new LoadAllcomplaints().execute() ;				

		// Get listview
		ListView lv = getListView();
		
		searchByName.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				keyword = inputSearch.getText().toString() ;
				searchBy = "name" ;
				new LoadAllcomplaints().execute();
			}
		});
		searchByIssue.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				keyword = inputSearch.getText().toString() ;
				searchBy = "issue" ;
				new LoadAllcomplaints().execute();
			}
		});
		search.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				keyword = inputSearch.getText().toString() ;
				new LoadAllcomplaints().execute();
			}
		});

		// on seleting single complaint
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// getting values from selected ListItem
				String pid = ((TextView) view.findViewById(R.id.pid)).getText()
						.toString();

				// Starting new intent
				Intent in = new Intent(getApplicationContext(),
						LoadComplaintActivity.class);
				// sending pid to next activity
				in.putExtra(TAG_PID, pid);
				
				// starting new activity and expecting some response back
				startActivityForResult(in, 100);
			}
		});

		// Link to Login Screen
		btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

				public void onClick(View view) {
					Intent i = new Intent(getApplicationContext(),LoginActivity.class);
					startActivity(i);
					finish();
				}
		});
		
		
        
        //Set listener for action item clicked
		quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {			
			public void onItemClick(QuickAction source, int pos, int actionId) {				
				ActionItem actionItem = quickAction.getActionItem(pos);
                 
				//here we can filter which action item was clicked with pos or actionId parameter
				if (actionId == ID_COMP) {
					
					AndroidId = Settings.Secure.getString(getContentResolver(),
					         Settings.Secure.ANDROID_ID);
					Toast.makeText(getApplicationContext(), AndroidId, Toast.LENGTH_SHORT).show();
			    	Log.i("mac address", AndroidId);	
			    	//check if device is blocked
			    	boolean user = UserFunctions.userStatus() ;
			    	boolean device = UserFunctions.deviceStatus(AndroidId, "device") ;
			    	if ( (user == true) && (device == true) ) {
			    		Intent i = new Intent(getApplicationContext(), NewComplaintActivity.class);
						startActivity(i);
			    		Log.i("user", "blocked");
			    	}
			    	else if ( (user==false) && (device==false) )   {
			    		Toast.makeText(getApplicationContext(), "Device and User are blocked for complaint submission", Toast.LENGTH_SHORT).show();
			    	}
			    	else if (!(user))  {
			    		Toast.makeText(getApplicationContext(), "User is blocked for complaint submission", Toast.LENGTH_SHORT).show();
			    	}
			    	else {
			    		Toast.makeText(getApplicationContext(), "Device is blocked for complaint submission", Toast.LENGTH_SHORT).show();
			    	}
					
				} else if (actionId == ID_COMP_HIS) {
					Toast.makeText(getApplicationContext(), "See Complatint History", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
					UserFunctions.user = null ;
					Intent i = new Intent(getApplicationContext(),
							MainActivity.class);
					startActivity(i);
					finish();
				}
			}
		});
		
		//set listnener for on dismiss event, this listener will be called only if QuickAction dialog was dismissed
		//by clicking the area outside the dialog.
		quickAction.setOnDismissListener(new QuickAction.OnDismissListener() {			

			public void onDismiss() {
				
			}
		});
		
		//show on btn1
		Button btn1 = (Button) this.findViewById(R.id.btnLinkToLoginScreen);
		btn1.setText(UserFunctions.user) ;
		btn1.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				quickAction.show(v);
			}
		});      
		
	}
	/**
	 * get bluetooth adapter MAC address
	 * @return MAC address String
	 */
	public static String getBluetoothMacAddress() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// if device does not support Bluetooth
		if(mBluetoothAdapter==null){
			Log.d("BluetoothAdapter","device does not support bluetooth");
			return null;
		}		
		return mBluetoothAdapter.getAddress();
	}

	/*** Background Async Task to Load all complaint by making HTTP Request	 * */
	public class LoadAllcomplaints extends AsyncTask<String, String, String> {
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(AnonymousActivity.this);
			pDialog.setMessage("Loading complaints. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		/**
		* getting All complaints from url
		* */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("searchBy", searchBy));
			params.add(new BasicNameValuePair("keyword", keyword));
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(UserFunctions.url_all_complaints, "GET", params);
		
			// Check your log cat for JSON reponse
			Log.d("All complaints: ", json.toString());
			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);
				//Remove previous result
				complaintsList.clear() ;
				if (success == 1) {
					// complaints found
					// Getting Array of complaints
					complaints = json.getJSONArray(TAG_complaintS);
					// looping through All complaints
					for (int i = 0; i < complaints.length(); i++) {
						JSONObject c = complaints.getJSONObject(i);
						// Storing each json item in variable
						String id = c.getString(TAG_PID);
						String name = c.getString(TAG_NAME);
						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();
						// adding each child node to HashMap key => value
						map.put(TAG_PID, id);
						map.put(TAG_NAME, name);
						// adding HashList to ArrayList
						complaintsList.add(map);
					}
				} 
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		public void onPostExecute(String file_url) {
			// dismiss the dialog after getting all complaints
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					adapter = null ;
					adapter = new SimpleAdapter(
						AnonymousActivity.this, complaintsList,
						R.layout.list_item, new String[] { TAG_PID,TAG_NAME}, new int[] { R.id.pid, R.id.name });
						// updating listview
					setListAdapter(null) ;
					setListAdapter(adapter);
				}
			});
		}
	}
}