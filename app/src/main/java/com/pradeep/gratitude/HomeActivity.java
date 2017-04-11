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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
        InputStream stream = getResources().openRawResource(R.raw.output);
        InputStream meetingStream = getResources().openRawResource(R.raw.meetingdata);
        InputStream quotesStream = getResources().openRawResource(R.raw.quotesprocessed);
        final bigBookManuscriptDbHelper bigBookHelper=new bigBookManuscriptDbHelper(this);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        final reflectionsDbHelper helper=new reflectionsDbHelper(this);
        final BufferedReader meetingsReader = new BufferedReader(new InputStreamReader(meetingStream));
        final BufferedReader quotesReader = new BufferedReader(new InputStreamReader(quotesStream));
        final meetingLocationsDbHelper meetingsHelper=new meetingLocationsDbHelper(this);
        final quotesDbHelper quotesHelper=new quotesDbHelper(this);
        InputStream bigBookStream = getResources().openRawResource(R.raw.bigbook_output);
        final BufferedReader bigBookReader = new BufferedReader(new InputStreamReader(bigBookStream));
        String line;
        //Read text from file
        final StringBuilder text = new StringBuilder();
        try {
            while ((line = bigBookReader.readLine()) != null) {
                text.append(line);
            }
            bigBookReader.close();
        }
        catch (Exception ex){

        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Here you should write your time consuming task...
                    int count=0;
                    while (count<366) {
                        try{
                            String line = reader.readLine();

                            while (line != null) {
                                if (count==0)
                                    count++;
                                else {
                                    String[] values = line.split("\\|");
                                    helper.insertReflection(values[0], values[1], values[2], values[3], values[4]);

                                    count++;
                                }
                                line = reader.readLine();
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
                        reader.close();
                        updateBarHandler.post(new Runnable() {
                            public void run() {
                                barProgressDialog.setProgress(0);
                                barProgressDialog.setMax(164);
                                barProgressDialog.setTitle("Hang On - Quotes...");

                            }
                        });
                        count=0;
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
                                barProgressDialog.setMax(16);
                                barProgressDialog.setTitle("Hang On - Adding Big Book...");

                            }
                        });
                        count=0;
                        String[] chaptersString=text.toString().split("\\~");
                        while (count<16){
                            String[] values=chaptersString[count].split("\\|");
                            bigBookHelper.insertChapter(values[0],values[1]);
                            count++;
                            updateBarHandler.post(new Runnable() {
                                public void run() {
                                    barProgressDialog.incrementProgressBy(1);
                                }
                            });
                        }
                        updateBarHandler.post(new Runnable() {
                            public void run() {
                                barProgressDialog.setProgress(0);
                                barProgressDialog.setMax(1646);
                                barProgressDialog.setTitle("Hang On - Meetings...");

                            }
                        });
                        count=0;
                        while (count<=1646) {
                            try{
                                String line = meetingsReader.readLine();

                                while (line != null) {
                                    if (count==0)
                                        count++;
                                    else {
                                        meetingObject meeting = new meetingObject(line);
                                        meetingsHelper.insertMeeting(meeting);
                                        count++;
                                    }
                                    line = meetingsReader.readLine();
                                    updateBarHandler.post(new Runnable() {
                                        public void run() {
                                            barProgressDialog.incrementProgressBy(1);
                                        }
                                    });
                                    if(count==1646){
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
                        meetingsReader.close();

                    }
                } catch (Exception e) {
                }
            }
        }).start();

    }
    //End

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
    public void bigBookManuscriptAction(View view){
        Intent chaptersActivityIntent=new Intent(this,ChaptersActivity.class);
        startActivity(chaptersActivityIntent);
    }
}
