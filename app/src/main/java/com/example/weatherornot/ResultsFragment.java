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

public class ResultsFragment extends Fragment implements ItemClickListener {

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
//        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
//        recyclerView = view.findViewById(R.id.recyclerView);
//        planets = getResources().getStringArray(R.array.locations);
//        introductions = getResources().getStringArray(R.array.locations_description);
//
//        MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter(getActivity(), planets, introductions, images);
////        myRecyclerViewAdapter.setClickListener(this);
//        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
//        recyclerView.setAdapter(myRecyclerViewAdapter);
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        planets = getResources().getStringArray(R.array.locations);
        introductions = getResources().getStringArray(R.array.locations_description);

        MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter(getActivity(), planets, introductions, images);
        myRecyclerViewAdapter.setClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(myRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onClick(View view, int position) {
        ImageFragment imageFragment = new ImageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("planet", planets[position]);
        bundle.putInt("image", images[position]);
        imageFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
//                .setCustomAnimations(
//                        R.anim.slide_in_right, // enter
//                        R.anim.fade_out, // exit
//                        R.anim.fade_in, // popEnter
//                        R.anim.slide_out_right // popExit
//                )
                .replace(R.id.ctnFragment, imageFragment)
//                .addToBackStack(null)
                .commit();
    }
}
