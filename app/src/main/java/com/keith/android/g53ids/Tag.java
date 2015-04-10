package com.keith.android.g53ids;

/**
 * Created by Keith on 3/28/2015.
 */
public class Tag {

    private String mId;
    private String mName;
    private String mPoi;
    private int mFlag;

    public Tag( String id, String name, String poi, int flag){
        mId = id;
        mName = name;
        mPoi = poi;
        mFlag = flag;
    }

    public String getId(){
        return mId;
    }

    public String getName(){
        return mName;
    }

    public String getPoi(){
        return mPoi;
    }

    public int getFlag(){
        return mFlag;
    }

    public String displayTag(){
        return "ID:" + mId + ", Name:" + mName + ", Poi:" + mPoi + ", Flag:"+mFlag ;
    }
}
