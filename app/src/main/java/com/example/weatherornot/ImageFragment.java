package com.example.weatherornot;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weatherornot.ResultsFragment;

public class ImageFragment extends Fragment {

    private String planet;
    private int imageId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            planet = getArguments().getString("planet");
            imageId = getArguments().getInt("image");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_image, container, false);
        TextView locName = myView.findViewById(R.id.planetName);
        locName.setText(planet);
        ImageView imageView = myView.findViewById(R.id.planetImage);
        imageView.setImageResource(imageId);

        Button btnBack = myView.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {
            Fragment fruit = new SearchFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_left, // popEnter
                            R.anim.slide_out_right // exit
                    )
                    .replace(R.id.ctnFragment, fruit)
                    .addToBackStack(null)
                    .commit();
        });

        return myView;
    }


}