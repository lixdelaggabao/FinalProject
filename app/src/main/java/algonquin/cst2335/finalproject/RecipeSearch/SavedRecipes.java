package algonquin.cst2335.finalproject.RecipeSearch;

import androidx.activity.OnBackPressedCallback;
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
import androidx.room.Room;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import algonquin.cst2335.finalproject.MainActivity;
import algonquin.cst2335.finalproject.R;
import algonquin.cst2335.finalproject.databinding.ActivitySavedRecipesBinding;

public class SavedRecipes extends AppCompatActivity {
    private ActivitySavedRecipesBinding binding;
    private RecyclerView.Adapter recipeSearchAdapter;
    private ArrayList<Recipe> recipes;
    private RecipeDatabase recipeDatabase;
    private RecipeDAO recipeDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySavedRecipesBinding.inflate(getLayoutInflater());
        recipes = new ArrayList<>();

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        setupRecyclerView();

        View toolbarLogo = binding.toolbar.getChildAt(1);
        toolbarLogo.setOnClickListener(click -> {
            Intent recipeSearch = new Intent(SavedRecipes.this, RecipeSearch.class);
            startActivity(recipeSearch);
        });

        String deletedRecipe = getIntent().getStringExtra("deletedRecipe");

        if (deletedRecipe != null) {
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.recipe_search_delete_recipe_confirmation) + " " + deletedRecipe, Snackbar.LENGTH_LONG)
                    .show();

            disableBackButton();
        }

        loadDatabase();
        loadRecipes();
    }

    private void disableBackButton() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {}
        };

        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void loadDatabase() {
        recipeDatabase = Room.databaseBuilder(getApplicationContext(), RecipeDatabase.class, "recipe-database").build();
        recipeDAO = recipeDatabase.recipeDAO();
    }

    private void loadRecipes() {
        Executor recipesThread = Executors.newSingleThreadExecutor();
        recipesThread.execute(() ->
        {
            recipes.addAll(recipeDAO.getAllSavedRecipes());

            runOnUiThread(() ->  recipeSearchAdapter.notifyDataSetChanged());
        });
    }

    private void setupRecyclerView() {
        binding.savedRecipesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.savedRecipesRecyclerView.setAdapter(recipeSearchAdapter = new RecyclerView.Adapter<SavedRecipes.RecipeSearchHolder>() {
            @NonNull
            @Override
            public SavedRecipes.RecipeSearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recipe_preview, parent, false);

                return new SavedRecipes.RecipeSearchHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull SavedRecipes.RecipeSearchHolder holder, int position) {
                Recipe recipe = recipes.get(position);

                holder.recipeTitle.setText(recipe.getTitle());
                Picasso.get().load(recipe.getImageUrl()).into(holder.recipeImage);
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
        switch(item.getItemId()) {
            case R.id.home:
                Intent homeIntent = new Intent(SavedRecipes.this, MainActivity.class);
                startActivity(homeIntent);

                break;
            case R.id.viewRecipes:
                Intent savedRecipesIntent = new Intent(SavedRecipes.this, SavedRecipes.class);
                startActivity(savedRecipesIntent);

                break;
            case R.id.help:
                AlertDialog.Builder builder = new AlertDialog.Builder( SavedRecipes.this );
                builder.setTitle(getString(R.string.recipe_search_help_title))
                        .setMessage(getString(R.string.recipe_search_help_message))
                        .setPositiveButton(getString(R.string.recipe_search_help_button), (dialog, cl) -> {})
                        .create().show();

                break;
            case R.id.about:
                Toast.makeText(this, getString(R.string.recipe_search_about_message), Toast.LENGTH_SHORT).show();

                break;
            case R.id.saveRecipe:
            case R.id.deleteRecipe:
                return false;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_search_menu, menu);

        MenuItem saveItem = menu.findItem(R.id.saveRecipe);
        saveItem.setVisible(false);

        MenuItem deleteItem = menu.findItem(R.id.deleteRecipe);
        deleteItem.setVisible(false);

        MenuItem viewRecipesItem = menu.findItem(R.id.viewRecipes);
        viewRecipesItem.setVisible(false);

        return true;
    }

    class RecipeSearchHolder extends RecyclerView.ViewHolder {
        TextView recipeTitle;
        ImageView recipeImage;

        public RecipeSearchHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(click -> {
                int position = getAdapterPosition();
                Recipe selectedRecipe = recipes.get(position);
                RecipeDetailsFragment recipeFragment = new RecipeDetailsFragment(selectedRecipe);
                Bundle args = new Bundle();

                args.putString("CallingActivity", "SavedRecipes");
                recipeFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.recipeFragmentLocation, recipeFragment).commit();
            });

            recipeTitle = itemView.findViewById(R.id.previewRecipeTitle);
            recipeImage = itemView.findViewById(R.id.previewRecipeImage);
        }
    }
}