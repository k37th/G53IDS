package com.keith.android.g53ids.gps;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by Keith on 4/10/2015.
 */
public class ProviderLocationTracker implements LocationListener, LocationTracker{

    private static final long MIN_UPDATE_DISTANCE = 10;
    private static final long MIN_UPDATE_TIME = 1000 * 60;
    private LocationManager lm;

    public enum ProviderType{
        NETWORK,
        GPS
    };

    private String provider;
    private Location lastLocation;
    private long lastTime;
    private boolean isRunning;
    private LocationUpdateListener listener;

    public ProviderLocationTracker(Context context, ProviderType type){
        lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        if(type == ProviderType.NETWORK){
            provider = LocationManager.NETWORK_PROVIDER;
        }
        else{
            provider = LocationManager.GPS_PROVIDER;
        }
    }

    public void start(){
        if(isRunning){
            return;
        }
        isRunning = true;
        lm.requestLocationUpdates(provider, MIN_UPDATE_TIME, MIN_UPDATE_DISTANCE, this);
        lastLocation = null;
        lastTime = 0;
        return;
    }

    public void start(LocationUpdateListener update){
        start();
        listener = update;
    }

    public void stop(){
        if(isRunning){
            lm.removeUpdates(this);
            isRunning = false;
            listener = null;
        }
    }

    public boolean hasLocation(){
        if(lastLocation == null){
            return false;
        }
        if(System.currentTimeMillis() - lastTime > 5 * MIN_UPDATE_TIME){
            return false;
        }
        return true;
    }

    public boolean hasPossiblyStaleLocation(){
        if(lastLocation != null){
            return true;
        }
        return lm.getLastKnownLocation(provider) != null;
    }

    public Location getLocation(){
        if(lastLocation == null){
            return null;
        }
        if(System.currentTimeMillis() - lastTime > 5 * MIN_UPDATE_TIME){
            return null;
        }
        return lastLocation;
    }

    public Location getPossiblyStaleLocation(){
        if(lastLocation != null){
            return lastLocation;
        }
        return lm.getLastKnownLocation(provider);
    }

    public void onLocationChanged(Location newLoc){
        long now = System.currentTimeMillis();
        if(listener != null){
            listener.onUpdate(lastLocation, lastTime, newLoc, now);
        }
        lastLocation = newLoc;
        lastTime = now;
    }

    public void onProviderDisabled(String arg0){}

    public void onProviderEnabled(String arg0){}

    public void onStatusChanged(String arg0, int arg1, Bundle arg2){}

}
