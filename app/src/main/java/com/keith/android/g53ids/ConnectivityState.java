package com.keith.android.g53ids;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by Keith on 4/12/2015.
 */
public class ConnectivityState{

    private static ConnectivityState mInstance = null;
    private Context mContext;
    private ConnectivityManager cm;

    public static ConnectivityState getInstance(Context context){
        if(mInstance == null){
            mInstance = new ConnectivityState(context);
        }
        return mInstance;
    }

    private ConnectivityState(Context context){
        this.mContext = context;
        this.cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean internetNotAvailable(){
        return cm.getActiveNetworkInfo() == null;
    }

    public boolean isInternetAvailable(){
        return cm.getActiveNetworkInfo() != null;
    }
}
