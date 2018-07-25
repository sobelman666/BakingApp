package com.sobelman.bakingapp.viewmodel;

import android.content.Context;
import android.os.AsyncTask;

import com.sobelman.bakingapp.model.Recipe;
import com.sobelman.bakingapp.util.DataCache;
import com.sobelman.bakingapp.util.NetworkUtils;
import com.sobelman.bakingapp.util.RecipeParsingUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * AsyncTask for retrieving recipe data from a network location. If there is a problem with the
 * network, cached data is returned if available.
 */
public class FetchRecipesTask extends AsyncTask<Void, Void, List<Recipe>> {
    private Callback mCallback;

    FetchRecipesTask(Callback callback) {
        mCallback = callback;
    }

    @Override
    protected List<Recipe> doInBackground(Void... voids) {
        URL recipesUrl = NetworkUtils.getRecipesUrl();
        String response = null;
        try {
            response = NetworkUtils.getResponseFromHttpUrl(recipesUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response == null) {
            // try loading from cache
            response = DataCache.readRecipeJson(mCallback.getContext());
        } else {
            // cache the response
            DataCache.cacheRecipeJson(response, mCallback.getContext());
        }
        return RecipeParsingUtils.parseRecipes(response);
    }

    @Override
    protected void onPostExecute(List<Recipe> recipes) {
        mCallback.onFetchCompleted(recipes);
    }

    // Callback interface for returning data upon completion of background task. Also provides
    // access to a Context for use by the DataCache.
    public interface Callback {
        void onFetchCompleted(List<Recipe> recipes);
        Context getContext();
    }
}
