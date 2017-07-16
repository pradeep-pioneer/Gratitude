package com.pradeep.gratitude;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class SearchMeetings extends AppCompatActivity {

    private static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 786;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_meetings);
        RadioButton defaultButton=(RadioButton)findViewById(R.id.rbLocation);
        defaultButton.setChecked(true);
        onRadionButtonClick(0);
    }

    public void onLocationClick(View view){
        onRadionButtonClick(0);
    }

    public void onRadionButtonClick(int button){
        if (button==0){
            if(Build.VERSION.SDK_INT>=23 && ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                EditText textbox=(EditText)findViewById(R.id.etKeywords);
                textbox.setEnabled(false);
                textbox.setText("Type your keywords");
            }
            else{
                try {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSION_ACCESS_COURSE_LOCATION);
                }
                catch (Exception ex){
                    Toast.makeText(this, ex.getMessage(),Toast.LENGTH_LONG);
                }
            }
        }
        else{
            EditText textbox=(EditText)findViewById(R.id.etKeywords);
            textbox.setEnabled(true);
            textbox.setText("");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_COURSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    EditText textbox=(EditText)findViewById(R.id.etKeywords);
                    textbox.setEnabled(false);
                    textbox.setText("Type your keywords");
                } else {
                    RadioButton rbKeywords = (RadioButton)findViewById(R.id.rbKeywords);
                    rbKeywords.setChecked(true);
                    onRadionButtonClick(1);
                }
                return;
            }
        }
    }

    public void onKeywordsClick(View view){
        onRadionButtonClick(1);
    }

    public void findMeetingsAction(View view){
        RadioButton defaultButton=(RadioButton)findViewById(R.id.rbLocation);
        if (defaultButton.isChecked()){
            Intent meetingActivityIntent=new Intent(this,MeetingsActivity.class);
            meetingActivityIntent.putExtra("Mode",0);
            startActivity(meetingActivityIntent);
        }
        else {
            EditText textbox=(EditText)findViewById(R.id.etKeywords);
            String keyWords=textbox.getText().toString();
            Intent meetingActivityIntent=new Intent(this,MeetingsActivity.class);
            meetingActivityIntent.putExtra("Mode",1);
            meetingActivityIntent.putExtra("Keywords",keyWords);
            startActivity(meetingActivityIntent);
        }
    }
}
