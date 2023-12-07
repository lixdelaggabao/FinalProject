package algonquin.cst2335.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.finalproject.databinding.ActivityWeatherRoomBinding;
import algonquin.cst2335.finalproject.databinding.LatitudeAndLongitudeBinding;

/**
 * WeatherRoom is an Android activity that displays a list of favorite locations (latitude and longitude)
 * stored in the local database. Users can select a location from the list to view additional details
 * such as sunrise and sunset times in a separate fragment.
 */
public class WeatherRoom extends AppCompatActivity {

    /**
     * View Binding object for the activity layout.
     */
    ActivityWeatherRoomBinding binding;

    /**
     * List of favorite locations (latitude and longitude) retrieved from the local database.
     */
    ArrayList<SunriseAndSunset3> weather = new ArrayList<>();

    /**
     * ViewModel for managing data related to sunrise and sunset times.
     */
    SunriseAndSunsetViewModel viewModel;

    /**
     * RecyclerView adapter for displaying favorite locations in a list.
     */
    private RecyclerView.Adapter<MyRowHolder> myAdapter;

    /**
     * Data Access Object (DAO) for accessing the local database.
     */
    SunriseAndSunsetDAO sDAO;

    /**
     * The selected weather location for detailed view.
     */
    SunriseAndSunset3 selectedWeather;

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

        // Inflate the activity layout using View Binding
        binding = ActivityWeatherRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the Room database
        SunriseAndSunsetDatabase db = Room.databaseBuilder(getApplicationContext(), SunriseAndSunsetDatabase.class, "SunriseAndSunsetDatabase").build();
        sDAO = db.sasDAO();

        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(SunriseAndSunsetViewModel.class);
        viewModel.setSunriseAndSunsetDAO(sDAO);

        // Set up the RecyclerView and its adapter
        binding.deleteButton.setOnClickListener(click -> {
            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() -> {
                sDAO.deleteLocation(selectedWeather);

                runOnUiThread(() -> myAdapter.notifyDataSetChanged());
            });
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            /**
             * Called when RecyclerView needs a new ViewHolder of the given type to represent
             * an item.
             *
             * @param parent   The ViewGroup into which the new View will be added after it is bound
             *                 to an adapter position.
             * @param viewType The view type of the new View.
             * @return A new ViewHolder that holds a View of the given view type.
             */
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LatitudeAndLongitudeBinding binding = LatitudeAndLongitudeBinding.inflate(getLayoutInflater());
                return new MyRowHolder(binding.getRoot());
            }

            /**
             * Called by RecyclerView to display the data at the specified position. This method
             * should update the contents of the {@link MyRowHolder#itemView} to reflect the item at
             * the given position.
             *
             * @param holder   The ViewHolder which should be updated to represent the contents of the
             *                 item at the given position in the data set.
             * @param position The position of the item within the adapter's data set.
             */
            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                SunriseAndSunset3 locationInfo = weather.get(position);

                holder.locationInfo.setText(locationInfo.getLatitude() + " " + locationInfo.getLongitude());

                holder.itemView.setOnClickListener(view -> {
                    String latitude = viewModel.getFavoriteLocations().getValue().get(position).getSunrise();
                    String longitude = viewModel.getFavoriteLocations().getValue().get(position).getSunset();
                    SunriseAndSunset3 locInfo = new SunriseAndSunset3(viewModel.getFavoriteLocations().getValue().get(position).getSunrise(), viewModel.getFavoriteLocations().getValue().get(position).getSunset(), viewModel.getFavoriteLocations().getValue().get(position).getLatitude(), viewModel.getFavoriteLocations().getValue().get(position).getLongitude());
                    selectedWeather = locInfo;

                    SunriseSunsetFragment fragment = SunriseSunsetFragment.newInstance(latitude, longitude);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                });
            }

            /**
             * Returns the total number of items in the data set held by the adapter.
             *
             * @return The total number of items in this adapter.
             */
            @Override
            public int getItemCount() {
                return weather.size();
            }

            /**
             * Return the view type of the item at position for the purposes of view recycling.
             *
             * @param position Position to query
             * @return Integer value identifying the type of the view needed to represent the item at
             * position. Type codes need not be contiguous.
             */
            @Override
            public int getItemViewType(int position) {
                return 0;
            }
        });

        // Observe changes in the favorite locations LiveData and update the UI accordingly
        viewModel.getFavoriteLocations().observe(this, sunriseAndSunsetList -> {
            Log.d("WeatherRoom", "Observer triggered. List size: " + sunriseAndSunsetList.size());

            // Clear the existing weather list
            weather.clear();

            // Iterate through the LiveData list and create new SunriseAndSunset3 objects
            // with modified data for display
            for (SunriseAndSunset3 sunriseAndSunset : sunriseAndSunsetList) {
                SunriseAndSunset3 sunriseAndSunsetNew = new SunriseAndSunset3(
                        sunriseAndSunset.getSunrise(),
                        sunriseAndSunset.getSunset(),
                        sunriseAndSunset.getLatitude(),
                        sunriseAndSunset.getLongitude());

                weather.add(sunriseAndSunsetNew);
            }

            // Notify the adapter of changes in the data set
            myAdapter.notifyDataSetChanged();
        });
    }
}

/**
 * ViewHolder class for holding views of each item in the RecyclerView.
 */
class MyRowHolder extends RecyclerView.ViewHolder {
    TextView locationInfo;
    Button deleteButton;

    /**
     * Constructor for the ViewHolder.
     *
     * @param itemView The view associated with the ViewHolder.
     */
    public MyRowHolder(@NonNull View itemView) {
        super(itemView);
        locationInfo = itemView.findViewById(R.id.locationInfo);
        deleteButton = itemView.findViewById(R.id.deleteButton);
    }
}
