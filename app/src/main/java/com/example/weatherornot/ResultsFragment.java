package com.example.weatherornot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ResultsFragment extends Fragment {

    RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.location_results_location_fragment, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        populate_weatherSpinner();
    }

    private void populate_weatherSpinner() {
        final String[] weatherArray = getResources().getStringArray(R.array.weather_types);
        final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, weatherArray);
        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        ((Spinner) this.getView().findViewById(R.id.searchPage_weatherSpinner)).setAdapter(stringArrayAdapter);
    }
}
