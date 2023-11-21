package algonquin.cst2335.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AllRecipes extends AppCompatActivity {
    private RecyclerView.Adapter recipeSearchAdapter;
    private ArrayList<Recipe> recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_recipes);

        RecyclerView allRecipesRecyclerView = findViewById(R.id.allRecipesRecyclerView);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        allRecipesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        recipes = new ArrayList<>();
        recipes.add(new Recipe(1, "Sample Recipe", "@drawable/sample_recipe"));

        allRecipesRecyclerView.setAdapter(recipeSearchAdapter = new RecyclerView.Adapter<AllRecipes.RecipeSearchHolder>() {
            @NonNull
            @Override
            public AllRecipes.RecipeSearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recipe_preview, parent, false);
                return new AllRecipes.RecipeSearchHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull AllRecipes.RecipeSearchHolder holder, int position) {
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

    class RecipeSearchHolder extends RecyclerView.ViewHolder {
        TextView recipeTitle;
        ImageView recipeImage;

        public RecipeSearchHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(click -> {
                Intent intent = new Intent(AllRecipes.this, RecipeDetails.class);

                startActivity(intent);
            });

            recipeTitle = itemView.findViewById(R.id.previewRecipeTitle);
            recipeImage = itemView.findViewById(R.id.previewRecipeImage);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.viewRecipes) {
            Intent intent = new Intent(AllRecipes.this, AllRecipes.class);

            startActivity(intent);
        } else if (id == R.id.deleteRecipe) {
            AlertDialog.Builder builder = new AlertDialog.Builder( AllRecipes.this );
            builder.setMessage("Do you want to delete the message: ")
                    .setTitle("Question:")
                    .setNegativeButton("No", (dialog, cl) -> {})
                    .setPositiveButton("Yes", (dialog, cl) -> {
                        Snackbar.make(findViewById(android.R.id.content), "You deleted message #", Snackbar.LENGTH_LONG)
                                .setAction("Undo", click -> {

                                })
                                .show();
                    })
                    .create().show();
        } else if (id == R.id.about) {
            Toast.makeText(this, "Version 1.0, created by Lixdel Louisse Aggabao", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_search_menu, menu);

        return true;
    }
}