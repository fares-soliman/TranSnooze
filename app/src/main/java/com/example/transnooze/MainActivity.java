package com.example.transnooze;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    String[][] locations = new String[][] {{"Work", "Union Station"}, {"Coming Home", "998 Pine Valley Circle"}, {"Out with the boys", "966 Sheridan Way"}, {"haha", "maha"}, {"caha", "tafa"}};

    ListView locationListView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        locationListView = findViewById(R.id.locationListView);
        locationListView.setEmptyView(findViewById(R.id.emptyView));

        ArrayList<HashMap<String,String>> list = new ArrayList<>();

        for(int i=0;i<locations.length;i++){
            HashMap<String, String> item = new HashMap<>();
            item.put( "line1", locations[i][0]);
            item.put( "line2", locations[i][1]);
            list.add( item );
        }

        SimpleAdapter locationAdapter = new SimpleAdapter(this, list, R.layout.white_text, new String[] { "line1","line2" }, new int[] {R.id.text1, R.id.text2});
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
    }

}
