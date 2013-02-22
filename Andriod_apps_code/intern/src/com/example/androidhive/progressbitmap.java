package com.example.androidhive;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class progressbitmap extends Activity {


	protected LocationManager locationManager;
	protected Location currentLocation;
	Geocoder geocoder ;
	String lat ;
	String log ;
	double LATITUDE = 26.9167;
	double LONGITUDE = 75.8167;
	public static int flag = 1 ;
	public static String address = "";
	List<Address> addresses = null;	

public void onCreate(Bundle savedInstanceState)

{		
	super.onCreate(savedInstanceState);
	
	setContentView(R.layout.capture);
	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	geocoder = new Geocoder(this, Locale.ENGLISH);
	
	boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	// Check if enabled and if not send user to the GSP settings

    if (!enabled) {
       	flag = 0 ;
       	Log.i("sorry", "gps not enabled") ;
       	address = "Enable GPS first !!!" ;
       	finish() ;     	
    }
	currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	if (currentLocation != null) {				
		try {
			List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
			if(addresses != null) {
	   			Address returnedAddress = addresses.get(0);
	   			StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
	   			for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
	   				strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
	   			}
	   			address = strReturnedAddress.toString() ;
	   			Log.i("address", address) ;
	   			finish() ;
			}
			else{
				flag = 0 ;
				address = "No Address returned!" ;
				Log.i("address","No Address returned!");
				finish() ;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flag = 0 ;
			address = "Canont get Address!" ;
			Log.i("address", "Canont get Address!");
			finish() ;
		}
	}
	else {
		flag = 0 ;
		address = "Cannot get location !" ;
		finish() ;
	   				/*Toast.makeText(getApplicationContext(), "null", Toast.LENGTH_LONG).show();
	   				lat = "26.9167" ;
	   				log = "75.8167" ;	
	   				try {
	   				  List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
	   				 
	   				  if(addresses != null) {
	   				   Address returnedAddress = addresses.get(0);
	   				   StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
	   				   for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
	   				    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
	   				   }
	   				   address = strReturnedAddress.toString() ;
	   				   Log.i("address", address) ;
	   				  }
	   				  else{
	   					  address = "No Address returned!" ;
	   							  Log.i("address","No Address returned!");
	   				  }
	   				 } catch (IOException e) {
	   				  // TODO Auto-generated catch block
	   				  e.printStackTrace();
	   				  address = "Canont get Address!" ;
	   				  Log.i("address", "Canont get Address!");
	   				 }*/
		}		
    }

   private class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location location) {
		}

		public void onStatusChanged(String s, int i, Bundle b) {
			/*Toast.makeText(getApplicationContext(), "Provider status changed",
					Toast.LENGTH_LONG).show();*/
		}

		public void onProviderDisabled(String s) {
			Toast.makeText(getApplicationContext(),
					"Provider disabled by the user. GPS turned off",
					Toast.LENGTH_LONG).show();
		}

		public void onProviderEnabled(String s) {
			Toast.makeText(getApplicationContext(),
					"Provider enabled by the user. GPS turned on",
					Toast.LENGTH_LONG).show();
		}

	}
}