package com.pradeep.gratitude;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class quotesDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "quotes.db";
    public static final String QUOTES_TABLE_NAME = "quotes";
    public static final String QUOTES_COLUMN_ID = "id";
    public static final String QUOTES_COLUMN_HEADINGTEXT = "headingText";

    public quotesDbHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table quotes " +
                        "(id integer primary key AUTOINCREMENT, headingText text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public boolean addQuote(String quote){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(QUOTES_COLUMN_HEADINGTEXT,quote);
        db.insert(QUOTES_TABLE_NAME,null,contentValues);
        return true;
    }

    public String getRandomQuote()
    {
        String output="";
        Random number=new Random();
        int randomId=number.nextInt(164-1)+1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from quotes where id=" + String.valueOf(randomId), null );
        if(res.moveToFirst()) {
            output=res.getString(res.getColumnIndex(QUOTES_COLUMN_HEADINGTEXT));
        }
        return output;
    }

}
