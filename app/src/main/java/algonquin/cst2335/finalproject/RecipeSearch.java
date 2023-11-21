package algonquin.cst2335.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecipeSearch extends AppCompatActivity {
    private RecyclerView.Adapter recipeSearchAdapter;
    private ArrayList<Recipe> recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_search);

        EditText searchEditText = findViewById(R.id.searchEditText);
        ImageButton searchButton = findViewById(R.id.searchButton);
        Toolbar toolbar = findViewById(R.id.toolbar);
        RecyclerView recipeSearchRecyclerView = findViewById(R.id.recipeSearchRecyclerView);
        SharedPreferences preferences = getSharedPreferences("RecipeSearchData", Context.MODE_PRIVATE);

        searchEditText.setText(preferences.getString("SearchText", ""));

        searchButton.setOnClickListener(click -> {
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString("SearchText", searchEditText.getText().toString());
            editor.apply();
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        recipeSearchRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        recipes = new ArrayList<>();
        recipes.add(new Recipe(1, "Sample Recipe", "@drawable/sample_recipe"));

        recipeSearchRecyclerView.setAdapter(recipeSearchAdapter = new RecyclerView.Adapter<RecipeSearchHolder>() {
            @NonNull
            @Override
            public RecipeSearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recipe_preview, parent, false);
                return new RecipeSearchHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull RecipeSearchHolder holder, int position) {
                holder.recipeTitle.setText("");
                holder.recipeImage.setImageResource(0);

                Recipe recipe = recipes.get(position);
                holder.recipeTitle.setText(recipe.getTitle());
                holder.recipeImage.setImageResource(R.drawable.sample_recipe);
            }

            @Override
            public int getItemCount() {
                return recipes.size();
            }

            @Override
            public int getItemViewType(int position){
                return 0;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.viewRecipes) {
            Intent intent = new Intent(RecipeSearch.this, AllRecipes.class);

            startActivity(intent);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.recipe_search_menu, menu);

        return true;
    }

    class RecipeSearchHolder extends RecyclerView.ViewHolder {
        TextView recipeTitle;
        ImageView recipeImage;

        public RecipeSearchHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(click -> {
                Intent intent = new Intent(RecipeSearch.this, RecipeDetails.class);

                startActivity(intent);
            });

            recipeTitle = itemView.findViewById(R.id.previewRecipeTitle);
            recipeImage = itemView.findViewById(R.id.previewRecipeImage);
        }
    }
}