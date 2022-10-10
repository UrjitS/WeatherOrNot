package com.example.weatherornot;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class ResultsPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_page);

        ResultsPageFragment resultsPageFragment = new ResultsPageFragment();
        // obtain the manager for Fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        // FragmentTransaction is an API for performing a set of Fragment operations
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // replace an existing Fragment that was added to a container
        fragmentTransaction.replace(R.id.linear_layout, resultsPageFragment).commit();
        // an optional String tag name for the fragment can be added as the third parameter
        // to later retrieve the fragment with FragmentManager.findFragmentByTag(String)
//        fragmentTransaction.replace(R.id.linear_layout, fragment1, "myFragment1").commit();
    }
}