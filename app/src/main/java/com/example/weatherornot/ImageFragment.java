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
//        View myView = inflater.inflate(R.layout.fragment_frame_layout, container, false);
        TextView locName = myView.findViewById(R.id.planetName);
        locName.setText(planet);
        TextView currentWeather = myView.findViewById(R.id.temperature);
        String weth = "Current Temperature 12 degrees";
        currentWeather.setText(weth);
        ImageView imageView = myView.findViewById(R.id.planetImage);
        imageView.setImageResource(imageId);

        Button btnBack = myView.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {
            ResultsFragment galleryFragment = new ResultsFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.fade_in, // popEnter
                            R.anim.fade_out // exit
                    )
                    .replace(R.id.ctnFragment, galleryFragment)
//                    .addToBackStack(null)
                    .commit();
        });

        return myView;
    }


}