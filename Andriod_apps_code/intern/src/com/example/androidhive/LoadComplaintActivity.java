package com.example.androidhive;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.androidhive.library.JSONParser;
import com.example.androidhive.library.UserFunctions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.AbsListView.OnScrollListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class LoadComplaintActivity extends Activity implements OnScrollListener {

	TextView txtName;
	TextView txtissue;
	TextView txtDesc;
	TextView txtCreatedAt;
	ImageView imView;
	VideoView vView;

	String pid;

	// Progress Dialog
	private ProgressDialog pDialog;
	
	Context context ;
	
	URL myFileUrl = null ;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// single complaint url
	private static final String url_complaint_detials = UserFunctions.url_complaint_detials ;
	private static final String uploadUrl = UserFunctions.uploadUrl ;
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_complaint = "complaint";
	private static final String TAG_PID = "pid";
	private static final String TAG_NAME = "name";
	private static final String TAG_PHOTO = "photo";
	private static final String TAG_VIDEO = "video";
	private static final String TAG_issue = "issue";
	private static final String TAG_DESCRIPTION = "description";
	private static final String TAG_created_at = "created_at";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.complaint);
		//Set context
		context = this ;
		// getting complaint details from intent
		Intent i = getIntent();
		
		// getting complaint id (pid) from intent
		pid = i.getStringExtra(TAG_PID);

		// Getting complete complaint details in background thread
		new GetcomplaintDetails().execute();
		imView = (ImageView)findViewById(R.id.imview);
        imView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent in = new Intent(getApplicationContext(),
						DisplayPhotoActivity.class);
				// sending pid to next activity
				in.putExtra("url", myFileUrl.toString() ) ;
				startActivity(in);
			}
		});
	}

	/**
	 * Background Async Task to Get complete complaint details
	 * */
	class GetcomplaintDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(LoadComplaintActivity.this);
			pDialog.setMessage("Loading complaint details. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Getting complaint details in background thread
		 * */
		protected String doInBackground(String... params) {

			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					// Check for success tag
					int success;
					Bitmap bmImg ;
					try {
						// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("pid", pid));

						// getting complaint details by making HTTP request
						// Note that complaint details url will use GET request
						JSONObject json = jsonParser.makeHttpRequest(
								url_complaint_detials, "GET", params);

						// check your log for json response
						Log.d("Single complaint Details", json.toString());
						
						// json success tag
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							// successfully received complaint details
							JSONArray complaintObj = json
									.getJSONArray(TAG_complaint); // JSON Array
							
							// get first complaint object from JSON Array
							JSONObject complaint = complaintObj.getJSONObject(0);

							// complaint with this pid found
							// Edit Text
							txtName = (TextView) findViewById(R.id.inputName);
							txtissue = (TextView) findViewById(R.id.inputissue);
							txtDesc = (TextView) findViewById(R.id.inputDesc);
							txtDesc = (TextView) findViewById(R.id.date);
							vView = (VideoView)findViewById(R.id.viview);

							// display complaint data in TextView
							txtName.setText(complaint.getString(TAG_NAME));
							txtissue.setText(complaint.getString(TAG_issue));
							txtDesc.setText(complaint.getString(TAG_DESCRIPTION));
							txtDesc.setText(complaint.getString(TAG_created_at));
							//display pic
							String extension = complaint.getString(TAG_PHOTO) ;
							if (extension != "null" ) {         
								try {
									myFileUrl= new URL(uploadUrl+pid+".jpg");
						            Log.d("ImageURL", myFileUrl.toString() );
						        } catch (MalformedURLException e) {
						        	// TODO Auto-generated catch block
						            e.printStackTrace();
						        }
						        try {
							        HttpURLConnection conn= (HttpURLConnection)myFileUrl.openConnection();
							        conn.setDoInput(true);
							        conn.connect();
							        InputStream is = conn.getInputStream();       
							        bmImg = BitmapFactory.decodeStream(is);
							        bmImg = getResizedBitmap(bmImg, (0.15*bmImg.getHeight()), (0.15*bmImg.getWidth())) ;
							        imView.setImageBitmap(bmImg);
						        } catch (IOException e) {
						        // TODO Auto-generated catch block
						        	e.printStackTrace();
						        }
							}
					       // display Video
							//**** Not tested, require real device ****
							extension = complaint.getString(TAG_VIDEO) ;
							if (extension != "null" ) {
								//use this to get touch events
								vView.requestFocus();
								//loads video from remote server
								//set the video path
								String vSource = ( uploadUrl+pid+".mp4" ) ;
								//set the video URI, passing the vSource as a URI
					     	 	vView.setVideoURI(Uri.parse(vSource));
					     	 	Log.i ("Video URL", vSource) ;
					     	 	//enable this if you want to enable video controllers, such as pause and forward
					     	 	vView.setMediaController(new MediaController(context));
					          
					          	//plays the movie
					     	 	vView.start();
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

			return null;
		}
		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once got all details
			pDialog.dismiss();
		}
	}
	public Bitmap getResizedBitmap(Bitmap bm, double newHeight, double newWidth) {
	    int width = bm.getWidth();
	    int height = bm.getHeight();
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;

	    // create a matrix for the manipulation
	    Matrix matrix = new Matrix();

	    // resize the bit map
	    matrix.postScale(scaleWidth, scaleHeight);

	    // recreate the new Bitmap
	    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

	    return resizedBitmap;
	}
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		
	}
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}
}
