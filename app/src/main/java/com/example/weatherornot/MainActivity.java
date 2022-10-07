package com.example.weatherornot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void continueAsGuest(View view) {
        Intent continueAsGuest = new Intent(MainActivity.this, SearchPage.class);
        startActivity(continueAsGuest);
    }

    public void loadSignUpPage(View view) {
        Intent loadSignUpPage = new Intent(MainActivity.this, SignUpPage.class);
        startActivity(loadSignUpPage);
    }

    public void loginButton(View view) {
        Intent loginUser = new Intent(MainActivity.this, SearchPage.class);
        startActivity(loginUser);
    }
}