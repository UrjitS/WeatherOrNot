package com.example.weatherornot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;

public class SearchPage extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        populate_weatherSpinner();
    }

    /** Fills the weatherSpinner with different types of weather. */
    private void populate_weatherSpinner() {
        final String[] weatherArray = getResources().getStringArray(R.array.weather_types);
        final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(SearchPage.this, android.R.layout.simple_spinner_item, weatherArray);
        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        ((Spinner) findViewById(R.id.searchPage_weatherSpinner)).setAdapter(stringArrayAdapter);
    }

    /** Button handler for search button. */
    public void searchButtonHandler(View view) {
        final Intent i = new Intent(this, ChooseLocation.class);
        startActivity(i);
    }

    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        final Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

}