package com.example.androidhive;
//ADD PACKAGE HERE

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * ShootAndCropActivity is for capturing and cropping camera images
 * - user presses button to capture an image using the device camera
 * - when they return with the captured image Uri, the app launches the crop action intent
 * - on returning from the crop action, the app displays the cropped image
 */
public class ShootAndCropActivity extends Activity implements OnClickListener {
	
	//keep track of camera capture intent
	final int CAMERA_CAPTURE = 1;
	//keep track of cropping intent
	final int PIC_CROP = 2;
	//captured picture uri
	private Uri picUri;
	//captured picture path on sdCard
	public static String path = "" ;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture);
        
        //retrieve a reference to the UI button
        Button captureBtn = (Button)findViewById(R.id.capture_btn);
        //handle button clicks
        captureBtn.setOnClickListener(this);
    }

    /**
     * Click method to handle user pressing button to launch camera
     */
    public void onClick(View v) {
        if (v.getId() == R.id.capture_btn) {     
        	try {
	        	//use standard intent to capture an image
	        	Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	        	//we will handle the returned data in onActivityResult
	            startActivityForResult(captureIntent, CAMERA_CAPTURE);
        	}
            catch(ActivityNotFoundException anfe){
        		//display an error message
        		String errorMessage = "Whoops - your device doesn't support capturing images!";
        		Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
        		toast.show();
        	}
        }
    }
    
    /**
     * Handle user returning from both capturing and cropping the image
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == RESULT_OK) {
    		//user is returning from capturing an image using the camera
    		if(requestCode == CAMERA_CAPTURE){
    			//get the Uri for the captured image
    			picUri = data.getData();
    			//carry out the crop operation
    			performCrop();
    		}
    		//user is returning from cropping the image
    		else if(requestCode == PIC_CROP){
    			//get the returned data
    			Bundle extras = data.getExtras();
    			//get the cropped bitmap
    			Bitmap thePic = extras.getParcelable("data");
    			//retrieve a reference to the ImageView
    			ImageView picView = (ImageView)findViewById(R.id.picture);
    			//display the returned cropped image
    			picView.setImageBitmap(thePic);
    			//Save Image on sdCard 
    			
    		}
    	}
    }
    /**
     * Helper method to save image on sdCard
     */
    void saveImage(Bitmap myBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        int x = 0 ;
        String fname = "Image.jpg";
        File file ;
        do {
        	fname = "Image" + x + ".jpg" ;
        	file = new File (myDir, fname);	
        	x++ ;
        }while (file.exists ()) ;
        try {
               FileOutputStream out = new FileOutputStream(file);
               myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
               out.flush();
               out.close();
               path = root+"/"+fname ;

        } catch (Exception e) {
               e.printStackTrace();
        }
    }
    
    /**
     * Helper method to carry out crop operation
     */
    private void performCrop(){
    	//take care of exceptions
    	try {
    		//call the standard crop action intent (the user device may not support it)
	    	Intent cropIntent = new Intent("com.android.camera.action.CROP"); 
	    	//indicate image type and Uri
	    	cropIntent.setDataAndType(picUri, "image/*");
	    	//set crop properties
	    	cropIntent.putExtra("crop", "true");
	    	//indicate aspect of desired crop
	    	cropIntent.putExtra("aspectX", 1);
	    	cropIntent.putExtra("aspectY", 1);
	    	//indicate output X and Y
	    	cropIntent.putExtra("outputX", 256);
	    	cropIntent.putExtra("outputY", 256);
	    	//retrieve data on return
	    	cropIntent.putExtra("return-data", true);
	    	//start the activity - we handle returning in onActivityResult
	        startActivityForResult(cropIntent, PIC_CROP);  
    	}
    	//respond to users whose devices do not support the crop action
    	catch(ActivityNotFoundException anfe){
    		//display an error message
    		String errorMessage = "Whoops - your device doesn't support the crop action!";
    		Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
    		toast.show();
    	}
    }
}
