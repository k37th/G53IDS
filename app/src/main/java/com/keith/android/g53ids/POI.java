package com.keith.android.g53ids;

import org.mapsforge.core.model.LatLong;

public class POI {

    private String mId;
    private String mName;
    private String mType;
    private double mRating;
    private String mContact;
    private String mOpenTime;
    private String mCloseTime;
    private int mMonday;
    private int mTuesday;
    private int mWednesday;
    private int mThursday;
    private int mFriday;
    private int mSaturday;
    private int mSunday;
    private int mStatus;
    private LatLong mCoordinates;

    public POI(String id, String name, LatLong coordinates){
        mId = id;
        mName = name;
        mCoordinates = coordinates;
    }

    public POI(String id, String name, String type, double rating, String contact, String openTime, String closeTime,
               int monday, int tuesday, int wednesday, int thursday, int friday,
               int saturday, int sunday, int status, LatLong coordinates){
        mId = id;
        mName = name;
        mType = type;
        mRating = rating;
        mContact = contact;
        mOpenTime = openTime;
        mCloseTime = closeTime;
        mMonday = monday;
        mTuesday = tuesday;
        mWednesday = wednesday;
        mThursday = thursday;
        mFriday = friday;
        mSaturday = saturday;
        mSunday = sunday;
        mStatus = status;
        mCoordinates = coordinates;
    }

    public String getId(){
        return mId;
    }

    public String getName(){
        return mName;
    }

    public String getType(){ return mType;}

    public double getRating(){ return mRating;}

    public String getContact(){ return mContact;}

    public String getOpenTime(){
        return mOpenTime;
    }

    public String getCloseTime(){
        return mCloseTime;
    }

    public int getMonday(){
        return mMonday;
    }

    public int getTuesday(){
        return mTuesday;
    }

    public int getWednesday(){
        return mWednesday;
    }

    public int getThursday(){
        return mThursday;
    }

    public int getFriday(){
        return mFriday;
    }

    public int getSaturday(){
        return mSaturday;
    }

    public int getSunday(){
        return mSunday;
    }

    public int getStatus(){
        return mStatus;
    }

    @Override
    public String toString(){
        return mName;
    }

    public LatLong getCoordinates(){
        return mCoordinates;
    }
}
