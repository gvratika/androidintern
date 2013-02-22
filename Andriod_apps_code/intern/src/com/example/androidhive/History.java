package com.example.androidhive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.androidhive.library.JSONParser;
import com.example.androidhive.library.UserFunctions;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class History extends ListActivity {
	Button searchByName;
	Button searchByIssue;
	Button search;
	EditText inputSearch ;
	// Progress Dialog
	private ProgressDialog pDialog;

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
	// complaints JSONArray
	JSONArray complaints = null;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
		searchByName = (Button) findViewById(R.id.historyName);		
		searchByIssue = (Button) findViewById(R.id.historyIssue);
		search = (Button) findViewById(R.id.historySearch);
		inputSearch = (EditText) findViewById(R.id.historySearchText);
		complaintsList = new ArrayList<HashMap<String, String>>();
		
		Log.e("complaints history", "complaints history") ;
		
		// Loading complaints in Background Thread
		
		new LoadHistory().execute() ;				

		// Get listview
		ListView lv = getListView();
		
		searchByName.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				keyword = inputSearch.getText().toString() ;
				searchBy = "name" ;
				new LoadHistory().execute();
			}
		});
		searchByIssue.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				keyword = inputSearch.getText().toString() ;
				searchBy = "issue" ;
				new LoadHistory().execute();
			}
		});
		search.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				keyword = inputSearch.getText().toString() ;
				new LoadHistory().execute();
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
	}
	// Response from Edit complaint Activity
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if result code 100
		if (resultCode == 100) {
		// if result code 100 is received 
		// means user edited/deleted complaint
		// reload this screen again
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		} 
	}
	/*** Background Async Task to Load all complaint by making HTTP Request	 * */
	public class LoadHistory extends AsyncTask<String, String, String> {
	/**
	 * Before starting background thread Show Progress Dialog
	 * */
	@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(History.this);
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
			params.add(new BasicNameValuePair("user", UserFunctions.user));
			Log.d("user ", UserFunctions.user);
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(UserFunctions.complaints_history, "GET", params);
		
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
						History.this, complaintsList,
						R.layout.list_item, new String[] { TAG_PID,TAG_NAME}, new int[] { R.id.pid, R.id.name });
					// updating listview
					setListAdapter(adapter);
				}
			});
		}
	}
}
