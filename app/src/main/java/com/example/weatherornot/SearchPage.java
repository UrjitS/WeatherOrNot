package com.example.weatherornot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SearchPage extends AppCompatActivity {

    private Spinner weatherSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        weatherSpinner = findViewById(R.id.searchPage_weatherSpinner);

        populate_weatherSpinner();
    }

    /** Fills the weatherSpinner with different types of weather. */
    private void populate_weatherSpinner() {
        String[] weatherArray = getResources().getStringArray(R.array.weather_types);
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(SearchPage.this, android.R.layout.simple_spinner_item, weatherArray);
        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        weatherSpinner.setAdapter(stringArrayAdapter);
    }
}