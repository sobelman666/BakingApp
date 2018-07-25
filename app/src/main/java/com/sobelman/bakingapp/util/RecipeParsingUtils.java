package com.sobelman.bakingapp.util;

import com.sobelman.bakingapp.model.Recipe;
import com.sobelman.bakingapp.model.RecipeIngredient;
import com.sobelman.bakingapp.model.RecipeStep;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for parsing JSON data into model objects.
 */
public class RecipeParsingUtils {
    // JSON keys
    private static final String ID_KEY = "id";
    private static final String NAME_KEY = "name";
    private static final String SERVINGS_KEY = "servings";
    private static final String IMAGE_KEY = "image";
    private static final String INGREDIENTS_KEY = "ingredients";
    private static final String STEPS_KEY = "steps";
    private static final String QUANTITY_KEY = "quantity";
    private static final String MEASURE_KEY = "measure";
    private static final String RECIPE_INGREDIENT_KEY = "ingredient";
    private static final String SHORT_DESCRIPTION_KEY = "shortDescription";
    private static final String DESCRIPTION_KEY = "description";
    private static final String VIDEO_URL_KEY = "videoURL";
    private static final String THUMBNAIL_URL_KEY = "thumbnailURL";

    // measurement unit conversions from JSON values to standard abbreviations
    private static final String JSON_CUP = "CUP";
    private static final String JSON_TABLESPOON = "TBLSP";
    private static final String JSON_TEASPOON = "TSP";
    private static final String JSON_GRAM = "G";
    private static final String JSON_KILOGRAM = "K";
    private static final String JSON_OUNCE = "OZ";
    private static final String ABBR_CUP = "cup";
    private static final String ABBR_TABLESPOON = "tbsp";
    private static final String ABBR_TEASPOON = "tsp";
    private static final String ABBR_GRAM = "g";
    private static final String ABBR_KILOGRAM = "kg";
    private static final String ABBR_OUNCE = "oz";

    public static final String NO_MEASURE = "UNIT";

    /**
     * Parses a list of recipes in JSON format into a List of Recipe objects.
     *
     * @param jsonString The raw JSON data.
     * @return a List of Recipe objects parsed from the JSON data.
     */
    public static List<Recipe> parseRecipes(String jsonString) {
        try {
            JSONArray recipesArray = new JSONArray(jsonString);
            ArrayList<Recipe> recipes = new ArrayList<>();
            for (int i = 0; i < recipesArray.length(); i++) {
                Recipe recipe = parseRecipe(recipesArray.optJSONObject(i));
                if (recipe != null) recipes.add(recipe);
            }
            return recipes;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts a JSONObject containing recipe data into a Recipe object.
     *
     * @param recipeJson the JSONObject to be converted.
     * @return a Recipe object.
     */
    public static Recipe parseRecipe(JSONObject recipeJson) {
        if (recipeJson == null) return null;

        int id = recipeJson.optInt(ID_KEY);
        String name = recipeJson.optString(NAME_KEY);
        int servings = recipeJson.optInt(SERVINGS_KEY);
        String image = recipeJson.optString(IMAGE_KEY);
        JSONArray ingredientsJson = recipeJson.optJSONArray(INGREDIENTS_KEY);
        ArrayList<RecipeIngredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientsJson.length(); i++) {
            JSONObject ingredientJson = ingredientsJson.optJSONObject(i);
            RecipeIngredient recipeIngredient = parseRecipeIngredient(ingredientJson);
            if (recipeIngredient != null) {
                ingredients.add(recipeIngredient);
            }
        }
        JSONArray stepsJson = recipeJson.optJSONArray(STEPS_KEY);
        ArrayList<RecipeStep> steps = new ArrayList<>();
        for (int i = 0; i < stepsJson.length(); i++) {
            JSONObject stepJson = stepsJson.optJSONObject(i);
            RecipeStep recipeStep = parseRecipeStep(stepJson);
            if (recipeStep != null) {
                steps.add(recipeStep);
            }
        }
        return new Recipe(id, name, servings, image, ingredients, steps);
    }

    /**
     * Converts a JSONObject containing recipe ingredient data into a RecipeIngredient object.
     *
     * @param ingredientJson the JSONObject to be converted.
     * @return a RecipeIngredient object.
     */
    public static RecipeIngredient parseRecipeIngredient(JSONObject ingredientJson) {
        if (ingredientJson == null) return null;

        int quantity = ingredientJson.optInt(QUANTITY_KEY);
        String measure = parseMeasure(ingredientJson.optString(MEASURE_KEY));
        String ingredient = ingredientJson.optString(RECIPE_INGREDIENT_KEY);
        return new RecipeIngredient(quantity, measure, ingredient);
    }

    // helper method to convert measurement units to standard abbreviations
    private static String parseMeasure(String measure) {
        if (measure == null) return null;

        switch (measure) {
            case JSON_CUP:
                return ABBR_CUP;
            case JSON_TABLESPOON:
                return ABBR_TABLESPOON;
            case JSON_TEASPOON:
                return ABBR_TEASPOON;
            case JSON_GRAM:
                return ABBR_GRAM;
            case JSON_KILOGRAM:
                return ABBR_KILOGRAM;
            case JSON_OUNCE:
                return ABBR_OUNCE;
            default:
                return measure;
        }
    }

    /**
     * Converts a JSONObject containing recipe step data into a RecipeStep object.
     *
     * @param stepJson the JSON object to be converted.
     * @return a RecipeStep object.
     */
    public static RecipeStep parseRecipeStep(JSONObject stepJson) {
        if (stepJson == null) return null;

        int id = stepJson.optInt(ID_KEY);
        String shortDescription = stepJson.optString(SHORT_DESCRIPTION_KEY);
        String description = stepJson.optString(DESCRIPTION_KEY);
        String videoUrl = stepJson.optString(VIDEO_URL_KEY);
        String thumbnailUrl = stepJson.optString(THUMBNAIL_URL_KEY);
        return new RecipeStep(id, shortDescription, description, videoUrl, thumbnailUrl);
    }
}
