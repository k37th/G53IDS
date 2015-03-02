package com.keith.android.g53ids.database;

import android.content.ContentValues;
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

    public static final String POI_TYPE = "poi_type";
    public static final int POI_TYPE_COL = 2;

    public static final String POI_RATING = "poi_rating";
    public static final int POI_RATING_COL = 3;

    public static final String POI_CONTACT = "poi_contact";
    public static final int POI_CONTACT_COL = 4;

    public static final String POI_OPENHOUR = "poi_openHour";
    public static final int POI_OPENHOUR_COL = 5;

    public static final String POI_CLOSEHOUR = "poi_closeHour";
    public static final int POI_CLOSEHOUR_COL = 6;

    public static final String POI_MONDAY = "poi_monday";
    public static final int POI_MONDAY_COL = 7;

    public static final String POI_TUESDAY = "poi_tuesday";
    public static final int POI_TUESDAY_COL = 8;

    public static final String POI_WEDNESDAY = "poi_wednesday";
    public static final int POI_WEDNESDAY_COL = 9;

    public static final String POI_THURSDAY = "poi_thursday";
    public static final int POI_THURSDAY_COL = 10;

    public static final String POI_FRIDAY = "poi_friday";
    public static final int POI_FRIDAY_COL = 11;

    public static final String POI_SATURDAY = "poi_saturday";
    public static final int POI_SATURDAY_COL = 12;

    public static final String POI_SUNDAY = "poi_sunday";
    public static final int POI_SUNDAY_COL = 13;

    public static final String POI_STATUS = "poi_status";
    public static final int POI_STATUS_COL = 14;

    public static final String POI_LATITUDE = "poi_latitude";
    public static final int POI_LATITUDE_COL = 15;

    public static final String POI_LONGITUDE = "poi_longitude";
    public static final int POI_LONGITUDE_COL = 16;

    public static final String CREATE_POI_TABLE =
            "CREATE TABLE " + POI_TABLE + "(" +
                    POI_ID + " TEXT PRIMARY KEY," +
                    POI_NAME + " TEXT NOT NULL," +
                    POI_TYPE + " TEXT NOT NULL," +
                    POI_RATING + " DOUBLE NOT NULL," +
                    POI_CONTACT + " TEXT," +
                    POI_OPENHOUR + " TEXT NOT NULL," +
                    POI_CLOSEHOUR + " TEXT NOT NULL," +
                    POI_MONDAY + " INTEGER NOT NULL," +
                    POI_TUESDAY + " INTEGER NOT NULL," +
                    POI_WEDNESDAY + " INTEGER NOT NULL," +
                    POI_THURSDAY + " INTEGER NOT NULL," +
                    POI_FRIDAY + " INTEGER NOT NULL," +
                    POI_SATURDAY + " INTEGER NOT NULL," +
                    POI_SUNDAY + " INTEGER NOT NULL," +
                    POI_STATUS + " INTEGER NOT NULL," +
                    POI_LATITUDE + " DOUBLE NOT NULL," +
                    POI_LONGITUDE + " DOUBLE NOT NULL" +");";

    public static final String DROP_POI_TABLE =
            "DROP TABLE IF EXISTS " + POI_TABLE;

    public static final String LAST_SYNC_TABLE = "lastSync";

    public static final String SYNC_ID = "sync_id";
    public static final int SYNC_ID_COL = 0;

    public static final String SYNC_DATE = "sync_date";
    public static final int SYNC_DATE_COL = 1;

    public static final String CREATE_LAST_SYNC_TABLE =
            "CREATE TABLE " + LAST_SYNC_TABLE + "(" +
                    SYNC_ID + " INTEGER PRIMARY KEY," +
                    SYNC_DATE + " DATETIME DEFAULT NULL" +");";

    public static final String DROP_LAST_SYNC_TABLE =
            "DROP TABLE IF EXISTS " + LAST_SYNC_TABLE;

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
        db.execSQL(CREATE_LAST_SYNC_TABLE);
//        db.execSQL("INSERT INTO poi VALUES (1, 'Secret Recipe', 2.945219, 101.874778)");
//        db.execSQL("INSERT INTO poi VALUES (2, 'Econsave', 2.945846, 101.846540)");
//        db.execSQL("INSERT INTO poi VALUES (3, 'Maybank', 2.947723, 101.846717)");
//        db.execSQL("INSERT INTO poi VALUES (1, 'Secret Recipe','Restaurant', '10:00', '8:00', 1, 1, 1, 1, 1, 0, 0, 2.945219, 101.874778)");
        db.execSQL("INSERT INTO lastSync VALUES (1, NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(DBHelper.DROP_POI_TABLE);
        db.execSQL(DBHelper.DROP_LAST_SYNC_TABLE);
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
                        cursor.getString(POI_TYPE_COL),
                        cursor.getDouble(POI_RATING_COL),
                        cursor.getString(POI_CONTACT_COL),
                        cursor.getString(POI_OPENHOUR_COL),
                        cursor.getString(POI_CLOSEHOUR_COL),
                        cursor.getInt(POI_MONDAY_COL),
                        cursor.getInt(POI_TUESDAY_COL),
                        cursor.getInt(POI_WEDNESDAY_COL),
                        cursor.getInt(POI_THURSDAY_COL),
                        cursor.getInt(POI_FRIDAY_COL),
                        cursor.getInt(POI_SATURDAY_COL),
                        cursor.getInt(POI_SUNDAY_COL),
                        cursor.getInt(POI_STATUS_COL),
                        new LatLong(cursor.getDouble(POI_LATITUDE_COL),cursor.getDouble(POI_LONGITUDE_COL)));
                return poi;
            }
            catch(Exception e){
                return null;
            }
        }
    }

    public long insertPOI(POI p){
        ContentValues cv = new ContentValues();
        cv.put(POI_ID, p.getId());
        cv.put(POI_NAME, p.getName());
        cv.put(POI_TYPE, p.getType());
        cv.put(POI_RATING, p.getRating());
        cv.put(POI_CONTACT, p.getContact());
        cv.put(POI_OPENHOUR, p.getOpenTime());
        cv.put(POI_CLOSEHOUR, p.getCloseTime());
        cv.put(POI_MONDAY, p.getMonday());
        cv.put(POI_TUESDAY, p.getTuesday());
        cv.put(POI_WEDNESDAY, p.getWednesday());
        cv.put(POI_THURSDAY, p.getThursday());
        cv.put(POI_FRIDAY, p.getFriday());
        cv.put(POI_SATURDAY, p.getSaturday());
        cv.put(POI_SUNDAY, p.getSunday());
        cv.put(POI_STATUS, p.getStatus());
        cv.put(POI_LATITUDE, p.getCoordinates().latitude);
        cv.put(POI_LONGITUDE, p.getCoordinates().longitude);

        this.openWritableDB();
        long rowID = db.insert(POI_TABLE, null, cv);
        this.closeDB();

        return rowID;
    }

    public String getLastSyncDate(){
        String where = SYNC_ID + " = 1";
//        String[] whereArgs = new String[] {id};

        this.openReadableDB();
        Cursor cursor = db.query(LAST_SYNC_TABLE, null, where, null, null, null, null);
        cursor.moveToFirst();
        String date = cursor.getString(SYNC_DATE_COL);
        if(cursor != null){
            cursor.close();
        }
        closeDB();

        return date;
    }
}
