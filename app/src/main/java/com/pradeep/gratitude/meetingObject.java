package com.pradeep.gratitude;

import android.database.Cursor;
import android.location.Location;

/**
 * Created by Pradeep on 6/8/2015.
 */
public class meetingObject {
    int _id;
    String _groupName;
    String _address;
    String _area;
    String _state;
    String _postCode;
    String _dayTime;
    String _contact;
    Double _exactLat;
    Double _exactLng;
    Location _location;

    String _distance;

    public String get_distance(){return _distance;}

    public void set_distance(String distance){_distance=distance;}

    public  int get_id(){
        return _id;
    }

    public String get_groupName(){
        return _groupName;
    }

    public String get_address(){
        return _address;
    }

    public String get_area(){
        return _area;
    }

    public String get_state(){
        return _state;
    }

    public String get_postCode(){
        return _postCode;
    }

    public String get_dayTime(){
        return _dayTime;
    }

    public String get_contact(){
        return _contact;
    }

    public Double get_exactLat(){
        return _exactLat;
    }

    public Double get_exactLng(){
        return _exactLng;
    }

    public Location get_location(){return _location;}


    public meetingObject(Cursor result)
    {
        _id=result.getInt(result.getColumnIndex(meetingLocationsDbHelper.MEETINGS_COLUMN_ID));
        _groupName=result.getString(result.getColumnIndex(meetingLocationsDbHelper.MEETINGS_COLUMN_GROUPNAME));
        _address=result.getString(result.getColumnIndex(meetingLocationsDbHelper.MEETINGS_COLUMN_ADDRESS));
        _area=result.getString(result.getColumnIndex(meetingLocationsDbHelper.MEETINGS_COLUMN_AREA));
        _state=result.getString((result.getColumnIndex(meetingLocationsDbHelper.MEETINGS_COLUMN_STATE)));
        _postCode=result.getString(result.getColumnIndex(meetingLocationsDbHelper.MEETINGS_COLUMN_POSTCODE));
        _dayTime=result.getString(result.getColumnIndex(meetingLocationsDbHelper.MEETINGS_COLUMN_DAY_TIME));
        _contact=result.getString(result.getColumnIndex(meetingLocationsDbHelper.MEETINGS_COLUMN_CONTACT));
        _exactLat=result.getDouble(result.getColumnIndex(meetingLocationsDbHelper.MEETINGS_COLUMN_EXACT_LAT));
        _exactLng=result.getDouble(result.getColumnIndex(meetingLocationsDbHelper.MEETINGS_COLUMN_EXACT_LNG));
        _location=new Location("");
        _location.setLatitude(_exactLat);
        _location.setLongitude(_exactLng);
    }

    public meetingObject(
            int  id,
            String groupName,
            String address,
            String area,
            String state,
            String postCode,
            String dayTime,
            String contact,
            Double exactLat,
            Double exactLng
    ){
        _id=id;
        _groupName=groupName;
        _address=address;
        _area=area;
        _state=state;
        _postCode=postCode;
        _dayTime=dayTime;
        _contact=contact;
        _exactLat=exactLat;
        _exactLng=exactLng;
        _location=new Location("");
        _location.setLatitude(_exactLat);
        _location.setLongitude(_exactLng);
    }

    public meetingObject(String csvLine){
        String[] values = csvLine.split("\\|");
        _id=Integer.parseInt(values[0].replace("\"",""));
        _groupName=values[1].replace("\"","");
        _address=values[2].replace("\"","");
        _area=values[3].replace("\"","");
        _state=values[4].replace("\"","");
        _postCode=values[5].replace("\"","");
        _dayTime=values[6].replace("\"","");
        _contact=values[7].replace("\"","");
        _exactLat=Double.parseDouble(values[10].replace("\"",""));
        _exactLng=Double.parseDouble(values[11].replace("\"",""));
        _location=new Location("");
        _location.setLatitude(_exactLat);
        _location.setLongitude(_exactLng);
    }
}
