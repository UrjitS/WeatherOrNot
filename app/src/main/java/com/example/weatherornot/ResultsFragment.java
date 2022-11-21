package com.example.weatherornot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ResultsFragment extends Fragment implements ItemClickListener {

    RecyclerView recyclerView;
    ArrayList<String> destination, times, busPattern, bLastUpdate;
    int[] images;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        Bundle b = getArguments();
        destination = b.getStringArrayList("Destination");
        times = b.getStringArrayList("Times");
        busPattern = b.getStringArrayList("Pattern");
//        routeNum = b.getStringArrayList("RouteNumber");
        bLastUpdate = b.getStringArrayList("LastUpdate");

        images = new int[destination.size()];
        for (int i = 0; i < destination.size(); i++) {
            images[i] = R.drawable.translink_bus_image;
        }

        MyRecyclerViewAdapter myRecyclerViewAdapter = new
                MyRecyclerViewAdapter(getActivity(),
                destination, times, busPattern, bLastUpdate, images);
        myRecyclerViewAdapter.setClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(myRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onClick(View view, int position) {
        ImageFragment imageFragment = new ImageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("destination", destination.get(position));
        bundle.putString("time", times.get(position));
        bundle.putString("pattern", busPattern.get(position));
        bundle.putString("lastUpdate", bLastUpdate.get(position));
//        bundle.putString("direction", bDirection.get(position));
        bundle.putInt("image", images[position]);
        imageFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
//                .setCustomAnimations(
//                        R.anim.slide_in_right, // enter
//                        R.anim.slide_out_right // popExit
//                )
                .replace(R.id.ctnFragment, imageFragment)
                .commit();
    }
}
