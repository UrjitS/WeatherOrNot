package com.example.weatherornot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    EditText stopNumber, routeNumber;

    private final String ESTIMATES_URL = "https://api.translink.ca/rttiapi/v1/stops";
    private final String APP_ID = "H6I5JajNoTKkm7Ub2Wj0";

    private final ArrayList<String> busesDestination = new ArrayList<>();
    private final ArrayList<String> busesTime = new ArrayList<>();
    private final ArrayList<String> busPattern = new ArrayList<>();
    private final ArrayList<String> busLastUpdate = new ArrayList<>();


    FirebaseAuth firebaseAuth;

    public final static String DEBUG_TAG = "SearchPageDebug";

    /**
     * The text typed into either one of the search bars.
     */
    private String stop_search_query;
    private String route_search_query;

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
        uploadSearchQueryToFireBase();
        getBuses();
    }

    /**
     * for "SEE MAP" Button, go to google map API
     *
     * @param view Current view.
     */
    public void go_to_map(View view) {
        final Intent toMap = new Intent(this, GoogleMap.class);
        startActivity(toMap);
    }

    /**
     * Handler for the side nav bar.
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                Fragment fruit = new SearchFragment();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.ctnFragment, fruit);
                fragmentTransaction.commit();
                break;
            case R.id.nav_cycle:
                final Intent loginPage = new Intent(this, MainActivity.class);
                startActivity(loginPage);
                break;
            case R.id.nav_bus:
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

    /**
     * Wrapper function for the AsyncTaskRunner with error checking.
     */
    private void getBuses() {
        // TextViews.
        stopNumber = findViewById(R.id.searchPageStopNumber);
        routeNumber = findViewById(R.id.searchPageRouteNumber);

        // 53987

        String tempURL = "";
        String stopNo = stopNumber.getText().toString().trim();
        String routeNo = routeNumber.getText().toString().trim();

        // Error messages.
        final String errorMSGStop = "Please enter a valid stop number.";
        final String errorMSGRoute = "Please enter a valid route number.";

        // Error checking for STOPS.
        if (!stopNumber.getText().toString().isEmpty() && routeNumber.getText().toString().isEmpty()) {
            // If the search term is not a valid integer, exit function with message.
            try {
                Integer.parseInt(stopNo);
                //Integer.parseInt(routeNo);
                // SUCCESS: The search term is a valid integer.
                // Set the tempURL and proceed.
                // https://api.translink.ca/rttiapi/v1/stops/60980/estimates
                stop_search_query = stopNo;
                tempURL = ESTIMATES_URL + "/" + stopNo + "/estimates?apikey=" + APP_ID + "&routeNo=" + routeNo;
            } catch (NumberFormatException e) {
                // FAILURE: The search term is not a valid integer.
                final Toast t = Toast.makeText(getApplicationContext(), errorMSGStop, Toast.LENGTH_LONG);
                t.show();
                return;
            }
        }

        // Error checking for ROUTES.
        else if (!routeNumber.getText().toString().isEmpty() && stopNumber.getText().toString().isEmpty()) {
            // If the search term is not a valid integer, exit function with message.
            try {
                Integer.parseInt(routeNo);
                // SUCCESS: The search term is a valid integer.
                // Set the tempURL and proceed.
                route_search_query = routeNo;
                tempURL = ESTIMATES_URL + "/" + routeNo + "/estimates?apikey=" + APP_ID;
            } catch (NumberFormatException e) {
                // FAILURE: The search term is not a valid integer.
                final Toast t = Toast.makeText(getApplicationContext(), errorMSGRoute, Toast.LENGTH_LONG);
                t.show();
                return;
            }
        }

        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute(tempURL, stopNo, routeNo);

    }

    /**
     * If the user is logged in,
     * uploads the user's search query
     * onto the realtime database search history.
     */
    private void uploadSearchQueryToFireBase() {
        Log.d(DEBUG_TAG, "Running: uploadSearchQueryToFireBase()");

        // If the user is logged in, upload their query into the realtime database.
        // Else, do nothing.
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            final String emailMSG = "User " + user.getEmail() + " is logged in. Saving search query...";
            Log.d(DEBUG_TAG, emailMSG);

            // Keys for the database.
            final String USERS_KEY = "users";
            final String SEARCH_HISTORY_KEY = "search_history";
            final String ROUTE_OR_STOP;

            // Generate the path.
            final StringBuilder path = new StringBuilder();
            path.append(USERS_KEY)
                    .append("/")
                    .append(user.getUid())
                    .append("/")
                    .append(SEARCH_HISTORY_KEY)
                    .append("/");
            DatabaseReference myRef;

            // Push to either the "stop" or "route" tree.
            if (route_search_query != null) {
                ROUTE_OR_STOP = "route";
                path.append(ROUTE_OR_STOP);
                saveToFirebaseDatabase(path.toString(), stop_search_query);
            } else if (stop_search_query != null) {
                ROUTE_OR_STOP = "stop";
                path.append(ROUTE_OR_STOP);
                saveToFirebaseDatabase(path.toString(), stop_search_query);
            } else {
                Log.d(DEBUG_TAG, "ERROR: ROUTE AND STOP QUERY ARE BOTH NULL");
            }

        } else {
            Log.d(DEBUG_TAG, "User is not logged in. Exiting function...");
        }
    }

    /**
     * Uploads a given value to the given path on the Firebase Realtime Database.
     *
     * @param path  The path to upload to
     * @param value The value to set at the path
     */
    private static void saveToFirebaseDatabase(final String path, final String value) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(path);
        myRef.setValue(value);
        final String msg = "Uploaded value: " + value;
        Log.d(DEBUG_TAG, msg);
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

                            JSONObject busesObj = jsonObject.getJSONObject("NextBuses");
                            JSONObject nextBusObj = busesObj.getJSONObject("NextBus");
                            JSONObject schedulesObj = nextBusObj.getJSONObject("Schedules");
                            JSONArray busScheduleObj = schedulesObj.getJSONArray("Schedule");

                            for (int i = 0; i < busScheduleObj.length(); i++) {
                                String destination = busScheduleObj.getJSONObject(i).getString("Destination");
                                String expectedLeaveTime = busScheduleObj.getJSONObject(i).getString("ExpectedLeaveTime");
                                String lastTimeUpdate = busScheduleObj.getJSONObject(i).getString("LastUpdate");
                                String pattern = busScheduleObj.getJSONObject(i).getString("Pattern");
                                busesDestination.add(destination);
                                busesTime.add(expectedLeaveTime);
                                busPattern.add(pattern);
                                busLastUpdate.add(lastTimeUpdate);
                            }
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("Destination", busesDestination);
                            bundle.putStringArrayList("Times", busesTime);
                            bundle.putStringArrayList("Pattern", busPattern);
                            bundle.putStringArrayList("LastUpdate", busLastUpdate);
                            bundle.putString("StopNo", strings[1]);
                            bundle.putString("RoutNo", strings[2]);

                            Fragment results = new ResultsFragment();
                            results.setArguments(bundle);
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.ctnFragment, results);
                            fragmentTransaction.commit();
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