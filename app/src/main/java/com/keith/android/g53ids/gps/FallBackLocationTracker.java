package com.keith.android.g53ids.gps;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

/**
 * Created by Keith on 4/10/2015.
 */
public class FallBackLocationTracker implements LocationTracker, LocationTracker.LocationUpdateListener {

    private boolean isRunning;
    private ProviderLocationTracker gps;
    private ProviderLocationTracker net;

    private LocationUpdateListener listener;
    private Context mContext;
    Location lastLoc;
    long lastTime;

    public FallBackLocationTracker(Context context){
        mContext = context;
        gps = new ProviderLocationTracker(context, ProviderLocationTracker.ProviderType.GPS);
        net = new ProviderLocationTracker(context, ProviderLocationTracker.ProviderType.NETWORK);
    }

    public void start(){
        if(isRunning){
            return;
        }
        gps.start(this);
        net.start(this);
        isRunning = true;
    }

    public void start(LocationUpdateListener update){
        start();
        listener = update;
    }

    public void stop(){
        if(isRunning) {
            gps.stop();
            net.stop();
            isRunning = false;
            listener = null;
        }
    }

    public boolean hasLocation(){
        return gps.hasLocation() || net.hasLocation();
    }

    public boolean  hasPossiblyStaleLocation(){
        return gps.hasPossiblyStaleLocation() || net.hasPossiblyStaleLocation();
    }

    public Location getLocation(){
        Location ret = gps.getLocation();
        if(ret == null){
            ret = net.getLocation();
        }
        return ret;
    }

    public Location getPossiblyStaleLocation(){
        Location ret = gps.getPossiblyStaleLocation();
        if(ret == null){
            ret = net.getPossiblyStaleLocation();
        }
        return ret;
    }

    public void onUpdate(Location oldLoc, long oldTime, Location newLoc, long newTime){
        boolean update = false;
        if(lastLoc == null){
            update = true;
        }
        else if(lastLoc != null && lastLoc.getProvider().equals(newLoc.getProvider())){
            update = true;
        }
        else if(newLoc.getProvider().equals(LocationManager.GPS_PROVIDER)){
            update = true;
        }
        else if(newTime - lastTime > 5 * 60 * 1000){
            update = true;
        }

        if(update){
            if(listener != null){
                listener.onUpdate(lastLoc,lastTime,newLoc,newTime);
            }
            lastLoc = newLoc;
            lastTime = newTime;
            Log.d("Tracker", "Location changed");
            Intent i = new Intent();
            i.setAction("LOCATIONCHANGED");
            i.putExtra("latitude", lastLoc.getLatitude());
            i.putExtra("longitude", lastLoc.getLongitude());
            mContext.sendBroadcast(i);
        }
    }
}
