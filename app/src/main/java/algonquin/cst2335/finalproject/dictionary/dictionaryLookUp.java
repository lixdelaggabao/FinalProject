package algonquin.cst2335.finalproject.dictionary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import algonquin.cst2335.finalproject.R;

public class dictionaryLookUp extends AppCompatActivity {

    /*
    get the term passed in from the intent, then set it to a string variable called "lookup"

    Bundle getLookup = getIntent().getExtras();
    String lookup = getLookup.getString("term");
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary_look_up);

        //get the text view to show the term they searched for
        //TextView term = findViewById(R.id.txtDictionaryLookupTerm);
        //term.setText( lookup );
    }
}