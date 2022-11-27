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
    EditText stopNumber;

    private static final String ESTIMATES_URL = "https://api.translink.ca/rttiapi/v1/stops";
    private static final String APP_ID = "H6I5JajNoTKkm7Ub2Wj0";

    private final ArrayList<String> busesDestination = new ArrayList<>();
    private final ArrayList<String> busesTime = new ArrayList<>();
    private final ArrayList<String> busPattern = new ArrayList<>();


    FirebaseAuth firebaseAuth;

    /**
     * The tag for the debug messages.
     */
    public final static String DEBUG_TAG = "SearchPageDebug";

    /**
     * The text typed into the stop search bar.
     */
    private String stop_search_query;

    /** The maximum length of a bus stop query. */
    private static final int MAX_STOP_QUERY_LENGTH = 5;

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
        getBuses();
        uploadSearchQueryToFireBase();
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

    /**
     * Wrapper function for the AsyncTaskRunner with error checking.
     */
    private void getBuses() {
        // TextViews.
        stopNumber = findViewById(R.id.searchPageStopNumber);

        // 53987

        String tempURL = "";
        String stopNo = stopNumber.getText().toString().trim();
        String routeNo = ""; // Deprecated.

        // Error messages.
        final String errorMSGStop = "Please enter a valid stop number.";

        if (stopNo.isEmpty()) {
            final Toast t = Toast.makeText(getApplicationContext(), errorMSGStop, Toast.LENGTH_SHORT);
            t.show();
            return;
        }

        // Error checking for STOPS.
        if (!stopNumber.getText().toString().isEmpty()) {
            // If the search term is more than 5 characters, exit the function with a message.
            if (stopNumber.getText().toString().length() > MAX_STOP_QUERY_LENGTH) {
                final Toast t = Toast.makeText(getApplicationContext(), errorMSGStop, Toast.LENGTH_SHORT);
                t.show();
                return;
            }

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
                final Toast t = Toast.makeText(getApplicationContext(), errorMSGStop, Toast.LENGTH_SHORT);
                t.show();
                return;
            }
        } else {
            // FAILURE: The field is empty.
            final Toast t = Toast.makeText(getApplicationContext(), errorMSGStop, Toast.LENGTH_SHORT);
            t.show();
        }


        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute(tempURL, stopNo, "");

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
            final String STOP_KEY = "stop";

            // Generate the path.
            final StringBuilder path = new StringBuilder();
            path.append(USERS_KEY)
                    .append("/")
                    .append(user.getUid())
                    .append("/")
                    .append(SEARCH_HISTORY_KEY)
                    .append("/");


            // Push to the "stop" tree
            if (stop_search_query != null) {
                path.append(STOP_KEY);
                // If the query is more than 5 characters, exit the function.
                if(stop_search_query.length() > MAX_STOP_QUERY_LENGTH) {
                    Log.d(DEBUG_TAG, "Invalid search query. Upload cancelled.");
                    return;
                }
                saveToFirebaseDatabase(path.toString(), stop_search_query);
            } else {
                Log.e(DEBUG_TAG, "ERROR: STOP QUERY IS NULL");
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

    /**
     * Retrieves the bus information.
     */
    private class AsyncTaskRunner extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            busesDestination.clear();
            busesTime.clear();
            busPattern.clear();
            RequestQueue queue = Volley.newRequestQueue(SearchPage.this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, strings[0],
                    response -> {
                        try {
                            XmlToJson xmlToJson = new XmlToJson.Builder(response).build();
                            JSONObject jsonObject = xmlToJson.toJson();
                            Log.d("resp", String.valueOf(jsonObject));

                            assert jsonObject != null;
                            JSONObject busesObj = jsonObject.getJSONObject("NextBuses");
                            try {
                                JSONArray nextBusObj = busesObj.getJSONArray("NextBus");

                                for (int i = 0; i < nextBusObj.length(); i++) {
                                    JSONObject schedulesObj = nextBusObj.getJSONObject(i).getJSONObject("Schedules");
                                    JSONArray busScheduleObj = schedulesObj.getJSONArray("Schedule");

                                    for (int j = 0; j < busScheduleObj.length(); j++) {
                                        String destination = busScheduleObj.getJSONObject(j).getString("Destination");
                                        String expectedLeaveTime = busScheduleObj.getJSONObject(j).getString("ExpectedLeaveTime");

                                        String pattern = busScheduleObj.getJSONObject(j).getString("Pattern");
                                        busesDestination.add(destination);
                                        busesTime.add(expectedLeaveTime);
                                        busPattern.add(pattern);
                                    }
                                    Bundle bundle = new Bundle();
                                    bundle.putStringArrayList("Destination", busesDestination);
                                    bundle.putStringArrayList("Times", busesTime);
                                    bundle.putStringArrayList("Pattern", busPattern);
                                    bundle.putString("StopNo", strings[1]);
                                    bundle.putString("RoutNo", strings[2]);

                                    Fragment results = new ResultsFragment();
                                    results.setArguments(bundle);
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.ctnFragment, results);
                                    fragmentTransaction.commit();
                                }
                            } catch (JSONException ex) {
                                JSONObject nextBusObj = busesObj.getJSONObject("NextBus");
                                JSONObject schedulesObj = nextBusObj.getJSONObject("Schedules");
                                JSONArray busScheduleObj = schedulesObj.getJSONArray("Schedule");

                                for (int i = 0; i < busScheduleObj.length(); i++) {
                                    String destination = busScheduleObj.getJSONObject(i).getString("Destination");
                                    String expectedLeaveTime = busScheduleObj.getJSONObject(i).getString("ExpectedLeaveTime");
                                    String pattern = busScheduleObj.getJSONObject(i).getString("Pattern");
                                    busesDestination.add(destination);
                                    busesTime.add(expectedLeaveTime);
                                    busPattern.add(pattern);
                                }
                                Bundle bundle = new Bundle();
                                bundle.putStringArrayList("Destination", busesDestination);
                                bundle.putStringArrayList("Times", busesTime);
                                bundle.putStringArrayList("Pattern", busPattern);
                                bundle.putString("StopNo", strings[1]);
                                bundle.putString("RoutNo", strings[2]);

                                Fragment results = new ResultsFragment();
                                results.setArguments(bundle);
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.ctnFragment, results);
                                fragmentTransaction.commit();
                            }

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