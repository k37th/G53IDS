package com.keith.android.g53ids;

import java.util.ArrayList;

public class ResultList {
    public static ArrayList<POI> results;

    public static ArrayList<POI> getInstance(){
        if(results == null){
            results = new ArrayList<POI>();
        }
        return results;
    }

    public int getSize(){
        return results.size();
    }

    public void addPOI(POI p){
        results.add(p);
    }

    public void resetResults(){
        results.clear();
    }

}
