package com.keith.android.g53ids.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.keith.android.g53ids.POI;

import org.mapsforge.core.model.LatLong;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper{

    public static final String DB_NAME = "poi.db";
    public static final int DB_VERSION = 1;

    public static final String POI_TABLE = "POI";

    public static final String POI_ID = "poi_id";
    public static final int POI_ID_COL = 0;

    public static final String POI_NAME = "poi_name";
    public static final int POI_NAME_COL = 1;

    public static final String POI_LATITUDE = "poi_latitude";
    public static final int POI_LATITUDE_COL = 2;

    public static final String POI_LONGITUDE = "poi_longitude";
    public static final int POI_LONGITUDE_COL = 3;

    public static final String CREATE_POI_TABLE =
            "CREATE TABLE " + POI_TABLE + "(" +
                    POI_ID + " TEXT PRIMARY KEY," +
                    POI_NAME + " TEXT NOT NULL," +
                    POI_LATITUDE + " DOUBLE NOT NULL," +
                    POI_LONGITUDE + " DOUBLE NOT NULL" +");";

    public static final String DROP_POI_TABLE =
            "DROP TABLE IF EXISTS " + "POI_TABLE";

    private SQLiteDatabase db;
    private static DBHelper dbHelper;

    public static DBHelper getInstance(Context context){
        if(dbHelper == null){
            dbHelper = new DBHelper(context,DB_NAME,null,DB_VERSION);
        }
        return dbHelper;
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_POI_TABLE);

        db.execSQL("INSERT INTO poi VALUES (1, 'Secret Recipe', 2.945219, 101.874778)");
        db.execSQL("INSERT INTO poi VALUES (2, 'Econsave', 2.945846, 101.846540)");
        db.execSQL("INSERT INTO poi VALUES (3, 'Maybank', 2.947723, 101.846717)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(DBHelper.DROP_POI_TABLE);
        onCreate(db);
    }

    private void openReadableDB(){
        db = dbHelper.getReadableDatabase();
    }

    private void openWritableDB() {
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB(){
        if(db != null){
            db.close();
        }
    }
    public ArrayList<POI> getPois(String name){
        String where = POI_NAME + " LIKE ?";
        String[] whereArgs = new String[] {"%"+name+"%"};
        this.openReadableDB();
        Cursor cursor = db.query(POI_TABLE, null, where, whereArgs, null, null, null);
        ArrayList<POI> pois = new ArrayList<POI>();
        while(cursor.moveToNext()){
            pois.add(getPoiFromCursor(cursor));
        }
        if(cursor != null)
            cursor.close();
        closeDB();
        return pois;
    }
    public POI getPoi(String id){
        String where = POI_ID + " = ?";
        String[] whereArgs = new String[] {id};

        this.openReadableDB();
        Cursor cursor = db.query(POI_TABLE, null, where, whereArgs, null, null, null);
        cursor.moveToFirst();
        POI poi = getPoiFromCursor(cursor);
        if(cursor != null){
            cursor.close();
        }
        closeDB();

        return poi;
    }

    private static POI getPoiFromCursor(Cursor cursor){
        if(cursor == null || cursor.getCount() == 0){
            return new POI("0","No results found",new LatLong(0,0));
        }
        else{
            try{
                POI poi = new POI(
                        cursor.getString(POI_ID_COL),
                        cursor.getString(POI_NAME_COL),
                        new LatLong(cursor.getDouble(POI_LATITUDE_COL),cursor.getDouble(POI_LONGITUDE_COL)));
                return poi;
            }
            catch(Exception e){
                return null;
            }
        }
    }
}
