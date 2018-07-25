package com.sobelman.bakingapp.viewmodel;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.sobelman.bakingapp.model.Recipe;

import java.util.List;

/**
 * LiveData to manage a List of Recipes fetched using a FetchRecipesTask. Implements
 * FetchRecipesTask.Callback in order to update the LiveData value upon completion of the
 * background task.
 */
public class RecipesLiveData extends LiveData<List<Recipe>> implements FetchRecipesTask.Callback {
    // a Context for use by the FetchRecipesTask
    private Context mContext;

    RecipesLiveData(Context context) {
        mContext = context;
        new FetchRecipesTask(this).execute(null, null);
    }

    @Override
    public void onFetchCompleted(List<Recipe> recipes) {
        setValue(recipes);
    }

    @Override
    public Context getContext() {
        return mContext;
    }
}
