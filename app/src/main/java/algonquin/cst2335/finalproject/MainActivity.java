package algonquin.cst2335.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import algonquin.cst2335.finalproject.RecipeSearch.RecipeSearch;
import algonquin.cst2335.finalproject.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;

        if (id == R.id.sunriseAndSunsetLookup) {
            intent = new Intent(MainActivity.this, SunriseAndSunsetLookup.class);
        } else if (id == R.id.recipeSearch) {
            intent = new Intent(MainActivity.this, RecipeSearch.class);
        } else if (id == R.id.dictionary) {
            intent = new Intent(MainActivity.this, Dictionary.class);
        } else if (id == R.id.deezerSongSearch) {
            intent = new Intent(MainActivity.this, DeezerSongSearch.class);
        }

        startActivity(intent);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.application_menu, menu);

        return true;
    }
}