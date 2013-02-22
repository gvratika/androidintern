package com.example.androidhive;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class DisplayPhotoActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo);
		// getting complaint details from intent
		Intent i = getIntent();
			
		// getting photo url from intent
		URL myFileUrl = null ;
		try {
			myFileUrl = new URL(i.getStringExtra("url"));
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ImageView imView = (ImageView)findViewById(R.id.imView);
		Bitmap bmImg ;
		try {
			HttpURLConnection conn= (HttpURLConnection)myFileUrl.openConnection();
	        conn.setDoInput(true);
	        conn.connect();
	        InputStream is = conn.getInputStream();       
	        bmImg = BitmapFactory.decodeStream(is);
	        imView.setImageBitmap(bmImg);
        } catch (IOException e) {
        // TODO Auto-generated catch block
        	e.printStackTrace();
        //Expand image
        } 
	}
}