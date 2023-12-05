package algonquin.cst2335.finalproject.RecipeSearch;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Recipe.class}, version=1, exportSchema = false)
public abstract class RecipeDatabase extends RoomDatabase {
    public abstract RecipeDAO recipeDAO();
}
