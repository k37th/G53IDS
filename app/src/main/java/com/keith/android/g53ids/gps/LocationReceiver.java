package com.keith.android.g53ids.gps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LocationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        Log.d("MainActivity", "Receive broadcast");
//            updateLocation(intent.getDoubleExtra("latitude", 0),intent.getDoubleExtra("longitude",0));
    }
}
