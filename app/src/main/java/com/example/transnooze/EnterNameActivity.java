package com.example.transnooze;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EnterNameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_enter_name);

        final String nameOfPlace = (String) getIntent().getExtras().get("nameOfPlace");

        Button button = findViewById(R.id.button);
        final EditText nameInputText = findViewById(R.id.nameInputText);

        //transfers given names back to MainActivity to be added into ListView
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nameToList = new Intent(getBaseContext(), MainActivity.class);
                nameToList.putExtra("nameOfPlace", nameOfPlace);
                nameToList.putExtra("mainName", nameInputText.getText().toString());
                startActivity(nameToList);
            }
        });


    }


}


