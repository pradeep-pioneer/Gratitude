package com.pradeep.gratitude;
import android.database.Cursor;

/**
 * Created by Pradeep on 5/11/2015.
 */
public class reflectionObject {
    private int _id=-1;
    private String _day="";
    private String _heading="";
    private String _sourceText="";
    private String _reflectionSource="";
    private String _reflectionText="";
    private boolean _isFavorite=false;
    public reflectionObject(int id, String day, String heading, String sourceText, String reflectionSource, String reflectionText, boolean isFavorite){
        _id=id;
        _day=day;
        _heading=heading;
        _sourceText=sourceText;
        _reflectionSource=reflectionSource;
        _reflectionText=reflectionText;
        _isFavorite=isFavorite;
    }

    public reflectionObject(int id, String day, String heading, String sourceText, String reflectionSource, String reflectionText, int isFavorite){
        _id=id;
        _day=day;
        _heading=heading;
        _sourceText=sourceText;
        _reflectionSource=reflectionSource;
        _reflectionText=reflectionText;
        _isFavorite=(isFavorite!=0);
    }

    public reflectionObject(Cursor result){
        _id=result.getInt(result.getColumnIndex(reflectionsDbHelper.REFLECTIONS_COLUMN_ID));
        _day=result.getString(result.getColumnIndex(reflectionsDbHelper.REFLECTIONS_COLUMN_DAY));
        _heading=result.getString(result.getColumnIndex(reflectionsDbHelper.REFLECTIONS_COLUMN_HEADINGTEXT));
        _sourceText=result.getString(result.getColumnIndex(reflectionsDbHelper.REFLECTIONS_COLUMN_SOURCETEXT));
        _reflectionSource=result.getString(result.getColumnIndex(reflectionsDbHelper.REFLECTIONS_COLUMN_REFLECTIONSOURCE));
        _reflectionText=result.getString(result.getColumnIndex(reflectionsDbHelper.REFLECTIONS_COLUMN_REFLECTIONTEXT));
        _isFavorite= (result.getInt(result.getColumnIndex(reflectionsDbHelper.REFLECTIONS_COLUMN_ISFAVORITE))!=0);
    }

    public int getId(){
        return _id;
    }

    public String getDay(){
        return _day;
    }

    public String getHeading(){
        return _heading;
    }

    public String getSourceText(){
        return _sourceText;
    }

    public String getReflectionSource(){
        return _reflectionSource;
    }

    public String getReflectionText(){
        return _reflectionText;
    }

    public boolean getIsFavorite(){return _isFavorite;}

}