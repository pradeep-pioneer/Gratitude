package com.pradeep.gratitude;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class HomeActivity extends Activity {
    ProgressDialog barProgressDialog;
    Handler updateBarHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        updateBarHandler = new Handler();
        checkData();
        setQuote();
    }
    @Override
    protected void onResume(){
        super.onResume();
        System.gc();
        setQuote();
    }


    private void setQuote(){
        quotesDbHelper quoteHelper=new quotesDbHelper(this);
        String quote=quoteHelper.getRandomQuote();
        TextView quoteText=(TextView)findViewById(R.id.textViewQuote);
        quoteText.setText(quote);
    }
    private void checkData(){
        reflectionsDbHelper helper=new reflectionsDbHelper(this);
        SimpleDateFormat dateFormat=new SimpleDateFormat("MMMM d");
        Calendar calendar=Calendar.getInstance();
        Date date=calendar.getTime();
        String day=dateFormat.format(date);
        reflectionObject reflection=helper.getReflection(day);
        if (reflection==null){
            addData();
        }
    }

    public void addData()
    {
        barProgressDialog = new ProgressDialog(HomeActivity.this);
        barProgressDialog.setTitle("Hang On - Reflections...");
        barProgressDialog.setMessage("This too shall pass ...");
        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(366);
        barProgressDialog.show();
        barProgressDialog.setCancelable(false);
        barProgressDialog.setCanceledOnTouchOutside(false);
        final reflectionsDbHelper helper=new reflectionsDbHelper(this);
        final meetingLocationsDbHelper meetingsHelper=new meetingLocationsDbHelper(this);
        final quotesDbHelper quotesHelper=new quotesDbHelper(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Here you should write your time consuming task...
                    int count=0;
                    BufferedReader reflectionReader = getReflectionsDataReader();
                    while (count<366) {
                        try{
                            String line = reflectionReader.readLine();

                            while (line != null) {
                                if (count==0)
                                    count++;
                                else {
                                    String[] values = line.split("\\|");
                                    helper.insertReflection(values[0], values[1], values[2], values[3], values[4]);

                                    count++;
                                }
                                line = reflectionReader.readLine();
                                updateBarHandler.post(new Runnable() {
                                    public void run() {
                                        barProgressDialog.incrementProgressBy(1);
                                    }
                                });
                            }
                        }
                        catch (Exception e){
                            String message=e.getMessage();
                        }
                        helper.close();
                        reflectionReader.close();
                        updateBarHandler.post(new Runnable() {
                            public void run() {
                                barProgressDialog.setProgress(0);
                                barProgressDialog.setMax(164);
                                barProgressDialog.setTitle("Hang On - Quotes...");

                            }
                        });
                        count=0;
                        BufferedReader quotesReader = getQuotesDataReader();
                        while (count<164) {
                            try {
                                String line = quotesReader.readLine();
                                quotesHelper.addQuote(line);

                                count++;
                                updateBarHandler.post(new Runnable() {
                                    public void run() {
                                        barProgressDialog.incrementProgressBy(1);
                                    }
                                });
                            } catch (Exception e) {
                                String message = e.getMessage();
                            }
                        }
                        quotesHelper.close();
                        quotesReader.close();
                        updateBarHandler.post(new Runnable() {
                            public void run() {
                                barProgressDialog.setProgress(0);
                                barProgressDialog.setMax(1727);
                                barProgressDialog.setTitle("Hang On - Meetings...");

                            }
                        });
                        count=0;
                        BufferedReader meetingsDataReader = getMeetingDataReader();
                        while (count<=1727) {
                            try{
                                String line = meetingsDataReader.readLine();

                                while (line != null) {
                                    if (count==0)
                                        count++;
                                    else {
                                        meetingObject meeting = new meetingObject(line);
                                        meetingsHelper.insertMeeting(meeting);
                                        count++;
                                    }
                                    line = meetingsDataReader.readLine();
                                    updateBarHandler.post(new Runnable() {
                                        public void run() {
                                            barProgressDialog.incrementProgressBy(1);
                                        }
                                    });
                                    if(count==1727){
                                        updateBarHandler.post(new Runnable() {
                                            public void run() {
                                                barProgressDialog.setCancelable(true);
                                                barProgressDialog.setCanceledOnTouchOutside(true);
                                                barProgressDialog.dismiss();
                                            }
                                        });
                                    }
                                }

                            }
                            catch (Exception e){
                                String message=e.getMessage();
                            }
                        }
                        meetingsHelper.close();
                        meetingsDataReader.close();

                    }
                } catch (Exception e) {
                }
            }
        }).start();

    }
    //End

    public BufferedReader getReflectionsDataReader(){
        try{
            String urlText = getString(R.string.reflections_url);
            URL url = new URL(urlText);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            InputStream stream = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            return reader;
        }
        catch (Exception ex){
            InputStream meetingStream = getResources().openRawResource(R.raw.output);
            BufferedReader reader = new BufferedReader(new InputStreamReader(meetingStream));
            return reader;
        }
    }

    public BufferedReader getMeetingDataReader(){
        try{
            String urlText = getString(R.string.meetings_url);
            URL url = new URL(urlText);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            InputStream stream = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            return reader;
        }
        catch (Exception ex){
            InputStream meetingStream = getResources().openRawResource(R.raw.meetingdata);
            BufferedReader reader = new BufferedReader(new InputStreamReader(meetingStream));
            return reader;
        }
    }

    public BufferedReader getQuotesDataReader(){
        try{
            String urlText = getString(R.string.quotes_url);
            URL url = new URL(urlText);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            InputStream stream = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            return reader;
        }
        catch (Exception ex){
            InputStream meetingStream = getResources().openRawResource(R.raw.quotesprocessed);
            BufferedReader reader = new BufferedReader(new InputStreamReader(meetingStream));
            return reader;
        }
    }

    public void dailyReflectionAction(View view){
        Intent reflectionIntent=new Intent(this,ReflectionActivity.class);
        reflectionIntent.putExtra(ReflectionActivity.INTENT_TYPE_DAY,"");
        startActivity(reflectionIntent);
    }

    public void favoriteReflectionAction(View view){
        Intent favReflectionIntent=new Intent(this,FavoriteReflections.class);
        favReflectionIntent.putExtra("Mode",0);
        startActivity(favReflectionIntent);
    }

    public void allReflectionAction(View view){
        Intent favReflectionIntent=new Intent(this,FavoriteReflections.class);
        favReflectionIntent.putExtra("Mode",1);
        startActivity(favReflectionIntent);
    }

    public void findMeetingsAction(View view){
        Intent meetingActivityIntent=new Intent(this,SearchMeetings.class);
        //meetingActivityIntent.putExtra("Mode",0);
        startActivity(meetingActivityIntent);
    }

    public void browseMeetingsAction(View view){
        Intent meetingActivityIntent=new Intent(this,MeetingsActivity.class);
        meetingActivityIntent.putExtra("Mode",1);
        meetingActivityIntent.putExtra("Keywords","");
        startActivity(meetingActivityIntent);
    }

    public void contactUsAction(View view){
        Intent mapActivityIntent=new Intent(this,ContactUsActivity.class);
        startActivity(mapActivityIntent);
    }
}
