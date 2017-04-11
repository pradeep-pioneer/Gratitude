package com.pradeep.gratitude;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FavoriteReflections extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_reflections);
        reflectionsDbHelper helper = new reflectionsDbHelper(this);
        Intent intent=getIntent();
        int mode=intent.getIntExtra("Mode",0);
        final reflectionAdapter adapter;
        if(mode==0)
        adapter = new reflectionAdapter(helper.getFavoriteReflections());
        else
        adapter=new reflectionAdapter(helper.getAllReflections());

        ListView list = (ListView) findViewById(R.id.lvFavoriteReflections);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                reflectionObject reflection = adapter.getReflectionObject(arg2);
                Intent reflectionIntent=new Intent(FavoriteReflections.this,ReflectionActivity.class);
                reflectionIntent.putExtra(ReflectionActivity.INTENT_TYPE_DAY,reflection.getDay());
                startActivity(reflectionIntent);
            }
        });
    }

    public class reflectionAdapter extends BaseAdapter {

        ArrayList<reflectionObject> reflectionObjectList;

        public reflectionAdapter() {
            reflectionObjectList = new ArrayList<reflectionObject>();
        }

        public reflectionAdapter(ArrayList<reflectionObject> dataSource) {
            reflectionObjectList = dataSource;
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return reflectionObjectList.size();
        }

        @Override
        public reflectionObject getItem(int arg0) {
            // TODO Auto-generated method stub
            return reflectionObjectList.get(arg0);
        }

        public  reflectionObject getReflectionObject(int position){
            return reflectionObjectList.get(position);
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
                LayoutInflater inflater = (LayoutInflater) FavoriteReflections.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                arg1 = inflater.inflate(R.layout.favorite_reflection_listitem, arg2, false);
            }

            TextView reflectionDay = (TextView) arg1.findViewById(R.id.reflectionDay);
            TextView reflectionHeading = (TextView) arg1.findViewById(R.id.reflectionHeading);

            reflectionObject reflection = reflectionObjectList.get(arg0);

            reflectionDay.setText(reflection.getDay());
            reflectionHeading.setText(reflection.getHeading());

            return arg1;
        }
    }
}
