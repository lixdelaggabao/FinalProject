package algonquin.cst2335.finalproject.RecipeSearch;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

/**
 * This interface is used for interacting with the database for inserting, deleting, and
 * retrieving recipes.
 * @author Lixdel Louisse Aggabao
 * @version 1.0
 * @see Recipe
 * @see RecipeDatabase
 * @see RecipeDetailsFragment
 * @see RecipeSearch
 * @see SavedRecipes
 * @since 20.0
 */
@Dao
public interface RecipeDAO {
    /**
     * Inserts a new recipe into the database.
     * @param recipe The recipe to be inserted.
     */
    @Insert
    void insertRecipe(Recipe recipe);

    /**
     * Deletes a recipe from the database.
     * @param recipe The recipe to be deleted.
     */
    @Delete
    void deleteRecipe(Recipe recipe);

    /**
     * Retrieves a recipe from the database based on its ID.
     * @param recipeId The ID of the recipe to be retrieved.
     * @return The recipe with the specified ID or null if not found.
     */
    @Query("Select * from Recipe where id = :recipeId limit 1")
    Recipe getRecipeById(int recipeId);

    /**
     * Retrieves all recipes from the database.
     * @return A list of all saved recipes in the database.
     */
    @Query("Select * from Recipe")
    List<Recipe> getAllSavedRecipes();
}
