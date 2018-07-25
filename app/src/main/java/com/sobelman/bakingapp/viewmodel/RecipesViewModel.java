package com.sobelman.bakingapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

/**
 * ViewModel for the recipes list in MainActivity.
 */
public class RecipesViewModel extends AndroidViewModel {
    private RecipesLiveData mRecipesData;

    public RecipesViewModel(@NonNull Application application) {
        super(application);
        mRecipesData = new RecipesLiveData(application);
    }

    public RecipesLiveData getRecipesData() {
        return mRecipesData;
    }
}
