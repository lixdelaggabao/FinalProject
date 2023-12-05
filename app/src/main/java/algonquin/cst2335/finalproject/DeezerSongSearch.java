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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.finalproject.databinding.ActivityDeezerSongSearchBinding;

/**
 * the purpose of this class is to be the main driver class for a mobile dictionary app.
 */
public class DeezerSongSearch extends AppCompatActivity {

    //for debugging in logcat
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if ( item.getItemId() == R.id.item_1 ) {


            AlertDialog.Builder builder = new AlertDialog.Builder( DeezerSongSearch.this );
            builder.setMessage("Do you want to delete this item?")
                    .setTitle("Question")
                    .setPositiveButton("Yes", ( dialog, cl ) -> {
                        Executor thread = Executors.newSingleThreadExecutor();
                        thread.execute(() -> {
                            Song w = definitions.get( position );
                            dDAO.deleteSong( w );
                            definitions.remove( w );
                            runOnUiThread(() -> myAdapter.notifyItemRemoved( position ));
                            Snackbar.make( findViewById(R.id.fragmentLocation ), "You deleted a item", Snackbar.LENGTH_LONG)
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
            String text = "Version 1.1 by Ali";
            int duration = Toast.LENGTH_SHORT;

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
                Snackbar.make ( view, "You inserted a record into the database", Snackbar.LENGTH_LONG).show();

            });
        }
        //close the fragment
        getSupportFragmentManager().popBackStack();
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDeezerSongSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        //set up the database connection, etc
        SongDatabase db = Room.databaseBuilder(getApplicationContext(), SongDatabase.class, "definitions").build();
        dDAO = db.songDAO();

        queue = Volley.newRequestQueue( this );

        //set up the view model
        DeezerModel = new ViewModelProvider(this).get( DeezerViewModel.class );
        definitions = DeezerModel.searchResults.getValue();

        DeezerModel.selectedSong.observe(this, (newDefinitionValue) -> {
            FragmentManager fMgr = getSupportFragmentManager();
            FragmentTransaction tx = fMgr.beginTransaction();
            SongDetailsFragment dictionaryFragment = new SongDetailsFragment(newDefinitionValue);
            getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentLocation, dictionaryFragment).commit();

        });

        //create instances of the view elements
        btnSearch = binding.buttonSearch;
        etSearch = binding.editTextSearch;
        btnSaved = binding.btnSongSaved;

        //set up the shared preferences, set search term = last value searched
        SharedPreferences prefs = getSharedPreferences("Deezer", Context.MODE_PRIVATE );
        searchTerm = prefs.getString("searchSong", "");
        Log.w(TAG, "In onCreate() shared pref for searchSong is: "+ searchTerm );
        etSearch.setText( searchTerm );

        //check if the definitions is empty
        if(definitions == null) {


            DeezerModel.searchResults.setValue( definitions = new ArrayList<>() );

            String stringURL = "";
            searchTerm = binding.editTextSearch.getText().toString();
        }

        binding.recyclerView.setLayoutManager( new LinearLayoutManager (this ));

        // onClickListener for the Search Button
        btnSearch.setOnClickListener(click -> {

            String stringURL = "";

            // first this button should set the search term to the shared preferences field "searchTerm"
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("searchTerm", etSearch.getText().toString());
            editor.apply();

            // get the term from the edit text
            searchTerm = binding.editTextSearch.getText().toString();

            // send the API request using the term provided
            stringURL = "https://api.deezer.com/search/artist/?q=" + searchTerm;

            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, stringURL,
                    null, (response) -> {
                try {

                    //first clear the list so new searches appear at top
                    definitions.clear();
                    myAdapter.notifyDataSetChanged();

                    // this show the whole JSON object in the logcat
                    Log.w(TAG, response.toString());
                    JSONArray dataArray = response.getJSONArray(Integer.parseInt("data"));

                    // start parsing the JSON object
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject artistObject = dataArray.getJSONObject(i);
                        String name = artistObject.getString("name");
                        String link = artistObject.getString("link");
                        Log.d(TAG, "Name: " + name);
                        Log.d(TAG, "Link: " + link);

                        // Use the extracted values as needed
                        // For example, you can create a Song object with these values
                        Song artistSong = new Song(name, link);
                        definitions.add(artistSong);
                    }

                    // Notify the adapter that the dataset has changed
                    myAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            },
                    (error) -> {
                        error.printStackTrace();
                    });

            queue.add(request);

        });

        //onClickListener for the Saved button
        btnSaved.setOnClickListener( clk -> {

            definitions.clear();
            myAdapter.notifyDataSetChanged();

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() ->
            {
                // get all the messages from the database
                definitions.addAll( dDAO.getAllSongs() );

                // load them into the RecycleView
                runOnUiThread( () -> binding.recyclerView.setAdapter( myAdapter ));

            });

        });

        binding.recyclerView.setAdapter( myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
                if ( viewType == 0 ) {
                    ActivityDeezerSongSearchBinding definitionBinding = ActivityDeezerSongSearchBinding.inflate( getLayoutInflater() );
                    definitionBinding.getRoot().setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ));

                    return new MyRowHolder( definitionBinding.getRoot() );
                } else {
                    ActivityDeezerSongSearchBinding definitionBinding = ActivityDeezerSongSearchBinding.inflate( getLayoutInflater() );
                    definitionBinding.getRoot().setLayoutParams( new ViewGroup.LayoutParams (
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ));

                    return new MyRowHolder( definitionBinding.getRoot() );
                }
            }

            @Override
            public void onBindViewHolder( @NonNull MyRowHolder holder, int position ) {
                holder.txtDefinition.setText( "" );
                holder.txtTerm.setText( "" );

                Song obj = definitions.get( position );
                holder.txtTerm.setText( obj.getTitle() );
                holder.txtDefinition.setText( obj.getArtist() );
            }

            @Override
            public int getItemCount() {
                return definitions.size();
            }

            @Override
            public int getItemViewType( int position ){

                return 0;
            }
        });
    }

    class MyRowHolder extends RecyclerView.ViewHolder {
        TextView txtDefinition;
        TextView txtTerm;

        public MyRowHolder( @NonNull View itemView ) {
            super( itemView );

            //set an onclick listener for each item
            itemView.setOnClickListener( clk -> {
                position = getAdapterPosition();
                Song selected = definitions.get( position );

                DeezerModel.selectedSong.postValue( selected );
                //create Alert Builder

            }); //end itemview onclick listener

            txtDefinition = itemView.findViewById( R.id.titleTextView );
            txtTerm = itemView.findViewById( R.id.artistTextView );
        }

    }
}

