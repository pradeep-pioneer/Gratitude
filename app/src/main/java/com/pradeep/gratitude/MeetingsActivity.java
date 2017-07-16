package com.pradeep.gratitude;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MeetingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetings);
        TextView tvLat=(TextView)findViewById(R.id.tvLat);
        TextView tvLng=(TextView)findViewById(R.id.tvLng);
        TextView tvMeetings = (TextView)findViewById(R.id.tvTotalMeetings);
        Location gps=getGPS();
        if (gps==null){
            Toast.makeText(this, "Please turn on Location!", Toast.LENGTH_LONG).show();
            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            this.startActivity(myIntent);
            gps=getGPS();
        }
        tvLat.setText("Latitude: " + Double.toString(gps.getLatitude()));
        tvLng.setText("Longitude: " + Double.toString(gps.getLongitude()));
        meetingLocationsDbHelper helper = new meetingLocationsDbHelper(this);
        final meetingsAdapter adapter;
        Intent intent=getIntent();
        int mode=intent.getIntExtra("Mode",0);
        ArrayList<meetingObject> meetings;
        if (mode==0){
            meetings = helper.getNearbyMeetings(gps,20000);
            adapter = new meetingsAdapter(meetings);
        }
        else {
            String keywords=intent.getStringExtra("Keywords");
            meetings = helper.getMeetingsByKeywords(keywords);
            adapter = new meetingsAdapter(meetings);
        }
        tvMeetings.setText("Total Meetings: " + meetings.size());
        ListView list = (ListView) findViewById(R.id.lvMeetings);
        list.setAdapter(adapter);
    }

    private Location getGPS() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);
        Location l = null;
        for (int i=providers.size()-1; i>=0; i--) {
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null) break;
        }
        return l;
    }

    public class meetingsAdapter extends BaseAdapter {

        ArrayList<meetingObject> meetingObjectList;

        public meetingsAdapter(ArrayList<meetingObject> dataSource) {
            meetingObjectList = dataSource;
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return meetingObjectList.size();
        }

        @Override
        public meetingObject getItem(int arg0) {
            // TODO Auto-generated method stub
            return meetingObjectList.get(arg0);
        }

        public  meetingObject getReflectionObject(int position){
            return meetingObjectList.get(position);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            // TODO Auto-generated method stub
            if (arg1 == null) {
                LayoutInflater inflater = (LayoutInflater) MeetingsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                arg1 = inflater.inflate(R.layout.meeting_listitem, arg2, false);
            }

            TextView tvGroupName=(TextView)arg1.findViewById(R.id.tvGroupName);
            TextView tvAddress=(TextView)arg1.findViewById(R.id.tvAddress);
            TextView tvFormattedAddress=(TextView)arg1.findViewById(R.id.tvFormattedAddress);
            TextView tvDayTime=(TextView)arg1.findViewById(R.id.tvDayTime);
            TextView tvContact=(TextView)arg1.findViewById(R.id.tvContact);
            TextView tvDistance=(TextView)arg1.findViewById(R.id.tvDistance);

            meetingObject meeting = meetingObjectList.get(arg0);

            tvGroupName.setText(meeting.get_groupName());
            tvAddress.setText(meeting.get_address());
            tvDayTime.setText("Day/Time: " + meeting.get_dayTime());
            tvContact.setText("Contact: " + meeting.get_contact());
            tvDistance.setText("Distance(Approx.): " + meeting.get_distance());
            tvFormattedAddress.setText(Html.fromHtml("<b>Full Address:<\\b><br/>" + meeting.get_formattedAddress()));
            return arg1;
        }
    }
}
