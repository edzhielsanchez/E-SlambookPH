package com.example.e_slambookph;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button buttonCreate, buttonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonCreate = findViewById(R.id.btnCreate);
        buttonView = findViewById(R.id.btnView);

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the CreateActivity when buttonView is clicked
                startActivity(new Intent(MainActivity.this, CreateActivity.class));
            }
        });
        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the ListActivity when buttonView is clicked
                startActivity(new Intent(MainActivity.this, ListActivity.class));
            }
        });
    }
}