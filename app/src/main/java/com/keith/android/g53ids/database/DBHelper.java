package com.keith.android.g53ids.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.PointF;

import com.keith.android.g53ids.POI;
import com.keith.android.g53ids.Tag;

import org.mapsforge.core.model.LatLong;

import java.util.ArrayList;
import java.util.Calendar;

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

    public static final String TAG_TABLE ="tag";

    public static final String TAG_ID = "tag_id";
    public static final int TAG_ID_COL = 0;

    public static final String TAG_NAME = "tag_name";
    public static final int TAG_NAME_COL = 1;

    public static final String TAG_POI = "tag_poi";
    public static final int TAG_POI_COL = 2;

    public static final String TAG_FLAG = "tag_flag";
    public static final int TAG_FLAG_COL = 3;

    public static final String CREATE_TAG_TABLE =
            "CREATE TABLE " + TAG_TABLE + "(" +
                    TAG_ID + " TEXT PRIMARY KEY," +
                    TAG_NAME + " TEXT NOT NULL," +
                    TAG_POI + " TEXT NOT NULL," +
                    TAG_FLAG + " INTEGER NOT NULL" + ");";

    public static final String DROP_TAG_TABLE =
            "DROP TABLE IF EXISTS " + TAG_TABLE;

    private SQLiteDatabase db;
    private static DBHelper dbHelper;

    public static DBHelper getInstance(Context context){
        if(dbHelper == null){
            dbHelper = new DBHelper(context.getApplicationContext(),DB_NAME,null,DB_VERSION);
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
        db.execSQL(CREATE_TAG_TABLE);
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
        db.execSQL(DBHelper.DROP_TAG_TABLE);
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
    public ArrayList<POI> getPois(boolean availableNow, String name){
        String where = POI_STATUS + " = ?  AND " + POI_NAME + " LIKE ?";
        if(availableNow){
            where += includeAvailabilityQuery();
        }
        String[] whereArgs = new String[] {"2","%"+name+"%"};

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

    public ArrayList<POI> getTagRelatedPois(boolean availableNow, String tag){

        String query = "SELECT * FROM "+POI_TABLE + " WHERE " +POI_STATUS +" = ?" ;
        if(availableNow){
            query += includeAvailabilityQuery();
        }
        query += " AND " + POI_ID + " IN " +
        "(SELECT DISTINCT "+ TAG_POI + " FROM " + TAG_TABLE + " WHERE "+ TAG_NAME +" LIKE ?)";

        String[] whereArgs = new String[] {"2","%"+tag+"%"};

        this.openReadableDB();
        Cursor cursor = db.rawQuery(query,whereArgs);
//        Cursor cursor = db.query(POI_TABLE, null, where, whereArgs, null, null, null);
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
        long rowID = db.replace(POI_TABLE, null, cv);
//        long rowID = db.insert(POI_TABLE, null, cv);
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

    public void updateSyncDatetime(String datetime){
        ContentValues cv = new ContentValues();
        cv.put(SYNC_ID, "1");
        cv.put(SYNC_DATE, datetime);
        this.openWritableDB();
        db.replace(LAST_SYNC_TABLE, null, cv);
        closeDB();
    }

    public long insertTag(Tag t){
        ContentValues cv = new ContentValues();
        cv.put(TAG_ID, t.getId());
        cv.put(TAG_NAME, t.getName());
        cv.put(TAG_POI, t.getPoi());
        cv.put(TAG_FLAG, t.getFlag());

        this.openWritableDB();
        long rowID = db.replace(TAG_TABLE, null, cv);
//        long rowID = db.insert(POI_TABLE, null, cv);
        this.closeDB();

        return rowID;
    }

    public ArrayList<Tag> getTags(String poi){
        String where = TAG_POI + " = ? AND " + TAG_FLAG + " = ? ORDER BY "+ TAG_NAME;
        String[] whereArgs = new String[] {poi, "0"};
        this.openReadableDB();
        Cursor cursor = db.query(TAG_TABLE, null, where, whereArgs, null, null, null);
        ArrayList<Tag> tags = new ArrayList<Tag>();
        while(cursor.moveToNext()){
            tags.add(getTagFromCursor(cursor));
        }
        if(cursor != null)
            cursor.close();
        closeDB();
        return tags;
    }

    private static Tag getTagFromCursor(Cursor cursor){
        if(cursor == null || cursor.getCount() == 0){
            return null;
        }
        else{
            try{
                Tag tag = new Tag(
                        cursor.getString(TAG_ID_COL),
                        cursor.getString(TAG_NAME_COL),
                        cursor.getString(TAG_POI_COL),
                        cursor.getInt(TAG_FLAG_COL));
                return tag;
            }
            catch(Exception e){
                return null;
            }
        }
    }

    public ArrayList<POI> retrieveNearPoi(String type, PointF p1, PointF p2, PointF p3, PointF p4){
        String where = POI_STATUS + " = ? AND "
                + POI_TYPE + " = ? AND "
                + POI_LATITUDE + " > ? AND "
                + POI_LATITUDE + " < ? AND "
                + POI_LONGITUDE + " < ? AND "
                + POI_LONGITUDE + " > ?";
        String[] whereArgs = new String[] {"2", type,String.valueOf(p3.x),String.valueOf(p1.x),String.valueOf(p2.y),String.valueOf(p4.y)};

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

    private String includeAvailabilityQuery(){
        Calendar calendar = Calendar.getInstance();
        int time = getTime(calendar);
        return " AND "+ getDayColumn(getDay(calendar)) + " = 1 AND " +
                "'"+ (time < 10 ? "0" + time : time)
                + "00:00'" + " BETWEEN " + POI_OPENHOUR + " AND " + POI_CLOSEHOUR;
    }

    private String getDayColumn(int d){
        switch (d){
            case 1:
                return POI_SUNDAY;
            case 2:
                return POI_MONDAY;
            case 3:
                return POI_TUESDAY;
            case 4:
                return POI_WEDNESDAY;
            case 5:
                return POI_THURSDAY;
            case 6:
                return POI_FRIDAY;
            case 7:
                return POI_SATURDAY;
            default:
                return POI_SUNDAY;
        }
    }

    private int getDay(Calendar calendar){
        return calendar.get(Calendar.DAY_OF_WEEK); //Sunday starts with 1
    }

    private int getTime(Calendar calendar){
        return calendar.get(Calendar.HOUR_OF_DAY); //10pm is 22
    }

}
