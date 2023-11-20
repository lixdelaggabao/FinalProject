package algonquin.cst2335.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import algonquin.cst2335.finalproject.dictionary.dictionaryLookUp;
import algonquin.cst2335.finalproject.dictionary.dictionarySaved;

/**
 * the purpose of this class is to be the main driver class for a mobile dictionary app.
 */
public class Dictionary extends AppCompatActivity {

    Button btnLookup;
    Button btnSaved;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        //create an instance of the buttons for an on click listener
        btnLookup = findViewById(R.id.btnDictionaryLookup);
        btnSaved = findViewById(R.id.btnDictionarySaved);

        btnLookup.setOnClickListener( click -> {
            /*this button should check to see if the edit text field is blank first
            if it is not blank it should send the query to the api for the term to lookup

            for now just forward it to a page that contains a recycle view that has a layout for
            displaying a single searched term. This page should contain a button to save the search,
            as well as a button to search another item and bring them back to the main page
            */
            //on click, send them to the look up page
            Intent lookup = new Intent(this, dictionaryLookUp.class);
            //lookup.putExtra("term", findViewById(R.id.etDictionaryLookup));
            startActivity(lookup);
        });

        btnSaved.setOnClickListener( clk -> {
            /*
            This button should send the user to a page that has a recycleview that contains a list
            of all the saved searches. when they click on the saved search it should show a fragment
            that has all of the definitions inside of it.
            It should have the option to delete the saved search, or to view the definition. either take
            them to the other page that shows the defintions, or use a fragment.
             */

            //on click send them to the saved searches page
            Intent saved = new Intent(this, dictionarySaved.class);
            startActivity(saved);
        });

    } //end on create
} //end class Dictionary