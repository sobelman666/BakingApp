package com.sobelman.bakingapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sobelman.bakingapp.model.Recipe;

import java.util.List;

/**
 * Simple cache for recipe data retrieved from a network location. Stores data in
 * shared preferences.
 */
public class DataCache {
    private static final String RECIPE_JSON_KEY = "recipes";

    public static void cacheRecipeJson(String json, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(RECIPE_JSON_KEY, json);
        editor.apply();
    }

    public static String readRecipeJson(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(RECIPE_JSON_KEY, null);
    }

    public static List<Recipe> readRecipes(Context context) {
        String recipesJson = readRecipeJson(context);
        return RecipeParsingUtils.parseRecipes(recipesJson);
    }
}
