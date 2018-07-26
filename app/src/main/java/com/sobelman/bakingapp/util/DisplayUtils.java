package com.sobelman.bakingapp.util;

import android.content.Context;

import com.sobelman.bakingapp.R;
import com.sobelman.bakingapp.model.RecipeIngredient;

import java.util.List;

/**
 * Utilities for displaying text in the UI.
 */
public class DisplayUtils {

    public static String getIngredientsDisplayText(List<RecipeIngredient> ingredients,
                                                   Context context) {
        if (ingredients == null) return null;

        StringBuilder stringBuilder = new StringBuilder();
        if (context != null) { // only display ingredients label if not in the widget
            stringBuilder.append(context.getString(R.string.label_ingredients_list)).append("\n");
        }
        boolean first = true;
        for (RecipeIngredient ingredient : ingredients) {
            if (first) first = false;
            else stringBuilder.append("\n");
            if (context != null) { // only show bullet points if not in widget
                stringBuilder.append("\u2022 "); // bullet point
            }
            stringBuilder.append(ingredient.getQuantity());
            stringBuilder.append(" ");
            String measure = ingredient.getMeasure();
            if (measure != null && !measure.equals(RecipeParsingUtils.NO_MEASURE)) {
                stringBuilder.append(ingredient.getMeasure());
                stringBuilder.append(" ");
            }
            stringBuilder.append(ingredient.getIngredient());
        }
        return stringBuilder.toString();
    }

    public static String getIngredientsDisplayText(List<RecipeIngredient> ingredients) {
        return getIngredientsDisplayText(ingredients, null);
    }

}
