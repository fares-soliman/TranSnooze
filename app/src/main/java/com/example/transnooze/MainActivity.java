package com.example.transnooze;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.Arrays;


import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.functions.Consumer;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private TextView userTitle;
    private TextView locationNameByApi;
    private int counter = 0;
    private int loopCounter = 0;


    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        prefs = this.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        editor = prefs.edit();


        final ArrayList<HashMap<String,String>> locations = new ArrayList<>();

        super.onCreate(savedInstanceState);

        //permission for location
        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION) // ask single or multiple permission once
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) {
                            // All requested permissions are granted
                        } else {
                            rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION);
                        }
                    }
                });

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        userTitle = findViewById(R.id.userTitle);
        locationNameByApi = findViewById(R.id.locationNameByApi);


        final ListView locationListView = findViewById(R.id.locationListView);
        locationListView.setEmptyView(findViewById(R.id.emptyView));

        counter = prefs.getInt("counter", 0);

        //first launch or launched from home screen (Log.e statements kept for debugging and clarity)
        if (getIntent().getAction() == null) {
            Log.e("location", "added");
            String nameOfPlace = (String) getIntent().getExtras().get("nameOfPlace");
            String mainName = (String) getIntent().getExtras().get("mainName");

            Log.e("counter", String.valueOf(counter));

            editor.putString("mainName" + counter, mainName);
            editor.putString("nameOfPlace" + counter, nameOfPlace);

            editor.putInt("counter", counter + 1).apply();
            editor.putString("notfirstLaunch", "true").apply();
        }

        if (((prefs.getString("notfirstLaunch" , "false").equals("true")))) {
            counter = prefs.getInt("counter", 0);
            Log.e("loop", "started");
            while (loopCounter < counter) {
                HashMap<String, String> item = new HashMap<>();
                Log.e("loop", String.valueOf(loopCounter));
                Log.e("counter", String.valueOf(prefs.getInt("counter", 57)));
                Log.e("mainName", prefs.getString("mainName" + loopCounter, "NULL"));
                Log.e("nameOfPlace", prefs.getString("nameOfPlace" + loopCounter, "NULL"));

                item.put( "line1", prefs.getString("mainName" + loopCounter, "NULL"));
                item.put( "line2", prefs.getString("nameOfPlace" + loopCounter, "NULL"));
                locations.add( item );
                loopCounter++;
                Log.e("loop", String.valueOf(loopCounter));
                Log.e("counter", String.valueOf(prefs.getInt("counter", 57)));
            }
        }

        //Adapter for the ListView
        SimpleAdapter locationAdapter = new SimpleAdapter(this, locations, R.layout.white_text, new String[] { "line1","line2" }, new int[] {R.id.userTitle, R.id.locationNameByApi}){
            @Override
            public View getView (int position, View convertView, ViewGroup parent)
            {
                View v = super.getView(position, convertView, parent);

                Switch onOrOff=v.findViewById(R.id.switch1);
                onOrOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            // The toggle is enabled
                        } else {
                            // The toggle is disabled
                        }
                    }
                });
                return v;
            }
        };
        ((ListView)findViewById(R.id.locationListView)).setAdapter(locationAdapter);


        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent listToMap = new Intent(getBaseContext(), MapsActivity.class);
                startActivity(listToMap);

            }
        });
        locationListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                locations.remove(position);
                editor.putInt("counter", counter - 1).apply();
                finish();
                startActivity(getIntent());
                return false;
            }
        });

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
