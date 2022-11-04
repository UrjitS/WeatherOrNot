package com.example.weatherornot;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        Fragment fruit = new ResultsFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.ctnFragment, fruit);
        fragmentTransaction.commit();
    }

    /**
     * Button handler for search button.
     */
    public void TestingAPIButtonHandler(View view) {
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
        Button button = findViewById(R.id.test_searchPage_button_search);
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
            JsonObjectRequest request =
                    new JsonObjectRequest(Request.Method.GET, strings[0],
                            null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray busesObj = response.getJSONArray("Buses");
                                JSONObject bus = busesObj.getJSONObject(0);
                                String direction = bus.getString("Direction");
                                String destination = bus.getString("Destination");
                                String pattern = bus.getString("Pattern");
                                int routeNo = bus.getInt("RouteNo");
                                int vehicleNo = bus.getInt("VehicleNo");

                                textView.setText("Bus details: " +
                                        "\nDirection: " + direction
                                        + "\nDestination: " +destination
                                        + "\nBus Pattern: " + pattern
                                        + "\nBus Route number: " + routeNo
                                        + "\nBus Vehicle number: "+ vehicleNo);
                            } catch (JSONException e) {
                                e.getMessage();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(SearchPage.this, error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
            queue.add(request);
            return null;
        }
    }
}