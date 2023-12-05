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

public class RecipeSearch extends AppCompatActivity {
    private ActivityRecipeSearchBinding binding;
    private RecyclerView.Adapter recipeSearchAdapter;
    private ArrayList<Recipe> recipes;
    private SharedPreferences preferences;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRecipeSearchBinding.inflate(getLayoutInflater());
        queue = Volley.newRequestQueue(this);
        recipes = new ArrayList<>();

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        setupRecyclerView();

        binding.searchButton.setOnClickListener(click -> {
            saveSearchStringToPreferences();
            loadSearchedRecipes();
        });

        String searchString = loadSearchStringFromPreferences();

        if(!searchString.equals("")) {
            loadSearchedRecipes();
        }
    }

    private String loadSearchStringFromPreferences() {
        preferences = getSharedPreferences("RecipeSearchData", Context.MODE_PRIVATE);
        binding.searchEditText.setText(preferences.getString("SearchText", ""));

        return binding.searchEditText.getText().toString();
    }

    private void saveSearchStringToPreferences() {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("SearchText", binding.searchEditText.getText().toString());
        editor.apply();
    }

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
                        recipes.clear();

                        JSONArray results = response.getJSONArray("results");

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

    private void setupRecyclerView() {
        binding.recipeSearchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recipeSearchRecyclerView.setAdapter(recipeSearchAdapter = new RecyclerView.Adapter<RecipeSearchHolder>() {
            @NonNull
            @Override
            public RecipeSearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_preview, parent, false);

                return new RecipeSearchHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull RecipeSearchHolder holder, int position) {
                Recipe recipe = recipes.get(position);

                holder.recipeTitle.setText(recipe.getTitle());
                Picasso.get().load(recipe.getImageUrl()).into(holder.recipeImage);
            }

            @Override
            public int getItemCount() {
                return recipes.size();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.recipe_search_menu, menu);

        MenuItem deleteItem = menu.findItem(R.id.deleteRecipe);
        deleteItem.setVisible(false);

        MenuItem saveItem = menu.findItem(R.id.saveRecipe);
        saveItem.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.home:
                Intent homeIntent = new Intent(RecipeSearch.this, MainActivity.class);
                startActivity(homeIntent);

                break;
            case R.id.viewRecipes:
                Intent savedRecipesIntent = new Intent(RecipeSearch.this, SavedRecipes.class);
                startActivity(savedRecipesIntent);

                break;
            case R.id.help:
                AlertDialog.Builder builder = new AlertDialog.Builder( RecipeSearch.this );
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

                args.putString("CallingActivity", "RecipeSearch");
                recipeFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.recipeFragmentLocation, recipeFragment).commit();
            });

            recipeTitle = itemView.findViewById(R.id.previewRecipeTitle);
            recipeImage = itemView.findViewById(R.id.previewRecipeImage);
        }
    }
}