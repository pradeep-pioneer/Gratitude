package com.pradeep.gratitude;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ChaptersActivity extends Activity {
    ProgressDialog barProgressDialog;
    Handler updateBarHandler;
    ArrayList<chapterObject> chapters;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapters);
        updateBarHandler = new Handler();
        checkData();
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        // TODO Auto-generated method stub
        if (arg1 == null) {
            LayoutInflater inflater = (LayoutInflater) ChaptersActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            arg1 = inflater.inflate(R.layout.chapters_listitem, arg2, false);
        }

        TextView heading = (TextView) arg1.findViewById(R.id.chapterHeadingTextView);

        chapterObject chapter = chapters.get(arg0);

        heading.setText(chapter.get_heading());
        //reflectionHeading.setText(reflection.getHeading());

        return arg1;
    }

    private void checkData(){
        final bigBookManuscriptDbHelper helper=new bigBookManuscriptDbHelper(this);
        chapters=helper.getAllChapters();
        if (chapters.size()==0){
            barProgressDialog = new ProgressDialog(ChaptersActivity.this);
            barProgressDialog.setTitle("Hang On - Adding Big Book...");
            barProgressDialog.setMessage("This too shall pass ...");
            barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
            barProgressDialog.setProgress(0);
            barProgressDialog.setMax(16);
            barProgressDialog.show();
            barProgressDialog.setCancelable(false);
            barProgressDialog.setCanceledOnTouchOutside(false);
            InputStream stream = getResources().openRawResource(R.raw.bigbook_output);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            //Read text from file
            final StringBuilder text = new StringBuilder();
            try {
                while ((line = reader.readLine()) != null) {
                    text.append(line);
                }
                reader.close();
            }
            catch (Exception ex){

            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{

                        String[] chaptersString=text.toString().split("\\~");
                        int count=0;
                        while (count<16){
                            String[] values=chaptersString[count].split("\\|");
                            helper.insertChapter(values[0],values[1]);
                            count++;
                            updateBarHandler.post(new Runnable() {
                                public void run() {
                                    barProgressDialog.incrementProgressBy(1);
                                }
                            });
                            if (count==16){
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
                    catch (Exception ex){

                    }
                }
            }).start();
        }
        else {

        }
    }
}
