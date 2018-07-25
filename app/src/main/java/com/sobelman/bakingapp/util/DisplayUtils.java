package com.sobelman.bakingapp.util;

import com.sobelman.bakingapp.model.RecipeIngredient;

import java.util.List;

/**
 * Utilities for displaying text in the UI.
 */
public class DisplayUtils {

    public static String getIngredientsDisplayText(List<RecipeIngredient> ingredients) {
        if (ingredients == null) return null;

        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (RecipeIngredient ingredient : ingredients) {
            if (first) first = false;
            else stringBuilder.append("\n");
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

}
