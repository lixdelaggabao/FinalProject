package algonquin.cst2335.finalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.finalproject.databinding.ActivityDeezerSongSearchBinding;

public class DeezerSongSearch extends AppCompatActivity {

    public static String TAG = "Deezer";
    Button btnSearch;
    Button btnSaved;
    EditText etSearch;
    protected String searchTerm;
    protected RequestQueue queue = null;
    private SongDAO dDAO;
    private ArrayList<Song> definitions;
    private RecyclerView.Adapter myAdapter;
    private Executor thread;
    private int position;
    ActivityDeezerSongSearchBinding binding;
    DeezerViewModel DeezerModel;
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if ( item.getItemId() == R.id.item_1 ) {


            AlertDialog.Builder builder = new AlertDialog.Builder( DeezerSongSearch.this );
            builder.setMessage("Do you want to delete this Song?")
                    .setTitle("Question")
                    .setPositiveButton("Yes", ( dialog, cl ) -> {
                        Executor thread = Executors.newSingleThreadExecutor();
                        thread.execute(() -> {
                            Song w = definitions.get( position );
                            dDAO.deleteSong( w );
                            definitions.remove( w );
                            runOnUiThread(() -> myAdapter.notifyItemRemoved( position ));
                            Snackbar.make( findViewById(R.id.fragmentLocation ), "You deleted a Song", Snackbar.LENGTH_LONG)
                                    .setAction("Undo", click -> {
                                        definitions.add( w ); //w is the removed message, at position
                                        myAdapter.notifyItemInserted( position );
                                    })//end undo action
                                    .show();
                        });
                    })//end positive button
                    .setNegativeButton("No", ( dialog, cl ) -> { })
                    .create().show();

        }
        else if ( item.getItemId() == R.id.About) {

            //toast saying something about the version
            Context context = getApplicationContext();
            String text = "Ali Komijani";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else if ( item.getItemId() == R.id.Save ) {
            View view = findViewById(R.id.Save);
            //save the definition to the database
            thread = Executors.newSingleThreadExecutor();
            thread.execute( () -> {
                Song w = definitions.get( position );
                dDAO.insertSong( w );
                definitions.remove( position );
                runOnUiThread( () -> myAdapter.notifyItemRemoved( position ) );
                Snackbar.make ( view, "You inserted a song into the database", Snackbar.LENGTH_LONG).show();

            });
        }
        //close the fragment
        getSupportFragmentManager().popBackStack();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeezerSongSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        SongDatabase db = Room.databaseBuilder(getApplicationContext(), SongDatabase.class, "definitions").build();
        dDAO = db.songDAO();
        thread = Executors.newSingleThreadExecutor();  // Add this line to initialize the thread
        queue = Volley.newRequestQueue(this);

        DeezerModel = new ViewModelProvider(this).get(DeezerViewModel.class);
        definitions = DeezerModel.searchResults.getValue();

        DeezerModel.selectedSong.observe(this, (newDefinitionValue) -> {
            FragmentManager fMgr = getSupportFragmentManager();
            FragmentTransaction tx = fMgr.beginTransaction();
            SongDetailsFragment dictionaryFragment = new SongDetailsFragment(newDefinitionValue);
            getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentLocation, dictionaryFragment).commit();
        });

        btnSearch = binding.buttonSearch;
        etSearch = binding.editTextSearch;
        btnSaved = binding.btnSongSaved;

        SharedPreferences prefs = getSharedPreferences("Deezer", Context.MODE_PRIVATE);
        searchTerm = prefs.getString("searchSong", "");
        etSearch.setText(searchTerm);

        if (definitions == null) {
            definitions = new ArrayList<>();
            DeezerModel.searchResults.setValue(definitions);
        }

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnSearch.setOnClickListener(click -> {
            searchTerm = etSearch.getText().toString();
            String url = "https://api.deezer.com/search/artist/?q=" + searchTerm;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                definitions.clear();
                                JSONArray a = response.getJSONArray("data");
                                for (int i = 0; i < a.length(); i++) {
                                    JSONObject hobby = a.getJSONObject(i);
                                    String name = hobby.getString("name");
                                    String link = hobby.getString("link");
                                    Log.d(TAG, "Name: " + name);
                                    Log.d(TAG, "Link: " + link);

                                    Song artistSong = new Song(name, link);
                                    definitions.add(artistSong);
                                }

                                myAdapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle errors
                            // TODO: Handle the error response
                        }
                    }
            );

            queue.add(jsonObjectRequest);
        });

        btnSaved.setOnClickListener(clk -> {
            // Save each song to the database
            thread.execute(() -> {
                for (Song song : definitions) {
                    dDAO.insertSong(song);
                }

                // Clear the list (optional, depending on your use case)
                definitions.clear();

                // Update the RecyclerView
                runOnUiThread(() -> myAdapter.notifyDataSetChanged());
            });
        });

        binding.recyclerView.setAdapter(myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = getLayoutInflater().inflate(R.layout.song_list_item, parent, false);
                return new MyRowHolder(itemView);
            }

            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                Song obj = definitions.get(position);
                holder.txtTerm.setText(obj.getTitle());
                holder.txtDefinition.setText(obj.getArtist());
            }

            @Override
            public int getItemCount() {
                return definitions.size();
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }
        });
    }

    class MyRowHolder extends RecyclerView.ViewHolder {
        TextView txtDefinition;
        TextView txtTerm;

        public MyRowHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(clk -> {
                position = getAdapterPosition();
                Song selected = definitions.get(position);
                DeezerModel.selectedSong.postValue(selected);
            });

            txtDefinition = itemView.findViewById(R.id.titleTextView);
            txtTerm = itemView.findViewById(R.id.artistTextView);
        }
    }
}
