package com.pradeep.gratitude;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MeetingMapActivity extends FragmentActivity
        implements OnMapReadyCallback {
    String _groupName;
    String _address;
    Double _lat;
    Double _lng;
    private GoogleMap _map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_map);
        Intent intent = getIntent();
        _groupName = intent.getStringExtra("group-name");
        _address = intent.getStringExtra("address");
        _lat = intent.getDoubleExtra("lat", 1);
        _lng = intent.getDoubleExtra("lng", 1);
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap map) {
        _map=map;
        LatLng current = new LatLng(_lat, _lng);
        _map.addMarker(new MarkerOptions()
                .position(current)
                .title(_groupName));
        _map.moveCamera(CameraUpdateFactory.newLatLng(current));
        _map.animateCamera(CameraUpdateFactory.zoomIn());
        _map.animateCamera(CameraUpdateFactory.zoomTo(15),2000, null);
    }
}
