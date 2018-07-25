package com.sobelman.bakingapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sobelman.bakingapp.model.Recipe;
import com.sobelman.bakingapp.viewmodel.RecipesViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.OnClickHandler {

    private RecyclerView mRecipeRecyclerView;
    private RecipeAdapter mRecipeAdapter;

    @BindView(R.id.progressBar) ProgressBar mProgressBar;
    @BindView(R.id.error_display) TextView mErrorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recipeRecyclerView = findViewById(R.id.rv_recipe_grid);
        RecyclerView.LayoutManager layoutManager;
        if (recipeRecyclerView != null) {
            // tablet layout
            mRecipeRecyclerView = recipeRecyclerView;

            // This bit is from Stack Overflow: https://stackoverflow.com/a/38472370
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
            int noOfColumns = (int) (dpWidth / 200);

            layoutManager = new GridLayoutManager(this, noOfColumns);
        } else {
            // phone layout
            mRecipeRecyclerView = findViewById(R.id.rv_recipe_list);
            layoutManager = new LinearLayoutManager(this);
        }
        mRecipeRecyclerView.setLayoutManager(layoutManager);

        mRecipeAdapter = new RecipeAdapter(this, this);
        mRecipeRecyclerView.setAdapter(mRecipeAdapter);

        ButterKnife.bind(this);

        setUpViewModel();
    }

    private void setUpViewModel() {
        mProgressBar.setVisibility(View.VISIBLE);
        RecipesViewModel viewModel = ViewModelProviders.of(this).get(RecipesViewModel.class);
        viewModel.getRecipesData().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                mProgressBar.setVisibility(View.INVISIBLE);

                if (recipes == null) {
                    mErrorTextView.setText(R.string.error_no_recipes);
                    showError();
                } else {
                    mRecipeAdapter.setRecipes(recipes);
                    showRecipes();
                }
            }
        });
    }

    private void showRecipes() {
        mErrorTextView.setVisibility(View.INVISIBLE);
        mRecipeRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showError() {
        mRecipeRecyclerView.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra(RecipeActivity.RECIPE_EXTRA, recipe);
        startActivity(intent);
    }
}
