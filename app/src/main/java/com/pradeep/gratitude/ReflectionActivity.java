package com.pradeep.gratitude;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ReflectionActivity extends ActionBarActivity {

    public final static String INTENT_TYPE_DAY="dayIntent";
    reflectionObject reflection;
    ProgressDialog barProgressDialog;
    Handler updateBarHandler;
    static final String SET_FAVORITE_TEXT="Add to Favorites";
    static final String REMOVE_FAVORITE_TEXT="Remove from Favorites";

    //Bulk Loading Stuff
    public void addData()
    {
        barProgressDialog = new ProgressDialog(ReflectionActivity.this);
        barProgressDialog.setTitle("Preparing Reflections...");
        barProgressDialog.setMessage("Setting Up ...");
        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(366);
        barProgressDialog.show();
        barProgressDialog.setCancelable(false);
        barProgressDialog.setCanceledOnTouchOutside(false);
        InputStream stream = getResources().openRawResource(R.raw.output);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        final reflectionsDbHelper helper=new reflectionsDbHelper(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Here you should write your time consuming task...
                    while (barProgressDialog.getProgress() <= barProgressDialog.getMax()) {
                        try{
                            String line = reader.readLine();
                            int count=0;
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

                        if (barProgressDialog.getProgress() == barProgressDialog.getMax()) {
                            barProgressDialog.dismiss();
                        }
                    }
                    SimpleDateFormat dateFormat=new SimpleDateFormat("MMMM dd");
                    Calendar calendar=Calendar.getInstance();
                    Date date=calendar.getTime();
                    String day=dateFormat.format(date).replace("0", "");
                    reflection=helper.getReflection(day);
                    LinearLayout content=(LinearLayout)findViewById(R.id.contentLayout);
                    content.post(new Runnable() {
                        @Override
                        public void run() {
                            setupTextView(reflection);
                        }
                    });
                } catch (Exception e) {
                }
            }
        }).start();

    }
    //End

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reflection);
        updateBarHandler = new Handler();
        Intent currentIntent=getIntent();
        String intentDay=currentIntent.getStringExtra(INTENT_TYPE_DAY);
        if (intentDay=="")
        setupView("");
        else
            setupView(intentDay);
    }

    private void setupView(String reflectionDay)
    {

        reflectionsDbHelper helper=new reflectionsDbHelper(this);
        SimpleDateFormat dateFormat=new SimpleDateFormat("MMMM d");
        Calendar calendar=Calendar.getInstance();
        Date date=calendar.getTime();
        String day;
        if (reflectionDay.isEmpty())
            day = dateFormat.format(date);//.replace("0", "");
        else
            day=reflectionDay;
        reflection=helper.getReflection(day);
        if (reflection==null){
            addData();

        }
        else {
            setupTextView(reflection);
        }
    }



    private void setupTextView(reflectionObject reflection){
        TextView headingText=(TextView)findViewById(R.id.headingTextView);
        TextView sourceText=(TextView)findViewById(R.id.sourceTextView);
        TextView reflectionSourceText=(TextView)findViewById(R.id.reflectionSourceTextView);
        TextView reflectionText=(TextView)findViewById(R.id.reflectionTextView);
        TextView copyrightText=(TextView)findViewById(R.id.copyrightTextView);

        //Set Title
        setTitle("Daily Reflection - " + reflection.getDay());

        //Set Heading Text
        SpannableString headingString=new SpannableString(reflection.getHeading());
        headingString.setSpan(new UnderlineSpan(), 0, headingString.length(), 0);
        headingString.setSpan(new StyleSpan(Typeface.BOLD), 0, headingString.length(), 0);
        headingText.setText(headingString);

        //Set source Text
        SpannableString sourceString=new SpannableString(reflection.getSourceText());
        sourceString.setSpan(new StyleSpan(Typeface.ITALIC), 0, sourceString.length(), 0);
        sourceText.setText(sourceString);

        //Set reflection Source Text
        reflectionSourceText.setText("- " + reflection.getReflectionSource());

        //Set reflection Text
        reflectionText.setText(reflection.getReflectionText());

        //Set copyright Text
        SpannableString copyrightString=new SpannableString("\nFrom the book Daily Reflections\n" +
                "Copyright © 1990 by Alcoholics Anonymous World Services, Inc.\n"+
                "Application Author: Pradeep S");
        copyrightString.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, copyrightString.length(), 0);
        copyrightText.setText(copyrightString);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reflection, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.exit) {
            this.finish();
            return true;
        }
        if (id==R.id.addFavorite){
            reflectionsDbHelper helper=new reflectionsDbHelper(this);
            String text= (String)item.getTitle();
            if (text==SET_FAVORITE_TEXT) {
                if (helper.markFavorite(reflection.getId())) {
                    Toast toast = Toast.makeText(this, "Successfully added to favorites", Toast.LENGTH_LONG);
                    toast.show();
                    reflection=helper.getReflection(reflection.getDay());

                } else {
                    Toast toast = Toast.makeText(this, "Unknown Error!", Toast.LENGTH_LONG);
                    toast.show();
                    reflection=helper.getReflection(reflection.getDay());
                }
            }
            else {
                if (helper.removeFavorite(reflection.getId())) {
                    Toast toast = Toast.makeText(this, "Successfully removed from favorites", Toast.LENGTH_LONG);
                    toast.show();
                    reflection=helper.getReflection(reflection.getDay());

                } else {
                    Toast toast = Toast.makeText(this, "Unknown Error!", Toast.LENGTH_LONG);
                    toast.show();
                    reflection=helper.getReflection(reflection.getDay());
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        MenuItem favorites=menu.getItem(0);
        if ((reflection.getIsFavorite()))
            favorites.setTitle(REMOVE_FAVORITE_TEXT);
        else
            favorites.setTitle(SET_FAVORITE_TEXT);

        return true;
    }
}
