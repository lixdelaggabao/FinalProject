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

/**
 * This class represents the activity for displaying the saved recipes.
 * @author Lixdel Louisse Aggabao
 * @version 1.0
 * @see Recipe
 * @see RecipeDAO
 * @see RecipeDatabase
 * @see RecipeSearch
 * @see RecipeDetailsFragment
 * @see RecipeSearchHolder
 * @since 20.0
 */
public class SavedRecipes extends AppCompatActivity {
    /**
     * The binding object for the layout of the SavedRecipes activity.
     */
    private ActivitySavedRecipesBinding binding;

    /**
     * The list to store the saved recipes.
     */
    private ArrayList<Recipe> recipes;

    /**
     * Adapter for the RecyclerView used in displaying the list of recipes.
     */
    private RecyclerView.Adapter recipeSearchAdapter;

    /**
     * The database used to store and retrieve recipes.
     */
    private RecipeDatabase recipeDatabase;

    /**
     * The Data Access Object for interacting with the database.
     */
    private RecipeDAO recipeDAO;

    /**
     * This method is called when the SavedRecipes activity is created.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize variables and components
        binding = ActivitySavedRecipesBinding.inflate(getLayoutInflater());
        recipes = new ArrayList<>();

        // Setup the content view
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        setupRecyclerView();

        // Setup the click listener for the toolbar logo
        View toolbarLogo = binding.toolbar.getChildAt(1);
        toolbarLogo.setOnClickListener(click -> {
            Intent recipeSearch = new Intent(SavedRecipes.this, RecipeSearch.class);
            startActivity(recipeSearch);
        });

        String deletedRecipe = getIntent().getStringExtra("deletedRecipe");

        // Show delete confirmation message and disable back button if there is a deleted recipe
        if (deletedRecipe != null) {
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.recipe_search_delete_recipe_confirmation) + " " + deletedRecipe, Snackbar.LENGTH_LONG)
                    .show();

            disableBackButton();
        }

        // Load resources to be used in the activity
        loadDatabase();
        loadRecipes();
    }

    /**
     * Disables the back button in this activity
     */
    private void disableBackButton() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {}
        };

        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    /**
     * Sets up the database for different operations.
     */
    private void loadDatabase() {
        recipeDatabase = Room.databaseBuilder(getApplicationContext(), RecipeDatabase.class, "recipe-database").build();
        recipeDAO = recipeDatabase.recipeDAO();
    }

    /**
     * Retrieves the details of all the recipes stored in the database.
     */
    private void loadRecipes() {
        Executor recipesThread = Executors.newSingleThreadExecutor();
        recipesThread.execute(() ->
        {
            recipes.addAll(recipeDAO.getAllSavedRecipes());

            runOnUiThread(() ->  recipeSearchAdapter.notifyDataSetChanged());
        });
    }

    /**
     * Sets up the RecyclerView with its adapter.
     */
    private void setupRecyclerView() {
        binding.savedRecipesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.savedRecipesRecyclerView.setAdapter(recipeSearchAdapter = new RecyclerView.Adapter<SavedRecipes.RecipeSearchHolder>() {
            /**
             * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
             * @param parent The ViewGroup into which the new View will be added after it is bound to
             *               an adapter position.
             * @param viewType The view type of the new View.
             * @return A new ViewHolder that holds a View of the given view type.
             */
            @NonNull
            @Override
            public SavedRecipes.RecipeSearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recipe_preview, parent, false);

                return new SavedRecipes.RecipeSearchHolder(view);
            }

            /**
             * Called by RecyclerView to display the data at the specified position.
             * @param holder The ViewHolder which should be updated to represent the contents of the
             *        item at the given position in the data set.
             * @param position The position of the item within the adapter's data set.
             */
            @Override
            public void onBindViewHolder(@NonNull SavedRecipes.RecipeSearchHolder holder, int position) {
                Recipe recipe = recipes.get(position);

                holder.recipeTitle.setText(recipe.getTitle());
                Picasso.get().load(recipe.getImageUrl()).into(holder.recipeImage);
            }

            /**
             * Returns the total number of items in the data set held by the adapter.
             * @return The total number of items in this adapter.
             */
            @Override
            public int getItemCount() {
                return recipes.size();
            }

            /**
             * Return the view type of the item at the specified position.
             * @param position The position to query.
             * @return An integer representing the view type of the item at the given position.
             */
            @Override
            public int getItemViewType(int position){
                return 0;
            }
        });
    }

    /**
     * Handles item selection in the options menu.
     * @param item The menu item that was selected.
     * @return True if the menu item selection is handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.home:
                // Open the home page activity
                Intent homeIntent = new Intent(SavedRecipes.this, MainActivity.class);
                startActivity(homeIntent);

                break;
            case R.id.viewRecipes:
                // Open the SavedRecipes activity
                Intent savedRecipesIntent = new Intent(SavedRecipes.this, SavedRecipes.class);
                startActivity(savedRecipesIntent);

                break;
            case R.id.help:
                // Display instructions on how to use the application
                AlertDialog.Builder builder = new AlertDialog.Builder( SavedRecipes.this );
                builder.setTitle(getString(R.string.recipe_search_help_title))
                        .setMessage(getString(R.string.recipe_search_help_message))
                        .setPositiveButton(getString(R.string.recipe_search_help_button), (dialog, cl) -> {})
                        .create().show();

                break;
            case R.id.about:
                // Display information about the owner of the application
                Toast.makeText(this, getString(R.string.recipe_search_about_message), Toast.LENGTH_SHORT).show();

                break;
            case R.id.saveRecipe:
            case R.id.deleteRecipe:
                return false;
        }

        return true;
    }

    /**
     * Initializes the contents of the options menu.
     * @param menu The options menu in which you place your items.
     * @return True for the menu to be displayed.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate custom menu resource
        getMenuInflater().inflate(R.menu.recipe_search_menu, menu);

        // Hide delete item
        MenuItem deleteItem = menu.findItem(R.id.deleteRecipe);
        deleteItem.setVisible(false);

        // Hide save item
        MenuItem saveItem = menu.findItem(R.id.saveRecipe);
        saveItem.setVisible(false);

        // Hide view recipes item
        MenuItem viewRecipesItem = menu.findItem(R.id.viewRecipes);
        viewRecipesItem.setVisible(false);

        return true;
    }

    /**
     * This class is the ViewHolder class for the RecyclerView.
     * @author Lixdel Louisse Aggabao
     * @version 1.0
     * @see SavedRecipes
     * @since 20.0
     */
    class RecipeSearchHolder extends RecyclerView.ViewHolder {
        /**
         * The TextView used for the recipe title in the recipe preview.
         */
        TextView recipeTitle;

        /**
         * The ImageView used for the recipe image in the recipe preview.
         */
        ImageView recipeImage;

        /**
         * Initializes the RecipeSearchHolder with the specified view.
         * @param itemView The view representing an individual item in the RecyclerView.
         */
        public RecipeSearchHolder(@NonNull View itemView) {
            super(itemView);

            // Setup click listener for the item
            itemView.setOnClickListener(click -> {
                int position = getAdapterPosition();
                Recipe selectedRecipe = recipes.get(position);
                RecipeDetailsFragment recipeFragment = new RecipeDetailsFragment(selectedRecipe);
                Bundle args = new Bundle();

                // Open fragment to display additional details of the selected recipe.
                args.putString("CallingActivity", "SavedRecipes");
                recipeFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.recipeFragmentLocation, recipeFragment).commit();
            });

            // Initialize TextViews in the item view
            recipeTitle = itemView.findViewById(R.id.previewRecipeTitle);
            recipeImage = itemView.findViewById(R.id.previewRecipeImage);
        }
    }
}