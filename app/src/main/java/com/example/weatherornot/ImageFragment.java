package com.example.weatherornot;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ImageFragment extends Fragment {

    private String destination, recTime, patternVal, lastUpd;
    private int imageId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            destination = getArguments().getString("destination");
            recTime = getArguments().getString("time");
            patternVal = getArguments().getString("pattern");
            lastUpd = getArguments().getString("lastUpdate");
            imageId = getArguments().getInt("image");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_image, container, false);
//        View myView = inflater.inflate(R.layout.fragment_frame_layout, container, false);
        TextView locName = myView.findViewById(R.id.destinationName);
        locName.setText(destination);

        TextView recordedTime = myView.findViewById(R.id.lastUpdatedTime);
        String arrive = "Arrival Time: " + recTime;
        recordedTime.setText(arrive);

        TextView pattern = myView.findViewById(R.id.busPattern);
        String busPattern = "Bus Pattern: " + patternVal;
        pattern.setText(busPattern);

        TextView lastUpdatedTime = myView.findViewById(R.id.routeNum);
        String lastUpdated = "Last Updated: " + lastUpd;
        lastUpdatedTime.setText(lastUpdated);
        Button btnBack = myView.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {
            Fragment fruit = new SearchFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.fade_in, // popEnter
                            R.anim.fade_out // exit
                    )
                    .replace(R.id.ctnFragment, fruit)
                    .addToBackStack(null)
                    .commit();
        });

        return myView;
    }


}