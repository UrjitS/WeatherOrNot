package com.example.weatherornot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SignUpPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);
    }

    public void signUpAccount(View view) {
        Intent signUpUser = new Intent(SignUpPage.this, SearchPage.class);
        startActivity(signUpUser);
    }
}