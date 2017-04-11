package com.pradeep.gratitude;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class reflectionsDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "reflections.db";
    public static final String REFLECTIONS_TABLE_NAME = "dailyReflections";
    public static final String REFLECTIONS_COLUMN_ID = "id";
    public static final String REFLECTIONS_COLUMN_HEADINGTEXT = "headingText";
    public static final String REFLECTIONS_COLUMN_SOURCETEXT = "sourceText";
    public static final String REFLECTIONS_COLUMN_REFLECTIONSOURCE = "reflectionSource";
    public static final String REFLECTIONS_COLUMN_REFLECTIONTEXT = "reflectionText";
    public static final String REFLECTIONS_COLUMN_DAY = "day";
    public static final String REFLECTIONS_COLUMN_ISFAVORITE = "isFavorite";

    public reflectionsDbHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table dailyReflections " +
                        "(id integer primary key, headingText text,sourceText text,reflectionSource text, reflectionText text,day text, isFavorite integer)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public boolean insertReflection  (String day, String headingText, String sourceText, String reflectionSource,String reflectionText)
    {
        Pattern unicodeOutliers = Pattern.compile("[^\\x00-\\x7F]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher headingMatcher = unicodeOutliers.matcher(headingText);
        Matcher sourceMatcher = unicodeOutliers.matcher(sourceText);
        Matcher reflectionMatcher = unicodeOutliers.matcher(reflectionText);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        contentValues.put(REFLECTIONS_COLUMN_HEADINGTEXT, headingMatcher.replaceAll(" "));
        contentValues.put(REFLECTIONS_COLUMN_SOURCETEXT, sourceMatcher.replaceAll(" "));
        contentValues.put(REFLECTIONS_COLUMN_REFLECTIONSOURCE, reflectionSource);
        contentValues.put(REFLECTIONS_COLUMN_REFLECTIONTEXT, reflectionMatcher.replaceAll(" "));
        contentValues.put(REFLECTIONS_COLUMN_DAY, day);
        contentValues.put(REFLECTIONS_COLUMN_ISFAVORITE,0);

        db.insert(REFLECTIONS_TABLE_NAME, null, contentValues);
        return true;
    }

    public reflectionObject getReflection(String day){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + REFLECTIONS_TABLE_NAME + " WHERE " + REFLECTIONS_COLUMN_DAY + " LIKE '%" + day + "%'", null );
        //Cursor res =  db.rawQuery( "select * from " + REFLECTIONS_TABLE_NAME + " WHERE id=132", null );
        if (res.moveToFirst()) {
            reflectionObject reflection = new reflectionObject(res);
            res.close();
            return  reflection;
        }
        else {
            res.close();
            return null;
        }
    }

    public ArrayList<reflectionObject> getAllReflections()
    {
        ArrayList<reflectionObject> array_list = new ArrayList<reflectionObject>();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from dailyReflections", null );
        if(res.moveToFirst()) {
            while (res.isAfterLast() == false) {
                array_list.add(new reflectionObject(res));
                res.moveToNext();
            }
        }
        res.close();
        return array_list;
    }

    public ArrayList<reflectionObject> getFavoriteReflections()
    {
        ArrayList<reflectionObject> array_list = new ArrayList<reflectionObject>();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from dailyReflections where isFavorite=1", null );
        if(res.moveToFirst()) {
            while (res.isAfterLast() == false) {
                array_list.add(new reflectionObject(res));
                res.moveToNext();
            }
        }
        res.close();
        return array_list;
    }

    public boolean markFavorite(int reflectionId){
        try{
            SQLiteDatabase db=this.getWritableDatabase();
            ContentValues values=new ContentValues();
            values.put(REFLECTIONS_COLUMN_ISFAVORITE,1);
            db.update(REFLECTIONS_TABLE_NAME,values,REFLECTIONS_COLUMN_ID+" = "+reflectionId,null);
            return true;
        }
        catch (Exception ex){
            return false;
        }
    }

    public boolean removeFavorite(int reflectionId){
        try{
            SQLiteDatabase db=this.getWritableDatabase();
            ContentValues values=new ContentValues();
            values.put(REFLECTIONS_COLUMN_ISFAVORITE,0);
            db.update(REFLECTIONS_TABLE_NAME,values,REFLECTIONS_COLUMN_ID+" = "+reflectionId,null);
            return true;
        }
        catch (Exception ex){
            return false;
        }
    }
}