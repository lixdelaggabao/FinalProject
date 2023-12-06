package algonquin.cst2335.finalproject.RecipeSearch;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * This class represents a single recipe entity with an ID,
 * title, image URL, summary, and source URL.
 * @author Lixdel Louisse Aggabao
 * @version 1.0
 * @see RecipeDAO
 * @see RecipeDatabase
 * @see RecipeDetailsFragment
 * @see RecipeSearch
 * @see SavedRecipes
 * @since 20.0
 */
@Entity
public class Recipe {
    /**
     * The unique identifier for the recipe.
     */
    @PrimaryKey
    @ColumnInfo(name="id")
    private int id;

    /**
     * The name of the recipe.
     */
    @ColumnInfo(name="title")
    private String title;

    /**
     * The URL of the image of the recipe.
     */
    @ColumnInfo(name="imageUrl")
    private String imageUrl;

    /**
     * A brief description of the recipe.
     */
    @ColumnInfo(name="summary")
    private String summary;

    /**
     * The URL from which the recipe is from.
     */
    @ColumnInfo(name="sourceUrl")
    private String sourceUrl;

    /**
     * Initializes the recipe with default values.
     */
    public Recipe() {
        this(-1, "", "", "", "");
    }

    /**
     * Initializes the recipe with the specified ID, title, and image URL.
     * @param id The ID of the recipe.
     * @param title The title of the recipe.
     * @param imageUrl The URL of the image of the recipe.
     */
    public Recipe(int id, String title, String imageUrl) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
    }

    /**
     * Initializes the recipe with the specified ID, title, image URL, summary, and source URL.
     * @param id The ID of the recipe.
     * @param title The title of the recipe.
     * @param imageUrl The URL of the image of the recipe.
     * @param summary The summary of the recipe.
     * @param sourceUrl The source URL of the recipe.
     */
    public Recipe(int id, String title, String imageUrl, String summary, String sourceUrl) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.summary = summary;
        this.sourceUrl = sourceUrl;
    }

    /**
     * Gets the ID of the recipe.
     * @return The ID of the recipe.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the recipe.
     * @param id The ID to set for the recipe.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the title of the recipe.
     * @return The title of the recipe.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the recipe.
     * @param title The title to set for the recipe.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the image URL of the recipe.
     * @return The image URL of the recipe.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the image URL of the recipe.
     * @param imageUrl The image URL to set for the recipe.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the summary of the recipe.
     * @return The summary of the recipe.
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Sets the summary of the recipe.
     * @param summary The summary to set for the recipe.
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * Gets the source URL of the recipe.
     * @return The source URL of the recipe.
     */
    public String getSourceUrl() {
        return sourceUrl;
    }

    /**
     * Sets the source URL of the recipe.
     * @param sourceUrl The source URL to set for the recipe.
     */
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}
