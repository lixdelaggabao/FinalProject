package algonquin.cst2335.finalproject.RecipeSearch;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface RecipeDAO {
    @Insert
    void insertRecipe(Recipe recipe);

    @Delete
    void deleteRecipe(Recipe recipe);

    @Query("Select * from Recipe where id = :recipeId limit 1")
    Recipe getRecipeById(int recipeId);

    @Query("Select * from Recipe")
    List<Recipe> getAllSavedRecipes();
}
