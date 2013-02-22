package com.example.androidhive;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.os.Bundle;

import com.example.androidhive.library.UserFunctions;

	public class Uploader extends Activity {

	    

	    /** Called when the activity is first created. */
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	    	
	    	String urlServer = UserFunctions.upload_Server_url;
		    //String pathToOurFile = "/mnt/sdcard/06032012018.mp4";
		    String pathToOurFile = "/mnt/sdcard/Image0212.jpg";

		    HttpURLConnection connection = null;
		    DataOutputStream outputStream = null;
		    DataInputStream inputStream = null;

		    String lineEnd = "\r\n";
		    String twoHyphens = "--";
		    String boundary =  "*****";
		    int bytesRead, bytesAvailable, bufferSize;
		    byte[] buffer;
		    int maxBufferSize = 1*1024*1024;
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.upload);

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
	        outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + "test.jpg" +"\"" + lineEnd);
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

