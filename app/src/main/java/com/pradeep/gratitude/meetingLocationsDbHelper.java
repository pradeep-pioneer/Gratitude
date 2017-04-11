package com.pradeep.gratitude;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import java.util.ArrayList;

/**
 * Created by Pradeep on 6/8/2015.
 */
public class meetingLocationsDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "meetings.db";
    public static final String MEETINGS_TABLE_NAME = "meetingLocations";
    public static final String MEETINGS_COLUMN_ID = "id";
    public static final String MEETINGS_COLUMN_GROUPNAME = "groupname";
    public static final String MEETINGS_COLUMN_ADDRESS = "address";
    public static final String MEETINGS_COLUMN_AREA = "area";
    public static final String MEETINGS_COLUMN_STATE = "state";
    public static final String MEETINGS_COLUMN_POSTCODE = "postcode";
    public static final String MEETINGS_COLUMN_DAY_TIME = "day_time";
    public static final String MEETINGS_COLUMN_CONTACT = "contact";
    public static final String MEETINGS_COLUMN_EXACT_LAT = "exact_lat";
    public static final String MEETINGS_COLUMN_EXACT_LNG = "exact_lng";

    //Constructor
    meetingLocationsDbHelper(Context context){super(context,DATABASE_NAME,null,1);}

    //Insert
    public boolean insertMeeting(meetingObject meeting){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(MEETINGS_COLUMN_ID,meeting.get_id());
        contentValues.put(MEETINGS_COLUMN_GROUPNAME,meeting.get_groupName());
        contentValues.put(MEETINGS_COLUMN_ADDRESS,meeting.get_address());
        contentValues.put(MEETINGS_COLUMN_AREA,meeting.get_area());
        contentValues.put(MEETINGS_COLUMN_STATE,meeting.get_state());
        contentValues.put(MEETINGS_COLUMN_POSTCODE,meeting.get_postCode());
        contentValues.put(MEETINGS_COLUMN_DAY_TIME,meeting.get_dayTime());
        contentValues.put(MEETINGS_COLUMN_CONTACT,meeting.get_contact());
        contentValues.put(MEETINGS_COLUMN_EXACT_LAT,meeting.get_exactLat());
        contentValues.put(MEETINGS_COLUMN_EXACT_LNG,meeting.get_exactLng());
        db.insert(MEETINGS_TABLE_NAME,null,contentValues);
        return true;
    }

    public ArrayList<meetingObject> getAllMeetings(){
        ArrayList<meetingObject> meetings=new ArrayList<meetingObject>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + MEETINGS_TABLE_NAME, null );
        if(res.moveToFirst()) {
            while (res.isAfterLast() == false) {
                meetings.add(new meetingObject(res));
                res.moveToNext();
            }
        }
        res.close();
        return  meetings;
    }

    public ArrayList<meetingObject> getNearbyMeetings(Location location, int radius){
        ArrayList<meetingObject> meetings=new ArrayList<meetingObject>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + MEETINGS_TABLE_NAME, null );
        if(res.moveToFirst()) {
            while (res.isAfterLast() == false) {
                meetingObject meeting = new meetingObject(res);
                double distance=location.distanceTo(meeting.get_location());
                meeting.set_distance(String.format("%.2f", distance) + " Meters");
                if (distance <= radius)
                    meetings.add(meeting);
                res.moveToNext();
            }
        }
        res.close();
        return  meetings;
    }
    public ArrayList<meetingObject> getMeetingsByKeywords(String keywords)
    {
        String[] keywordsArray=keywords.split("\\,");
        StringBuilder builder=new StringBuilder("select * from " + MEETINGS_TABLE_NAME);
        for (int i = 0; i < keywordsArray.length; i++) {
            if(i==0)
                builder.append(" WHERE (" + MEETINGS_COLUMN_GROUPNAME + " LIKE '%" + keywordsArray[i] + "%'");
            else
                builder.append(" OR (" + MEETINGS_COLUMN_GROUPNAME + " LIKE '%" + keywordsArray[i] + "%'");
            builder.append(" OR " + MEETINGS_COLUMN_ADDRESS + " LIKE '%" + keywordsArray[i] + "%'");
            builder.append(" OR " + MEETINGS_COLUMN_AREA + " LIKE '%" + keywordsArray[i] + "%'");
            builder.append(" OR " + MEETINGS_COLUMN_STATE + " LIKE '%" + keywordsArray[i] + "%'");
            builder.append(" OR " + MEETINGS_COLUMN_POSTCODE + " LIKE '%" + keywordsArray[i] + "%'");
            builder.append(" OR " + MEETINGS_COLUMN_CONTACT + " LIKE '%" + keywordsArray[i] + "%')");
        }
        ArrayList<meetingObject> meetings=new ArrayList<meetingObject>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery(builder.toString(), null );
        if(res.moveToFirst()) {
            while (res.isAfterLast() == false) {
                meetingObject meeting = new meetingObject(res);
                meetings.add(meeting);
                res.moveToNext();
            }
        }
        res.close();
        return  meetings;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + MEETINGS_TABLE_NAME
                + " (" + MEETINGS_COLUMN_ID + " integer"
                + ", " + MEETINGS_COLUMN_GROUPNAME + " text"
                + ", " + MEETINGS_COLUMN_ADDRESS + " text"
                + ", " + MEETINGS_COLUMN_AREA + " text"
                + ", " + MEETINGS_COLUMN_STATE + " text"
                + ", " + MEETINGS_COLUMN_POSTCODE + " text"
                + ", " + MEETINGS_COLUMN_DAY_TIME + " text"
                + ", " + MEETINGS_COLUMN_CONTACT + " text"
                + ", " + MEETINGS_COLUMN_EXACT_LAT + " real"
                + ", " + MEETINGS_COLUMN_EXACT_LNG + " real)"

        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MEETINGS_TABLE_NAME);
        onCreate(db);
    }
}
