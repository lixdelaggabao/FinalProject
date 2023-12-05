package algonquin.cst2335.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button sunriseAndSunsetLookupBtn = findViewById(R.id.sunriseAndSunsetLookupBtn);
        Button recipeSearchBtn = findViewById(R.id.recipeSearchBtn);
        Button dictionaryBtn = findViewById(R.id.dictionaryBtn);
        Button deezerSongSearchBtn = findViewById(R.id.deezerSongSearchBtn);

        sunriseAndSunsetLookupBtn.setOnClickListener(click -> {
            Intent intent = new Intent(MainActivity.this, SunriseAndSunsetLookup.class);
            startActivity(intent);
        });

        recipeSearchBtn.setOnClickListener(click -> {
            Intent intent = new Intent(MainActivity.this, RecipeSearch.class);
            startActivity(intent);
        });

        dictionaryBtn.setOnClickListener(click -> {
            Intent intent = new Intent(MainActivity.this, Dictionary.class);
            startActivity(intent);
        });

        deezerSongSearchBtn.setOnClickListener(click -> {
            Intent intent = new Intent(MainActivity.this, DeezerSongSearch.class);
            startActivity(intent);
        });
    }
}