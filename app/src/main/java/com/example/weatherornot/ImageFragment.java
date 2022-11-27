package com.example.weatherornot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class ImageFragment extends Fragment implements OnMapReadyCallback {

    private String destination, recTime, patternVal, stopNo, routeNo;
    private int imageId;
    SupportMapFragment mapFragment;
    com.google.android.gms.maps.GoogleMap googleMapS;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            destination = getArguments().getString("destination");
            recTime = getArguments().getString("time");
            patternVal = getArguments().getString("pattern");

            imageId = getArguments().getInt("image");
            this.stopNo = getArguments().getString("StopNo");
            this.routeNo = getArguments().getString("RoutNo");
        }

    }

    @Override
    public void onMapReady(@NonNull com.google.android.gms.maps.GoogleMap googleMap) {
        googleMapS = googleMap;
        // Marker test. 49.2477085254479, -123.0038584025334
        LatLng comp3717Lecture = new LatLng(49.2477085254479, -123.0038584025334);
        googleMapS.addMarker(new MarkerOptions().position(comp3717Lecture).title("Stop ID: 52740"));
        googleMapS.moveCamera(CameraUpdateFactory.newLatLngZoom(comp3717Lecture, 15));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View myView = inflater.inflate(R.layout.fragment_image, container, false);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapAPI);
        mapFragment.getMapAsync(this);

        AsyncTaskRunner runner = new AsyncTaskRunner();
        String tempURL = "https://api.translink.ca/rttiapi/v1/stops/" + stopNo + "?apikey=H6I5JajNoTKkm7Ub2Wj0";
        runner.execute(tempURL);

        TextView locName = myView.findViewById(R.id.destinationName);
        locName.setText(destination);

        TextView timeC = myView.findViewById(R.id.busPattern2);
        String timeCtext = "Arrival Time: " + recTime;
        timeC.setText(timeCtext);


        TextView pattern = myView.findViewById(R.id.busPattern);
        String busPattern = "Bus Pattern: " + patternVal;
        pattern.setText(busPattern);


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
    @SuppressLint("StaticFieldLeak")
    private class AsyncTaskRunner extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            RequestQueue queue = Volley.newRequestQueue(getContext());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, strings[0],
                    response -> {
                        try {
                            XmlToJson xmlToJson = new XmlToJson.Builder(response).build();
                            JSONObject jsonObject = xmlToJson.toJson();
                            Log.d("resp", String.valueOf(jsonObject));

                            JSONObject busesObj = jsonObject.getJSONObject("Stop");
                            double longitude = busesObj.getDouble("Longitude");
                            double latitude = busesObj.getDouble("Latitude");

                            LatLng comp3717Lecture = new LatLng(latitude, longitude);
                            googleMapS.addMarker(new MarkerOptions().position(comp3717Lecture).title("Bus Stop"));
                            googleMapS.moveCamera(CameraUpdateFactory.newLatLngZoom(comp3717Lecture, 15));

                        } catch (JSONException e) {
                            System.out.println(e.getMessage());
                        }
                    }, error -> Log.d("resp", "hello"));

            queue.add(stringRequest);
            return null;
        }

        @Override
        protected void onPostExecute(String bitmap) {
            //super.onPostExecute(bitmap);

        }

    }
}