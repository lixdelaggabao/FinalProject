/**
 * SunriseAndSunsetLookup is an Android activity that allows users to look up sunrise and sunset times
 * for a specific latitude and longitude. It uses the SunriseSunset API to fetch the data and displays
 * the results on the UI. Users can also save the data to favorites or the local database.
 */
package algonquin.cst2335.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import algonquin.cst2335.finalproject.databinding.ActivitySunriseAndSunsetLookupBinding;

/**
 * The main activity class for the SunriseAndSunsetLookup app.
 */
public class SunriseAndSunsetLookup extends AppCompatActivity {
    /**
     * The request queue for making network requests using Volley.
     */
    RequestQueue queue = null;

    /**
     * The View Binding object for the activity layout.
     */
    ActivitySunriseAndSunsetLookupBinding binding;

    /**
     * The adapter for the RecyclerView.
     */
    private RecyclerView.Adapter myAdapter;

    /**
     * The ViewModel for managing data related to sunrise and sunset times.
     */
    SunriseAndSunsetViewModel sunriseModel;

    /**
     * The Data Access Object (DAO) for accessing the local database.
     */
    SunriseAndSunsetDAO sDAO;

    /**
     * The latitude for the sunrise and sunset lookup.
     */
    protected String latitude;

    /**
     * The longitude for the sunrise and sunset lookup.
     */
    protected String longitude;

    /**
     * Handles the selection of menu items in the app.
     *
     * @param item The selected menu item.
     * @return True if the menu item is handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.item_1) {
            // Handle "favorites" menu item
            Intent intent = new Intent(this, WeatherRoom.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.item_2) {
            // Handle "help" menu item
            // Add your help logic here
            showHelpSnackbar();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Initializes the contents of the Activity's standard options menu.
     *
     * @param menu The menu to be populated.
     * @return True if the menu should be displayed, false otherwise.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Called when the activity is first created. Responsible for initializing UI components,
     * setting up ViewModel, and handling user interactions.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down, this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SunriseAndSunsetDatabase db = Room.databaseBuilder(getApplicationContext(), SunriseAndSunsetDatabase.class, "SunriseAndSunsetDatabase").build();
        sDAO = db.sasDAO();
        sunriseModel = new ViewModelProvider(this).get(SunriseAndSunsetViewModel.class);
        sunriseModel.setSunriseAndSunsetDAO(sDAO);

        binding = ActivitySunriseAndSunsetLookupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences p = getSharedPreferences("LatitudeSearch", Context.MODE_PRIVATE);
        SharedPreferences p2 = getSharedPreferences("LongitudeSearch", Context.MODE_PRIVATE);
        binding.lantitudeEt.setText(p.getString("LatitudeSearch", ""));
        binding.longtitudeEt.setText(p2.getString("LongitudeSearch", ""));

        queue = Volley.newRequestQueue(this);

        binding.lookupBt.setOnClickListener(clk -> {
            latitude = binding.lantitudeEt.getText().toString();
            longitude = binding.longtitudeEt.getText().toString();
            SharedPreferences.Editor editor = p.edit();
            SharedPreferences.Editor editor2 = p2.edit();

            editor.putString("LatitudeSearch", binding.lantitudeEt.getText().toString());
            editor.apply();
            editor2.putString("LongitudeSearch", binding.longtitudeEt.getText().toString());
            editor2.apply();

            String stringURL = "https://api.sunrisesunset.io/json?lat=" + latitude + "&lng=" + longitude + "&timezone=UTC&date=today";

            Log.d("SunriseSunsetLookup", "Latitude: " + latitude);
            Log.d("SunriseSunsetLookup", "Longitude: " + longitude);
            Log.d("SunriseSunsetLookup", "URL: " + stringURL);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, stringURL, null,
                    (response) -> {
                        try {
                            JSONObject results = response.getJSONObject("results");
                            String sunrise = results.getString("sunrise");
                            String sunset = results.getString("sunset");

                            runOnUiThread(() -> {
                                binding.sunrise.setText(getString(R.string.sunrise_time, sunrise));
                                binding.sunrise.setVisibility(View.VISIBLE);

                                binding.sunset.setText(getString(R.string.sunset_time, sunset));
                                binding.sunset.setVisibility(View.VISIBLE);
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, (error) -> {
                Log.e("SunriseSunsetLookup", "Error: " + error.toString());
            });

            queue.add(request);
        });

        binding.saveToFavoriteBtn.setOnClickListener(clk -> {
            showSaveToDatabaseDialog();
        });

        setSupportActionBar(binding.toolbar);
    }

    /**
     * Saves the current sunrise and sunset data to the local database.
     */
    private void saveToFavorite() {
        String sunriseTime = binding.sunrise.getText().toString();
        String sunsetTime = binding.sunset.getText().toString();

        SunriseAndSunset3 sunriseAndSunset = new SunriseAndSunset3(sunriseTime, sunsetTime, latitude, longitude);
        sunriseAndSunset.setFavorite(true);

        new Thread(() -> {
            long id = sDAO.insertLocation(sunriseAndSunset);

            runOnUiThread(() -> {
                if (id != -1) {
                    Log.d("SunriseSunsetLookup", "Saved to favorite with ID: " + id);
                    showToast(getString(R.string.data_saved_to_favorites));
                } else {
                    Log.e("SunriseSunsetLookup", "Failed to save to favorite");
                    showToast(getString(R.string.failed_to_save_data_to_favorites));
                }
            });
        }).start();
    }

    /**
     * Displays a toast message on the UI.
     *
     * @param message The message to be displayed.
     */
    private void showToast(String message) {
        Toast.makeText(SunriseAndSunsetLookup.this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Displays a Snackbar with help information on the UI.
     */
    private void showHelpSnackbar() {
        Snackbar.make(binding.getRoot(), getString(R.string.app_help_message), Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Displays a dialog prompting the user to confirm saving data to the local database.
     */
    private void showSaveToDatabaseDialog() {
        String sunriseTime = binding.sunrise.getText().toString();
        String sunsetTime = binding.sunset.getText().toString();

        SunriseAndSunset3 sunriseAndSunset = new SunriseAndSunset3(sunriseTime, sunsetTime, latitude, longitude);
        sunriseAndSunset.setFavorite(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.save_to_database));
        builder.setMessage(getString(R.string.confirm_save_to_database));
        builder.setPositiveButton("OK", (dialog, which) -> {
            saveToDatabase(sunriseAndSunset);
        });
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
            // Handle cancel if needed
        });
        builder.show();
    }

    /**
     * Saves the provided sunrise and sunset data to the local database.
     *
     * @param sunriseAndSunset The data to be saved.
     */
    private void saveToDatabase(SunriseAndSunset3 sunriseAndSunset) {
        new Thread(() -> {
            long id = sDAO.insertLocation(sunriseAndSunset);

            runOnUiThread(() -> {
                if (id != -1) {
                    Log.d("SunriseSunsetLookup", "Saved to database with ID: " + id);
                    showToast(getString(R.string.data_saved_to_database));
                } else {
                    Log.e("SunriseSunsetLookup", "Failed to save to database");
                    showToast(getString(R.string.failed_to_save_data_to_database));
                }
            });
        }).start();
    }
}
