package algonquin.cst2335.finalproject.RecipeSearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import algonquin.cst2335.finalproject.MainActivity;
import algonquin.cst2335.finalproject.R;
import algonquin.cst2335.finalproject.databinding.ActivityRecipeSearchBinding;

/**
 * This class represents the activity for searching and displaying recipes using the Spoonacular API.
 * @author Lixdel Louisse Aggabao
 * @version 1.0
 * @see Recipe
 * @see RecipeDAO
 * @see RecipeDatabase
 * @see RecipeDetailsFragment
 * @see SavedRecipes
 * @see RecipeSearchHolder
 * @since 20.0
 */
public class RecipeSearch extends AppCompatActivity {
    /**
     * The binding object for the layout of the RecipeSearch activity.
     */
    private ActivityRecipeSearchBinding binding;

    /**
     * The list to store the retrieved recipes.
     */
    private ArrayList<Recipe> recipes;

    /**
     * The SharedPreferences for storing search text.
     */
    private SharedPreferences preferences;

    /**
     * Adapter for the RecyclerView used in displaying the list of recipes.
     */
    private RecyclerView.Adapter recipeSearchAdapter;

    /**
     * The request queue for handling network requests.
     */
    private RequestQueue queue;

    /**
     * This method is called when the RecipeSearch activity is created.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize variables and components
        binding = ActivityRecipeSearchBinding.inflate(getLayoutInflater());
        queue = Volley.newRequestQueue(this);
        recipes = new ArrayList<>();

        // Setup the content view
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        setupRecyclerView();

        // Setup the click listener for the search button
        binding.searchButton.setOnClickListener(click -> {
            saveSearchStringToPreferences();
            loadSearchedRecipes();
        });

        // Load the saved search string from SharedPreferences
        String searchString = loadSearchStringFromPreferences();

        // Load previously searched recipes based on SharedPreferences
        if(!searchString.equals("")) {
            loadSearchedRecipes();
        }
    }

    /**
     * Loads the previous search string from SharedPreferences
     * @return The loaded search string.
     */
    private String loadSearchStringFromPreferences() {
        preferences = getSharedPreferences("RecipeSearchData", Context.MODE_PRIVATE);
        binding.searchEditText.setText(preferences.getString("SearchText", ""));

        return binding.searchEditText.getText().toString();
    }

    /**
     * Saves the latest search string to SharedPreferences.
     */
    private void saveSearchStringToPreferences() {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("SearchText", binding.searchEditText.getText().toString());
        editor.apply();
    }

    /**
     * Loads recipes based on the current search string using the Spoonacular API.
     */
    private void loadSearchedRecipes() {
        final String apiKey = "f539c5f0527d4a7b96b085e223d14e0a";
        String url = "";

        try {
            url = "https://api.spoonacular.com/recipes/complexSearch?query=" + URLEncoder.encode(binding.searchEditText.getText().toString(), "UTF-8") + "&apiKey=" + apiKey;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                (response) -> {
                    try {
                        // Clear previous searched recipes
                        recipes.clear();

                        JSONArray results = response.getJSONArray("results");

                        // Extract essential information and then store in the recipes array
                        for (int index = 0; index < results.length(); index++) {
                            JSONObject result = results.getJSONObject(index);
                            int recipeId = result.getInt("id");
                            String recipeTitle = result.getString("title");
                            String recipeImage = result.getString("image");

                            recipes.add(new Recipe(recipeId, recipeTitle, recipeImage));
                        }

                        recipeSearchAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                (error) -> error.printStackTrace());

        queue.add(request);
    }

    /**
     * Sets up the RecyclerView with its adapter.
     */
    private void setupRecyclerView() {
        binding.recipeSearchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recipeSearchRecyclerView.setAdapter(recipeSearchAdapter = new RecyclerView.Adapter<RecipeSearchHolder>() {
            /**
             * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
             * @param parent The ViewGroup into which the new View will be added after it is bound to
             *               an adapter position.
             * @param viewType The view type of the new View.
             *
             * @return A new ViewHolder that holds a View of the given view type.
             */
            @NonNull
            @Override
            public RecipeSearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_preview, parent, false);

                return new RecipeSearchHolder(view);
            }

            /**
             * Called by RecyclerView to display the data at the specified position.
             * @param holder The ViewHolder which should be updated to represent the contents of the
             *        item at the given position in the data set.
             * @param position The position of the item within the adapter's data set.
             */
            @Override
            public void onBindViewHolder(@NonNull RecipeSearchHolder holder, int position) {
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
        });
    }

    /**
     * Initializes the contents of the options menu.
     * @param menu The options menu in which you place your items.
     * @return True for the menu to be displayed.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // Inflate custom menu resource
        getMenuInflater().inflate(R.menu.recipe_search_menu, menu);

        // Hide delete item
        MenuItem deleteItem = menu.findItem(R.id.deleteRecipe);
        deleteItem.setVisible(false);

        // Hide save item
        MenuItem saveItem = menu.findItem(R.id.saveRecipe);
        saveItem.setVisible(false);

        return true;
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
                Intent homeIntent = new Intent(RecipeSearch.this, MainActivity.class);
                startActivity(homeIntent);

                break;
            case R.id.viewRecipes:
                // Open the SavedRecipes activity
                Intent savedRecipesIntent = new Intent(RecipeSearch.this, SavedRecipes.class);
                startActivity(savedRecipesIntent);

                break;
            case R.id.help:
                // Display instructions on how to use the application
                AlertDialog.Builder builder = new AlertDialog.Builder( RecipeSearch.this );
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
     * This class is the ViewHolder class for the RecyclerView.
     * @author Lixdel Louisse Aggabao
     * @version 1.0
     * @see RecipeSearch
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
                args.putString("CallingActivity", "RecipeSearch");
                recipeFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.recipeFragmentLocation, recipeFragment).commit();
            });

            // Initialize TextViews in the item view
            recipeTitle = itemView.findViewById(R.id.previewRecipeTitle);
            recipeImage = itemView.findViewById(R.id.previewRecipeImage);
        }
    }
}