package com.keith.android.g53ids;

import org.mapsforge.core.model.LatLong;

/**
 * Created by Keith on 3/31/2015.
 */
public class UserLocation {
    private static UserLocation mInstance = null;
    private LatLong mLocation;

    public static UserLocation getInstance(){
        if(mInstance == null){
            mInstance = new UserLocation();
        }
        return mInstance;
    }

    private UserLocation(){
        this.mLocation = new LatLong(0,0);
    }

    public LatLong getLocation(){
        return this.mLocation;
    }

    public void setLocation(LatLong location){
        this.mLocation = location;
    }

    public boolean nullLocation(){
        if(mLocation.latitude == 0 && mLocation.longitude == 0)
            return true;
        else
            return false;
    }
}
