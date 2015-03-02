package com.keith.android.g53ids.gps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class GPSTracker extends Service implements LocationListener{

    private final Context mContext;
    private static final String TAG = "GPSTracker";
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    Location location;
    double latitude;
    double longitude;

    private static final long MIN_DISTANCE = 1;
    private static final long MIN_TIME = 3 * 1000;

    protected LocationManager locationManager;

    public GPSTracker(Context context){
        this.mContext = context;
        getLocation();
    }

    public Location getLocation(){
        try{
            locationManager = (LocationManager)mContext.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(!isGPSEnabled && !isNetworkEnabled){

            }
            else{
                this.canGetLocation = true;
                if(isNetworkEnabled){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
                    Log.d("GPSTracker", "Network being used");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
//                else
//                if(isGPSEnabled){
//                    if(location == null){
//                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
//                        Log.d("GPSTracker","GPS being used");
//                        if(locationManager != null){
//                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                            if(location != null){
//                                latitude = location.getLatitude();
//                                longitude = location.getLongitude();
//                            }
//                        }
////                    }
//                }
                else{
                    Log.d("GPSTracker", "GPS not enabled");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return location;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("GPSTracker", "Location changed");
        Intent i = new Intent();
        i.setAction("LOCATIONCHANGED");
        i.putExtra("latitude", location.getLatitude());
        i.putExtra("longitude", location.getLongitude());
        mContext.sendBroadcast(i);
    }

    @Override
    public void onProviderDisabled(String provider) {
        if(LocationManager.GPS_PROVIDER.equals(provider)){
            Log.d(TAG,"GPS was turned off");
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        if(LocationManager.GPS_PROVIDER.equals(provider)){
            Log.d(TAG,"GPS was turned on");
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public boolean canGetLocation(){
        return this.canGetLocation;
    }

    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

}
