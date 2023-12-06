package algonquin.cst2335.finalproject.RecipeSearch;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * This class represents the database for Recipe entities.
 * @author Lixdel Louisse Aggabao
 * @version 1.0
 * @see Recipe
 * @see RecipeDAO
 * @see RecipeDetailsFragment
 * @see RecipeSearch
 * @see SavedRecipes
 * @since 20.0
 */
@Database(entities = {Recipe.class}, version=1, exportSchema = false)
public abstract class RecipeDatabase extends RoomDatabase {
    /**
     * Abstract method to retrieve the RecipeDAO interface for database operations.
     * @return RecipeDAO object for interacting with the database.
     */
    public abstract RecipeDAO recipeDAO();
}
