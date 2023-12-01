package algonquin.cst2335.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

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

import algonquin.cst2335.finalproject.databinding.ActivityDictionaryBinding;
import algonquin.cst2335.finalproject.databinding.DictionaryRecycleDefinitionBinding;
import algonquin.cst2335.finalproject.dictionary.DictionaryDetailsFragment;
import algonquin.cst2335.finalproject.dictionary.dictionaryDAO;
import algonquin.cst2335.finalproject.dictionary.dictionaryDB;
import algonquin.cst2335.finalproject.dictionary.dictionaryDatabase;
import algonquin.cst2335.finalproject.dictionary.dictionaryVM;

/**
 * the purpose of this class is to be the main driver class for a mobile dictionary app.
 */
public class Dictionary extends AppCompatActivity {

    //for debugging in logcat
    public static String TAG = "dictionary";
    Button btnSearch;
    Button btnSaved;
    EditText etTerm;
    protected String searchTerm;
    protected RequestQueue queue = null;
    private dictionaryDAO dDAO;
    private ArrayList<dictionaryDB> definitions;
    private RecyclerView.Adapter myAdapter;
    private Executor thread;
    private int position;
    ActivityDictionaryBinding binding;
    dictionaryVM dictionaryModel;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if ( item.getItemId() == R.id.item_1 ) {

            //delete
            //switch statement not working using R.id, wants constant integer
            //create Alert Builder

            //dictionaryDB selected = definitions.get( position );

            //chatModel.selectedMessage.postValue(selected);

            //String messageText = "something";

            AlertDialog.Builder builder = new AlertDialog.Builder( Dictionary.this );
            builder.setMessage("Do you want to delete this definition?")
                    .setTitle("Question")
                    .setPositiveButton("Yes", ( dialog, cl ) -> {
                        Executor thread = Executors.newSingleThreadExecutor();
                        thread.execute(() -> {
                            dictionaryDB w = definitions.get( position );
                            dDAO.deleteDefinition( w );
                            definitions.remove( w );
                            runOnUiThread(() -> myAdapter.notifyItemRemoved( position ));
                            Snackbar.make( findViewById(R.id.fragmentLocation ), "You deleted a definition", Snackbar.LENGTH_LONG)
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
            String text = "Version 1.0 by Correy Wilkinson";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else if ( item.getItemId() == R.id.Save ) {
            View view = findViewById(R.id.Save);
            //save the definition to the database
            thread = Executors.newSingleThreadExecutor();
            thread.execute( () -> {
                dictionaryDB w = definitions.get( position );
                dDAO.insertDefinition( w );
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

        //setContentView(R.layout.activity_dictionary);
        //set up the variable binding
        binding = ActivityDictionaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        //set up the database connection, etc
        dictionaryDatabase db = Room.databaseBuilder(getApplicationContext(), dictionaryDatabase.class, "definitions").build();
        dDAO = db.dDAO();

        queue = Volley.newRequestQueue( this );

        //set up the view model
        dictionaryModel = new ViewModelProvider(this).get( dictionaryVM.class );
        definitions = dictionaryModel.dictionary.getValue();

        dictionaryModel.selectedTerm.observe(this, (newDefinitionValue) -> {
            FragmentManager fMgr = getSupportFragmentManager();
            FragmentTransaction tx = fMgr.beginTransaction();
            DictionaryDetailsFragment dictionaryFragment = new DictionaryDetailsFragment(newDefinitionValue);
            getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentLocation, dictionaryFragment).commit();

        });

        //create instances of the view elements
        btnSearch = binding.btnDictionarySearch;
        btnSaved = binding.btnDictionarySaved;
        etTerm = binding.etDictionarySearch;

        //set up the shared preferences, set search term = last value searched
        SharedPreferences prefs = getSharedPreferences("Dictionary", Context.MODE_PRIVATE );
        searchTerm = prefs.getString("searchTerm", "");
        Log.w(TAG, "In onCreate() shared pref for searchTerm is: "+ searchTerm );
        etTerm.setText( searchTerm );

        //check if the definitions is empty
        if(definitions == null) {
            //original was just this line
            //chatModel.messages.postValue( messages = new ArrayList<ChatMessage>());

            //instead of loading all the saved definitions here save that for the saved button
            //here run the query to the API for the last item searched and saved in the shared
            //preferences and display it

            dictionaryModel.dictionary.setValue( definitions = new ArrayList<>() );

            String stringURL = "";
            searchTerm = binding.etDictionarySearch.getText().toString();

            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, stringURL,
                    null, ( response ) -> {
                try {

                    //first clear the list so new searches appear at top
                    definitions.clear();
                    myAdapter.notifyDataSetChanged();

                    // this show the whole JSON object in the logcat
                    Log.w(TAG, response.toString());

                    // start parsing the JSON object
                    JSONObject obj = response.getJSONObject(0 );

                    // this will get the term they searched
                    String word = obj.getString("word" );
                    Log.w( TAG, "The word is: " + word ) ;

                    // this will get the meanings array inside the object, which contains the definitions
                    JSONArray meaningsArray = obj.getJSONArray("meanings");

                    // loop through the meanings array,
                    for ( int i = 0; i < meaningsArray.length(); i++ ) {
                        JSONObject meaning = meaningsArray.getJSONObject(i);
                        JSONArray definitionsArray = meaning.getJSONArray("definitions");
                        // loop through the definitions array, which is inside the meanings array.
                        for ( int j = 0; j < definitionsArray.length(); j++ ) {
                            JSONObject definition = definitionsArray.getJSONObject( j );
                            String definitionText = definition.getString("definition" );
                            Log.w( TAG, "Definition " + j + ": " + definitionText );

                            // create a new object using 2 arg constructor, add to definitions Array List
                            dictionaryDB w = new dictionaryDB( word, definitionText );
                            definitions.add( w );

                            // notify the adapter an item has been inserted
                            myAdapter.notifyItemInserted(definitions.size() - 1 );
                        }
                    }

                } catch ( JSONException e ) {
                    e.printStackTrace();
                }
            },
                    ( error ) -> {
                        error.printStackTrace();
                    });

            queue.add( request );
        }

        binding.recyclerView.setLayoutManager( new LinearLayoutManager (this ));

        // onClickListener for the Search Button
        btnSearch.setOnClickListener( click -> {

            String stringURL = "";

            // first this button should set the search term to the shared preferences field "searchTerm"
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString( "searchTerm", etTerm.getText().toString());
            editor.apply();

            // get the term from the edit text
            searchTerm = binding.etDictionarySearch.getText().toString();

            // send the API request using the term provided
            stringURL = "https://api.dictionaryapi.dev/api/v2/entries/en/" + searchTerm;

            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, stringURL,
                    null, ( response ) -> {
                try {

                    //first clear the list so new searches appear at top
                    definitions.clear();
                    myAdapter.notifyDataSetChanged();

                    // this show the whole JSON object in the logcat
                    Log.w(TAG, response.toString());

                    // start parsing the JSON object
                    JSONObject obj = response.getJSONObject(0 );

                    // this will get the term they searched
                    String word = obj.getString("word" );
                    Log.w( TAG, "The word is: " + word ) ;

                    // this will get the meanings array inside the object, which contains the definitions
                    JSONArray meaningsArray = obj.getJSONArray("meanings");

                    // loop through the meanings array,
                    for ( int i = 0; i < meaningsArray.length(); i++ ) {
                        JSONObject meaning = meaningsArray.getJSONObject(i);
                        JSONArray definitionsArray = meaning.getJSONArray("definitions");
                        // loop through the definitions array, which is inside the meanings array.
                        for ( int j = 0; j < definitionsArray.length(); j++ ) {
                            JSONObject definition = definitionsArray.getJSONObject( j );
                            String definitionText = definition.getString("definition" );
                            Log.w( TAG, "Definition " + j + ": " + definitionText );

                            // create a new object using 2 arg constructor, add to definitions Array List
                            dictionaryDB w = new dictionaryDB( word, definitionText );
                            definitions.add( w );

                            // notify the adapter an item has been inserted
                            myAdapter.notifyItemInserted(definitions.size() - 1 );
                        }
                    }

                } catch ( JSONException e ) {
                    e.printStackTrace();
                }
            },
                    ( error ) -> {
                        error.printStackTrace();
                    });

            queue.add( request );

        });

        //onClickListener for the Saved button
        btnSaved.setOnClickListener( clk -> {
            /*
            It should have the option to delete the saved search, or to view the definition. either take
            them to the other page that shows the defintions, or use a fragment.
             */
            //first clear the recycle view,
            definitions.clear();
            myAdapter.notifyDataSetChanged();

           Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() ->
            {
                // get all the messages from the database
                definitions.addAll( dDAO.getAllDefinitions() );

                // load them into the RecycleView
                runOnUiThread( () -> binding.recyclerView.setAdapter( myAdapter ));

            });

        });

        binding.recyclerView.setAdapter( myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
                if ( viewType == 0 ) {
                    DictionaryRecycleDefinitionBinding definitionBinding = DictionaryRecycleDefinitionBinding.inflate( getLayoutInflater() );
                    definitionBinding.getRoot().setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ));

                    return new MyRowHolder( definitionBinding.getRoot() );
                } else {
                    DictionaryRecycleDefinitionBinding definitionBinding = DictionaryRecycleDefinitionBinding.inflate( getLayoutInflater() );
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

                dictionaryDB obj = definitions.get( position );
                holder.txtTerm.setText( obj.getTerm() );
                holder.txtDefinition.setText( obj.getDefinition() );
            }

            @Override
            public int getItemCount() {
                return definitions.size();
            }

            @Override
            public int getItemViewType( int position ){
              /*
                if (messages.get(position).isSentButton()) {
                    return 0;
                } else {
                    return 1;
                }

               */
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
                dictionaryDB selected = definitions.get( position );

                dictionaryModel.selectedTerm.postValue( selected );
                //create Alert Builder

/*                AlertDialog.Builder builder = new AlertDialog.Builder( Dictionary.this );

                builder.setMessage( "Please select an option" )
                        .setTitle("Attention")
                        .setNeutralButton("Cancel", ( dialog, cl ) -> { }) //end cancel button


                        .setNegativeButton("Save", ( dialog, cl ) -> {
                            thread = Executors.newSingleThreadExecutor();
                            thread.execute( () -> {
                                dictionaryDB w = definitions.get( position );
                                dDAO.insertDefinition( w );
                                definitions.remove( position );
                                runOnUiThread( () -> myAdapter.notifyItemRemoved( position ) );
                                    Snackbar.make ( txtTerm, "You inserted a record into the database", Snackbar.LENGTH_LONG).show();
                            });

                        }) //end Save button

                        .setPositiveButton("Delete", ( dialog, cl ) -> {
                            thread = Executors.newSingleThreadExecutor();
                            thread.execute( () -> {
                                dictionaryDB w = definitions.get( position );
                                dDAO.deleteDefinition( w );
                                definitions.remove( position );
                                runOnUiThread( () -> myAdapter.notifyItemRemoved( position ) );
                                    Snackbar.make( txtTerm, "You deleted definition #"+ position, Snackbar.LENGTH_LONG )
                                        .setAction("Undo", click -> {
                                            definitions.add( position, w) ; //w is the removed message, at position
                                            myAdapter.notifyItemInserted( position );
                                        })//end undo action
                                        .show();
                            }); //end thread execute
                        })//end delete button

                        .create().show();*/

            }); //end itemview onclick listener

            txtDefinition = itemView.findViewById( R.id.dictionary_recycle_txtDefinition );
            txtTerm = itemView.findViewById( R.id.dictionary_recycle_txtTerm );
        }

    } //end on create
} //end class Dictionary