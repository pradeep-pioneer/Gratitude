package com.pradeep.gratitude;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

public class SearchMeetings extends Activity {

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
            EditText textbox=(EditText)findViewById(R.id.etKeywords);
            textbox.setEnabled(false);
            textbox.setText("Type your keywords");
        }
        else{
            EditText textbox=(EditText)findViewById(R.id.etKeywords);
            textbox.setEnabled(true);
            textbox.setText("");
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
