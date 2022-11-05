package com.example.weatherornot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.ArrayList;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class SearchPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    FragmentManager fragmentManager;
    EditText location;
    private final String url = "https://api.translink.ca/rttiapi/v1/buses";
    private final String appId = "H6I5JajNoTKkm7Ub2Wj0";
    private final String url2 = "http://api.translink.ca/rttiapi/v1/buses";
    TextView textView;
    boolean finishedSearch = false;
    private ArrayList<String> busesDestination = new ArrayList<>();
    private ArrayList<String> busesTime = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);


        fragmentManager = getSupportFragmentManager();

        Fragment fruit = new SearchFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.ctnFragment, fruit);
        fragmentTransaction.commit();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Button handler for search button.
     */
    public void searchButtonHandler(View view) {
//        final Intent res = new Intent(this, MapsActivity.class);
//        startActivity(res);
        getBuses();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                Toast.makeText(this, "Main Menu", Toast.LENGTH_SHORT).show();
                Fragment fruit = new SearchFragment();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.ctnFragment, fruit);
                fragmentTransaction.commit();
                break;
            case R.id.nav_cycle:
                Toast.makeText(this, "Login Page", Toast.LENGTH_SHORT).show();
                final Intent loginPage = new Intent(this, MainActivity.class);
                startActivity(loginPage);
                break;
            case R.id.nav_bus:
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                final Intent logoutPage = new Intent(this, MainActivity.class);
                startActivity(logoutPage);
                break;
            default:
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void BackToSearch(View view) {
        Toast.makeText(this, "Back", Toast.LENGTH_SHORT).show();
    }

    private void getBuses() {
        location = findViewById(R.id.searchPage_editTextLocation);
        textView = findViewById(R.id.result);
        textView.setText("");
        String tempURL = "";
        String stopNo = location.getText().toString().trim();
        if (stopNo.equals("")) {
            textView.setText("Destination field should not be empty");
        } else {
            //tempURL = url2 + "?apikey=" + appId;
            tempURL = url + "?apikey=" + appId + "&stopNo=" + stopNo;
        }
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute(tempURL);
    }

    private class AsyncTaskRunner extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            RequestQueue queue = Volley.newRequestQueue(SearchPage.this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, strings[0],
                    response -> {
                        try {
                            XmlToJson xmlToJson = new XmlToJson.Builder(response).build();
                            JSONObject jsonObject = xmlToJson.toJson();
                            Log.d("resp", String.valueOf(jsonObject));

                            JSONObject busesObj = jsonObject.getJSONObject("Buses");
                            JSONArray bus = busesObj.getJSONArray("Bus");
                            for (int i = 0; i < bus.length(); i++) {
                                String time = bus.getJSONObject(i).getString("RecordedTime");
                                String destination = bus.getJSONObject(i).getString("Destination");
//                                String pattern = bus.getJSONObject(i).getString("Pattern");
//                                String routeNo = bus.getJSONObject(i).getString("RouteNo");
//                                int vehicleNo = bus.getJSONObject(i).getInt("VehicleNo");
                                busesDestination.add(destination);
                                busesTime.add(time);
                            }
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("Destination", busesDestination);
                            bundle.putStringArrayList("Times", busesTime);
                            Fragment fruit = new ResultsFragment();
                            fruit.setArguments(bundle);
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.ctnFragment, fruit);
                            fragmentTransaction.commit();
//                            searchButtonHandler();
//                            Log.d("resp", direction);
//                            Log.d("resp", destination);
//                            Log.d("resp", pattern);
//                            Log.d("resp", routeNo);
//                            Log.d("resp", String.valueOf(vehicleNo));
//                            textView.setText("Bus details: " +
//                                    "\nDirection: " + direction
//                                    + "\nDestination: " +destination
//                                    + "\nBus Pattern: " + pattern
//                                    + "\nBus Route number: " + routeNo
//                                    + "\nBus Vehicle number: "+ vehicleNo);
                            finishedSearch = true;
                        } catch (JSONException e) {
                            e.getMessage();
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