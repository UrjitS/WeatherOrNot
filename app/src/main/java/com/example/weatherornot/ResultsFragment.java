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
    String[] planets, introductions;
    int[] images = {R.drawable.land, R.drawable.land, R.drawable.land,
            R.drawable.land, R.drawable.land, R.drawable.land, R.drawable.land,
            R.drawable.land};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        planets = getResources().getStringArray(R.array.locations);
        introductions = getResources().getStringArray(R.array.locations_description);

        MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter(getActivity(), planets, introductions, images);
//        myRecyclerViewAdapter.setClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(myRecyclerViewAdapter);

        return view;
    }
}
