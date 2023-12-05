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

public class RecipeDetailsFragment extends Fragment {
    private RecipeDetailsLayoutBinding binding;
    private RequestQueue queue;
    private Recipe selectedRecipe;
    private RecipeDatabase recipeDatabase;
    private RecipeDAO recipeDAO;
    private String callingActivity;

    public RecipeDetailsFragment(Recipe recipe) {
        selectedRecipe = recipe;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        callingActivity = getArguments().getString("CallingActivity");
        queue = Volley.newRequestQueue(requireContext());
        binding = RecipeDetailsLayoutBinding.inflate(inflater);

        loadToolbar();
        loadDatabase();
        loadRecipe();

        View toolbarLogo = binding.toolbar.getChildAt(1);
        toolbarLogo.setOnClickListener(click -> {
            Intent recipeSearch = new Intent(getActivity(), RecipeSearch.class);
            startActivity(recipeSearch);
        });

        return binding.getRoot();
    }

    private void loadToolbar() {
        ((AppCompatActivity)getActivity()).setSupportActionBar(binding.toolbar);
        setHasOptionsMenu(true);
    }

    private void loadDatabase() {
        recipeDatabase = Room.databaseBuilder(requireContext().getApplicationContext(), RecipeDatabase.class, "recipe-database").build();
        recipeDAO = recipeDatabase.recipeDAO();
    }

    private void loadRecipe() {
        Executor thread = Executors.newSingleThreadExecutor();
        thread.execute(() -> {
            Recipe recipe = recipeDAO.getRecipeById(selectedRecipe.getId());

            if (recipe != null) {
                loadFromDatabase(recipe);
            } else {
                loadFromUrl();
            }
        });
    }

    private void loadFromDatabase(Recipe recipe) {
        requireActivity().runOnUiThread(() -> {
            binding.recipeDetailsTitleTextView.setText(recipe.getTitle());
            binding.recipeDetailsTitleTextView.setVisibility(View.VISIBLE);

            Picasso.get().load(recipe.getImageUrl()).into(binding.recipeDetailsImageView);
            binding.recipeDetailsImageView.setContentDescription(getString(R.string.recipe_search_image_description) + " " + recipe.getTitle());
            binding.recipeDetailsImageView.setVisibility(View.VISIBLE);

            binding.recipeDetailsSummaryTextView.setText(HtmlCompat.fromHtml(recipe.getSummary(), HtmlCompat.FROM_HTML_MODE_LEGACY));
            selectedRecipe.setSummary(recipe.getSummary());
            binding.recipeDetailsSummaryTextView.setVisibility(View.VISIBLE);

            binding.recipeDetailsSourceUrlTextView.setText(recipe.getSourceUrl());
            selectedRecipe.setSourceUrl(recipe.getSourceUrl());
            binding.recipeDetailsSourceUrlTextView.setVisibility(View.VISIBLE);
        });
    }

    private void loadFromUrl() {
        final String apiKey = "f539c5f0527d4a7b96b085e223d14e0a";
        String url = "https://api.spoonacular.com/recipes/" + selectedRecipe.getId() + "/information?apiKey=" + apiKey;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                (response) -> {
                    try {
                        String recipeTitle = response.getString("title");
                        String recipeImage = response.getString("image");
                        String recipeSummary = response.getString("summary");
                        String recipeSourceUrl = response.getString("sourceUrl");

                        requireActivity().runOnUiThread(() -> {
                            binding.recipeDetailsTitleTextView.setText(recipeTitle);
                            binding.recipeDetailsTitleTextView.setVisibility(View.VISIBLE);

                            Picasso.get().load(recipeImage).into(binding.recipeDetailsImageView);
                            binding.recipeDetailsImageView.setContentDescription("Image of " + recipeTitle);
                            binding.recipeDetailsImageView.setVisibility(View.VISIBLE);

                            binding.recipeDetailsSummaryTextView.setText(HtmlCompat.fromHtml(recipeSummary, HtmlCompat.FROM_HTML_MODE_LEGACY));
                            selectedRecipe.setSummary(recipeSummary);
                            binding.recipeDetailsSummaryTextView.setVisibility(View.VISIBLE);

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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();
        inflater.inflate(R.menu.recipe_search_menu, menu);

        MenuItem saveItem = menu.findItem(R.id.saveRecipe);
        MenuItem deleteItem = menu.findItem(R.id.deleteRecipe);

        if (callingActivity.equals("SavedRecipes")) {
            deleteItem.setVisible(true);
            saveItem.setVisible(false);
        } else {
            deleteItem.setVisible(false);
            saveItem.setVisible(true);
        }

        MenuItem homeItem = menu.findItem(R.id.home);
        homeItem.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.saveRecipe:
                Executor saveThread = Executors.newSingleThreadExecutor();

                saveThread.execute(() -> {
                    if (recipeDAO.getRecipeById(selectedRecipe.getId()) == null) {
                        recipeDAO.insertRecipe(selectedRecipe);

                        requireActivity().runOnUiThread(() -> Toast.makeText(getActivity(), getString(R.string.recipe_search_successful_save_message), Toast.LENGTH_SHORT).show());
                    } else {
                        requireActivity().runOnUiThread(() -> Toast.makeText(getActivity(), getString(R.string.recipe_search_failed_save_message), Toast.LENGTH_SHORT).show());
                    }
                });

                break;
            case R.id.deleteRecipe:
                AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
                builder.setTitle(getString(R.string.recipe_search_delete_recipe_title))
                       .setMessage(getString(R.string.recipe_search_delete_recipe_message) + " " + selectedRecipe.getTitle())
                       .setPositiveButton(getString(R.string.recipe_search_delete_recipe_positive), (dialog, cl) -> {
                           Executor deleteThread = Executors.newSingleThreadExecutor();
                           deleteThread.execute(() -> {
                               recipeDAO.deleteRecipe(selectedRecipe);

                               requireActivity().runOnUiThread(() -> {
                                   Intent resultIntent = new Intent(getActivity(), SavedRecipes.class);

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