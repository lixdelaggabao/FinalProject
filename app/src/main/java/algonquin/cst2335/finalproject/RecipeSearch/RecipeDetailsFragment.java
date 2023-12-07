package algonquin.cst2335.finalproject.RecipeSearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import androidx.appcompat.app.AlertDialog;
import algonquin.cst2335.finalproject.R;
import algonquin.cst2335.finalproject.databinding.RecipeDetailsLayoutBinding;

/**
 * This class represents the fragment used to display the details of a recipe.
 * @author Lixdel Louisse Aggabao
 * @version 1.0
 * @see Recipe
 * @see RecipeDAO
 * @see RecipeDatabase
 * @see RecipeSearch
 * @see SavedRecipes
 * @since 20.0
 */
public class RecipeDetailsFragment extends Fragment {
    /**
     * The binding object for the layout of the RecipeDetailsFragment.
     */
    private RecipeDetailsLayoutBinding binding;

    /**
     * The identifier for the activity that called this fragment.
     */
    private String callingActivity;

    /**
     * The currently selected recipe for which details are being displayed.
     */
    private Recipe selectedRecipe;

    /**
     * The database used to store and retrieve recipes.
     */
    private RecipeDatabase recipeDatabase;

    /**
     * The Data Access Object for interacting with the database.
     */
    private RecipeDAO recipeDAO;

    /**
     * The request queue for handling network requests.
     */
    private RequestQueue queue;

    /**
     * Initializes the fragment with the specified recipe.
     * @param recipe The recipe for which the details are to be displayed.
     */
    public RecipeDetailsFragment(Recipe recipe) {
        selectedRecipe = recipe;
    }

    /**
     * Creates the view hierarchy associated with the fragment.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return The root view of the fragment's layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Initialize variables and components
        callingActivity = getArguments().getString("CallingActivity");
        queue = Volley.newRequestQueue(requireContext());
        binding = RecipeDetailsLayoutBinding.inflate(inflater);

        // Load resources to be used in the fragment
        loadToolbar();
        loadDatabase();
        loadRecipe();

        // Setup the click listener for the toolbar logo
        View toolbarLogo = binding.toolbar.getChildAt(1);
        toolbarLogo.setOnClickListener(click -> {
            Intent recipeSearch = new Intent(getActivity(), RecipeSearch.class);
            startActivity(recipeSearch);
        });

        return binding.getRoot();
    }

    /**
     * Sets up the toolbar for the fragment.
     */
    private void loadToolbar() {
        ((AppCompatActivity)getActivity()).setSupportActionBar(binding.toolbar);
        setHasOptionsMenu(true);
    }

    /**
     * Sets up the database for different operations.
     */
    private void loadDatabase() {
        recipeDatabase = Room.databaseBuilder(requireContext().getApplicationContext(), RecipeDatabase.class, "recipe-database").build();
        recipeDAO = recipeDatabase.recipeDAO();
    }

    /**
     * Retrieves recipe details either from the database or from the Spoonacular API.
     */
    private void loadRecipe() {
        Executor thread = Executors.newSingleThreadExecutor();
        thread.execute(() -> {
            Recipe recipe = recipeDAO.getRecipeById(selectedRecipe.getId());

            // Check if recipe is saved in the database
            if (recipe != null) {
                loadFromDatabase(recipe);
            } else {
                loadFromUrl();
            }
        });
    }

    /**
     * Updates the UI with the recipe details retrieved from the database.
     * @param recipe The recipe for which details are to be displayed.
     */
    private void loadFromDatabase(Recipe recipe) {
        requireActivity().runOnUiThread(() -> {
            // Update UI for the title
            binding.recipeDetailsTitleTextView.setText(recipe.getTitle());
            binding.recipeDetailsTitleTextView.setVisibility(View.VISIBLE);

            // Update UI for the image
            Picasso.get().load(recipe.getImageUrl()).into(binding.recipeDetailsImageView);
            binding.recipeDetailsImageView.setContentDescription(getString(R.string.recipe_search_image_description) + " " + recipe.getTitle());
            binding.recipeDetailsImageView.setVisibility(View.VISIBLE);

            // Update UI for the summary
            binding.recipeDetailsSummaryTextView.setText(HtmlCompat.fromHtml(recipe.getSummary(), HtmlCompat.FROM_HTML_MODE_LEGACY));
            selectedRecipe.setSummary(recipe.getSummary());
            binding.recipeDetailsSummaryTextView.setVisibility(View.VISIBLE);

            // Update UI for the source URL
            binding.recipeDetailsSourceUrlTextView.setText(recipe.getSourceUrl());
            selectedRecipe.setSourceUrl(recipe.getSourceUrl());
            binding.recipeDetailsSourceUrlTextView.setVisibility(View.VISIBLE);
        });
    }

    /**
     * Updates the UI with the recipe details retrieved from the Spoonacular API.
     */
    private void loadFromUrl() {
        final String apiKey = "f539c5f0527d4a7b96b085e223d14e0a";
        String url = "https://api.spoonacular.com/recipes/" + selectedRecipe.getId() + "/information?apiKey=" + apiKey;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                (response) -> {
                    try {
                        // Retrieve details from the API
                        String recipeTitle = response.getString("title");
                        String recipeImage = response.getString("image");
                        String recipeSummary = response.getString("summary");
                        String recipeSourceUrl = response.getString("sourceUrl");

                        requireActivity().runOnUiThread(() -> {
                            // Update UI for the title
                            binding.recipeDetailsTitleTextView.setText(recipeTitle);
                            binding.recipeDetailsTitleTextView.setVisibility(View.VISIBLE);

                            // Update UI for the image
                            Picasso.get().load(recipeImage).into(binding.recipeDetailsImageView);
                            binding.recipeDetailsImageView.setContentDescription("Image of " + recipeTitle);
                            binding.recipeDetailsImageView.setVisibility(View.VISIBLE);

                            // Update UI for the summary
                            binding.recipeDetailsSummaryTextView.setText(HtmlCompat.fromHtml(recipeSummary, HtmlCompat.FROM_HTML_MODE_LEGACY));
                            selectedRecipe.setSummary(recipeSummary);
                            binding.recipeDetailsSummaryTextView.setVisibility(View.VISIBLE);

                            // Update UI for the source URL
                            binding.recipeDetailsSourceUrlTextView.setText(recipeSourceUrl);
                            selectedRecipe.setSourceUrl(recipeSourceUrl);
                            binding.recipeDetailsSourceUrlTextView.setVisibility(View.VISIBLE);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                (error) -> error.printStackTrace());

        queue.add(request);
    }

    /**
     * Initializes the contents of the options menu.
     * @param menu The options menu in which you place your items.
     * @param inflater The MenuInflater to inflate the menu.
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Clear existing menu items and inflate custom menu resource
        menu.clear();
        inflater.inflate(R.menu.recipe_search_menu, menu);

        // Get menu items
        MenuItem saveItem = menu.findItem(R.id.saveRecipe);
        MenuItem deleteItem = menu.findItem(R.id.deleteRecipe);
        MenuItem homeItem = menu.findItem(R.id.home);

        // Adjust visibility of menu items based on the calling activity
        if (callingActivity.equals("SavedRecipes")) {
            deleteItem.setVisible(true);
            saveItem.setVisible(false);
        } else {
            deleteItem.setVisible(false);
            saveItem.setVisible(true);
        }

        homeItem.setVisible(false);
    }

    /**
     * Handles item selection in the options menu.
     * @param item The menu item that was selected.
     * @return True if the menu item selection is handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.saveRecipe:
                Executor saveThread = Executors.newSingleThreadExecutor();

                saveThread.execute(() -> {
                    // Check if recipe is already saved in the database
                    if (recipeDAO.getRecipeById(selectedRecipe.getId()) == null) {
                        // Save recipe in the database
                        recipeDAO.insertRecipe(selectedRecipe);

                        requireActivity().runOnUiThread(() -> Toast.makeText(getActivity(), getString(R.string.recipe_search_successful_save_message), Toast.LENGTH_SHORT).show());
                    } else {
                        // Show error message
                        requireActivity().runOnUiThread(() -> Toast.makeText(getActivity(), getString(R.string.recipe_search_failed_save_message), Toast.LENGTH_SHORT).show());
                    }
                });

                break;
            case R.id.deleteRecipe:
                // Display confirmation dialog for recipe deletion
                AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
                builder.setTitle(getString(R.string.recipe_search_delete_recipe_title))
                       .setMessage(getString(R.string.recipe_search_delete_recipe_message) + " " + selectedRecipe.getTitle())
                       .setPositiveButton(getString(R.string.recipe_search_delete_recipe_positive), (dialog, cl) -> {
                           Executor deleteThread = Executors.newSingleThreadExecutor();
                           deleteThread.execute(() -> {
                               // Delete the recipe
                               recipeDAO.deleteRecipe(selectedRecipe);

                               requireActivity().runOnUiThread(() -> {
                                   Intent resultIntent = new Intent(getActivity(), SavedRecipes.class);

                                   // Open the SavedRecipes activity
                                   resultIntent.putExtra("deletedRecipe", selectedRecipe.getTitle());
                                   startActivity(resultIntent);
                               });
                           });
                       })
                       .setNegativeButton(getString(R.string.recipe_search_delete_recipe_negative), (dialog, cl) -> {})
                       .create().show();

                break;
        }

        return true;
    }
}