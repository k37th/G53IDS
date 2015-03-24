package com.keith.android.g53ids;

public class Comment {
    private String mDate;
    private String mText;

    public Comment(String date, String text){
        mDate = date;
        mText = text;
    }

    public String getdate(){
        return mDate;
    }

    public String getText(){
        return mText;
    }

}
