package com.pradeep.gratitude;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Pradeep on 7/26/2015.
 */
public class bigBookManuscriptDbHelper extends SQLiteOpenHelper  {
    public static final String DATABASE_NAME = "bb_manuscript.db";
    public static final String MANUSCRIPT_TABLE_NAME = "manuscript";
    public static final String MANUSCRIPT_COLUMN_ID = "id";
    public static final String MANUSCRIPT_COLUMN_HEADING = "heading";
    public static final String MANUSCRIPT_COLUMN_CONTENT = "content";
    public static final String MANUSCRIPT_COLUMN_ISBOOKMARKED = "isBookmarked";

    //Insert Data
    public boolean insertChapter(String heading, String content){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MANUSCRIPT_COLUMN_HEADING,heading);
        contentValues.put(MANUSCRIPT_COLUMN_CONTENT,content);
        db.insert(MANUSCRIPT_TABLE_NAME,null,contentValues);
        return true;
    }

    //GetList-All
    public ArrayList<chapterObject> getAllChapters(){
        ArrayList<chapterObject> array_list = new ArrayList<chapterObject>();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from manuscript", null );
        if(res.moveToFirst()) {
            while (res.isAfterLast() == false) {
                array_list.add(new chapterObject(res));
                res.moveToNext();
            }
        }
        res.close();
        return array_list;
    }

    //GetList-Bookmarked
    public ArrayList<chapterObject> getBookmarkedChapters(){
        ArrayList<chapterObject> array_list = new ArrayList<chapterObject>();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from manuscript where isBookmarked=1", null );
        if(res.moveToFirst()) {
            while (res.isAfterLast() == false) {
                array_list.add(new chapterObject(res));
                res.moveToNext();
            }
        }
        res.close();
        return array_list;
    }

    //GetChapter-ById
    public chapterObject getChapter(int id){
        chapterObject chapter=null;
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from manuscript where id=" + String.valueOf(id), null );
        if(res.moveToFirst()) {
            chapter = new chapterObject(res);
        }
        res.close();
        return chapter;
    }

    //GetChapter-Heading
    public chapterObject getChapter(String heading){
        chapterObject chapter=null;
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from manuscript where heading  LIKE '%" + heading + "%'", null );
        if(res.moveToFirst()) {
            chapter = new chapterObject(res);
        }
        res.close();
        return chapter;
    }

    //Constructor
    bigBookManuscriptDbHelper(Context context){super(context,DATABASE_NAME,null,1);}
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + MANUSCRIPT_TABLE_NAME
                        + " (" + MANUSCRIPT_COLUMN_ID + " integer"
                        + ", " + MANUSCRIPT_COLUMN_HEADING + " text"
                        + ", " + MANUSCRIPT_COLUMN_CONTENT + " text"
                        + ", " + MANUSCRIPT_COLUMN_ISBOOKMARKED + " integer)"

        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MANUSCRIPT_TABLE_NAME);
        onCreate(db);
    }
}
