package com.pradeep.gratitude;

import android.database.Cursor;

/**
 * Created by Pradeep on 7/26/2015.
 */
public class chapterObject {
    private int _id=-1;
    private String _heading="";
    private String _contentText="";
    private boolean _isBookmarked=false;

    public  chapterObject(int id, String heading, String content, boolean isBookmarked){
        _id=id; _heading=heading; _contentText=content; _isBookmarked=isBookmarked;
    }
    public  chapterObject(Cursor result){
        _id=result.getInt(result.getColumnIndex(bigBookManuscriptDbHelper.MANUSCRIPT_COLUMN_ID));
        _heading=result.getString(result.getColumnIndex(bigBookManuscriptDbHelper.MANUSCRIPT_COLUMN_HEADING));
        _contentText=result.getString(result.getColumnIndex(bigBookManuscriptDbHelper.MANUSCRIPT_COLUMN_CONTENT));
        _isBookmarked=(result.getInt(result.getColumnIndex(bigBookManuscriptDbHelper.MANUSCRIPT_COLUMN_ISBOOKMARKED))!=0);
    }

    public int get_id(){
        return _id;
    }

    public String get_contentText(){
        return _contentText;
    }

    public boolean get_isBookmarked(){
        return _isBookmarked;
    }

    public String get_heading(){
        return _heading;
    }

}
