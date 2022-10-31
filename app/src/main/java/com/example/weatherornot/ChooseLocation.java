package com.example.weatherornot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ChooseLocation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location);
    }

    public void loadRes(View view) {
        Intent signUpUser = new Intent(ChooseLocation.this, ResultsPage.class);
        startActivity(signUpUser);
    }
}