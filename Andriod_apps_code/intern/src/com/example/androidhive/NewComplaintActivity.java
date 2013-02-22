
package com.example.androidhive;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.EventLogTags.Description;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.androidhive.library.JSONParser;
import com.example.androidhive.library.UserFunctions;

public class NewComplaintActivity extends Activity {
	
	final int SELECT_PICTURE = 100; // request code for browsing images
	private static final int SELECT_VIDEO = 1; // request code for browsing images
    private Uri browseImageUri;			// image uri for existing images
    private Uri browseVideoUri;			// video uri for existing video
    private String imagePath = "";
    private String videoPath = "";
    int success ;		//for complaint submission
    
    EditText title;
	EditText issue;
	EditText description;
	EditText address;
	RadioButton uploadImage ;
	RadioButton captureImage ;
	RadioButton noImage ;
	RadioButton noVideo ;
	RadioButton uploadVideo ;
	RadioButton recordVideo ;
	CheckBox currentLocation ;
	CheckBox shareOnFb ;
	CheckBox shareOnTwitter ;
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	
	private static String urlServer = UserFunctions.url_create_complaint ;
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newcomplaint) ;
		
		title = (EditText) findViewById(R.id.newComplaintTitle);
		issue = (EditText) findViewById(R.id.newComplaintIssue);
		description = (EditText) findViewById(R.id.newComplaintDescription);
		address = (EditText) findViewById(R.id.newComplaintAddress);
		uploadImage = (RadioButton) findViewById(R.id.uploadImage);
		captureImage = (RadioButton) findViewById(R.id.captureImage);
		noImage = (RadioButton) findViewById(R.id.noImage);
		noVideo = (RadioButton) findViewById(R.id.noVideo);
		uploadVideo = (RadioButton) findViewById(R.id.uploadVideo);
		recordVideo = (RadioButton) findViewById(R.id.recordVideo);
		currentLocation = (CheckBox) findViewById(R.id.currentLocation);
		shareOnFb = (CheckBox) findViewById(R.id.shareOnFb);
		shareOnTwitter = (CheckBox) findViewById(R.id.shareOnTwitter);
		//theImageView = (ImageView) findViewById(R.id.thePicture); // imageview element		
	}
	public void getCurrentLocation(View view) { // onClick method for browsing images
		if ( currentLocation.isChecked()) {
			Intent i = new Intent(getApplicationContext(), progressbitmap.class);
			startActivity(i);
			if ( progressbitmap.flag == 0 ) {
				Log.i("enter if", "jahds") ;
				Toast.makeText(getApplicationContext(), progressbitmap.address, Toast.LENGTH_LONG).show();
				currentLocation.setChecked(false) ;
			}
			else {
				address.setText(progressbitmap.address) ;
			}
		}
    }
	public void recordVideo(View view) { // onClick method for browsing images
		Log.i("recordVideo", "recordVideo") ;
		Intent i = new Intent(getApplicationContext(), VideoParserActivity.class);
		startActivity(i);
		videoPath = VideoParserActivity.mOutputFileName ;
    }
	public void captureImage(View view) { // onClick method for browsing images
		Log.i("captureImage", "captureImage") ;
		Intent i = new Intent(getApplicationContext(), ShootAndCropActivity.class);
		startActivity(i);
		imagePath = ShootAndCropActivity.path ;
    }
	public void browseVideo(View view) { // onClick method for browsing images
		Log.i("browseVideo", "browseVideo") ;
    	Intent browse = new Intent();
    	browse.setType("video/*");
    	browse.setAction(Intent.ACTION_GET_CONTENT);
    	startActivityForResult(Intent.createChooser(browse, "Select a video"), SELECT_VIDEO);    	
    }
	public void browseImages(View view) { // onClick method for browsing images
		Log.i("browseImages", "browseImages") ;
    	Intent browse = new Intent();
    	browse.setType("image/*");
    	browse.setAction(Intent.ACTION_GET_CONTENT);
    	startActivityForResult(Intent.createChooser(browse, "Select an image"), SELECT_PICTURE);    	
    }	    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // handling the activity results
    	Log.i("onActivityResult", "onActivityResult") ;
    	if (resultCode != RESULT_OK) return;

        if (requestCode == SELECT_PICTURE) { // actions for select from file (100)
        	browseImageUri = data.getData();
        	imagePath = getRealPath(browseImageUri); // if using the gallery, you need to get the actual path
        	
        	if (imagePath == null) // if the path is not supplied, get it now
        		imagePath = browseImageUri.getPath();
        	Log.i("imagePath", imagePath) ;
       	}
        else {
        	browseVideoUri = data.getData();
        	videoPath = getRealPath(browseVideoUri); // if using the gallery, you need to get the actual path
        	
        	if (videoPath == null) // if the path is not supplied, get it now
        		videoPath = browseVideoUri.getPath();
        	Log.i("videoPath", videoPath) ;
       	}
        
        	
        //theImageView.setImageBitmap(selectedImage); // display our image
    }
        
    public String getRealPath(Uri imageUri) { // since the gallery doesn't give us the real path, let's take it
    	Log.i("getRealPath", "getRealPath") ;
    	String [] imageProj = {MediaStore.Images.Media.DATA}; // the column to return
    	Cursor cursor		= managedQuery(imageUri, imageProj, null, null, null); // query the uri and projection
    	
    	if (cursor == null) return null;
    	
    	int columnIndex 	= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA); // get the column index
    	
    	cursor.moveToFirst(); // move to the first column
    	
    	return cursor.getString(columnIndex); // return 
    }
    public void onSubmit(View view) { // onClick method for browsing images
		Log.i("Title", title.getText().toString()) ;
		Log.i("Issue", issue.getText().toString()) ;
		Log.i("Description", description.getText().toString()) ;
		String complaintTitle = title.getText().toString() ;
    	String complaintIssue = issue.getText().toString() ;
    	String complaintDescription = description.getText().toString() ;
    	
    	int flag = 0 ;
    	if ( complaintDescription.matches("")  ) {
    		description.setHint("Required") ;
    		description.setHintTextColor(Color.RED) ;
    		flag = 1 ;
    	}
    	if ( complaintTitle.matches("") ) {
    		title.setHint("Required") ;
    		title.setHintTextColor(Color.RED) ;
    		flag = 1 ;
    	}
    	if ( complaintIssue.matches("")  ) {
    		issue.setHint("Required") ;
    		issue.setHintTextColor(Color.RED) ;
    		flag = 1 ;
    	}
    	if ( flag == 0 ) {
    		if ( noImage.isChecked() ) {
    			imagePath = "" ;
    		}
    		if ( noVideo.isChecked() ) {
    			videoPath = "" ;
    		} 
    		new CreateNewComplaint().execute();
    		if (shareOnFb.isChecked()) {
    			
    		}
    	} 
    	   	
    }
    /**
	 * Background Async Task to Create new product
	 * */
	class CreateNewComplaint extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(NewComplaintActivity.this);
			pDialog.setMessage("Creating Product..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating product
		 * */
		protected String doInBackground(String... args) {
			String complaintTitle = title.getText().toString() ;
	    	String complaintIssue = issue.getText().toString() ;
	    	String complaintDescription = description.getText().toString() ;

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("name", complaintTitle));
			params.add(new BasicNameValuePair("issue", complaintIssue));
			params.add(new BasicNameValuePair("description", complaintDescription));
			params.add(new BasicNameValuePair("user", UserFunctions.user));
			if ( imagePath.matches("")  ) {
				params.add(new BasicNameValuePair("image", "null"));
	    	}
			else
				params.add(new BasicNameValuePair("image", ".jpg"));
			if ( videoPath.matches("")  ) {
				params.add(new BasicNameValuePair("video", "null"));
	    	}
			else
				params.add(new BasicNameValuePair("video", ".mp4"));
			
			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(urlServer,
					"POST", params);
			
			// check log cat fro response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				success = json.getInt(TAG_SUCCESS);
				int id = json.getInt(TAG_MESSAGE);
				if (success == 1) {
					// successfully created complaint
					String filename ;
					if (! (imagePath.matches("")) ) {
						filename = Integer.toString(id) + ".jpg" ;
						uploader(imagePath, filename) ;
					}
					if (! (videoPath.matches("")) ) {
						filename = Integer.toString(id) + ".mp4" ;
						uploader(videoPath, filename) ;
					}
					
				} else {
					// failed to create complaint
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			pDialog.dismiss();
			if (shareOnFb.isChecked()) {
				Log.i("Share on fb", "true") ;
    			String message = title.getText().toString() + "\nISSUE : \n" + issue.getText().toString() + "\nDESCRIPTION : \n" + description.getText().toString() ;
    			Log.i("message", message) ;
				FacebookActivity.FACEBOOK_SHARE_MESSAGE = message ;
    			Intent i = new Intent(getApplicationContext(),FacebookActivity.class);
				startActivity(i);
    		}
			finish() ;
		}

	}
	void uploader (String path, String filename ) {

		String urlServer = UserFunctions.upload_Server_url;
	    //String pathToOurFile = "/mnt/sdcard/06032012018.mp4";
	    String pathToOurFile = path;

	    HttpURLConnection connection = null;
	    DataOutputStream outputStream = null;
	    DataInputStream inputStream = null;

	    String lineEnd = "\r\n";
	    String twoHyphens = "--";
	    String boundary =  "*****";
	    int bytesRead, bytesAvailable, bufferSize;
	    byte[] buffer;
	    int maxBufferSize = 1*1024*1024;

        try
        {
	        FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );
	
	        URL url = new URL(urlServer);
	        connection = (HttpURLConnection) url.openConnection();
	
	        // Allow Inputs & Outputs
	        connection.setDoInput(true);
	        connection.setDoOutput(true);
	        connection.setUseCaches(false);
	
	        // Enable POST method
	        connection.setRequestMethod("POST");
	
	        connection.setRequestProperty("Connection", "Keep-Alive");
	        connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
	
	        outputStream = new DataOutputStream( connection.getOutputStream() );
	        outputStream.writeBytes(twoHyphens + boundary + lineEnd);
	        outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + filename +"\"" + lineEnd);
	        outputStream.writeBytes(lineEnd);
	
	        bytesAvailable = fileInputStream.available();
	        bufferSize = Math.min(bytesAvailable, maxBufferSize);
	        //Log.i("bufferSize", getString(bufferSize) ) ;
	        buffer = new byte[bufferSize];
	
	        // Read file
	        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	        while (bytesRead > 0)
	        {
		        outputStream.write(buffer, 0, bufferSize);
		        bytesAvailable = fileInputStream.available();
		        bufferSize = Math.min(bytesAvailable, maxBufferSize);
		        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	        }
	
	        outputStream.writeBytes(lineEnd);
	        outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
	
	        // Responses from the server (code and message)
	        int serverResponseCode = connection.getResponseCode();
	        String serverResponseMessage = connection.getResponseMessage();
	
	        fileInputStream.close();
	        outputStream.flush();
	        outputStream.close();
        }
        catch (Exception ex)
        {
        //Exception handling
        }    
	}
}