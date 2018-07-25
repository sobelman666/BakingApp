package com.sobelman.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.sobelman.bakingapp.model.RecipeStep;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a single RecipeStep detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeActivity}.
 */
public class RecipeStepDetailActivity extends AppCompatActivity {

    public static final String EXTRA_STEP_INDEX = "step_index";
    public static final String EXTRA_STEPS_LIST = "steps_list";
    public static final String EXTRA_RECIPE_NAME = "recipe_name";
    private static final String CURRENT_INDEX = "current_index";

    private ArrayList<RecipeStep> mSteps;
    private int mCurrentIndex = -1;

    @BindView(R.id.btn_previous_step) Button mPreviousButton;
    @BindView(R.id.btn_next_step) Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipestep_detail);
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String recipeName = getIntent().getStringExtra(EXTRA_RECIPE_NAME);
        if (recipeName != null) {
            setTitle(recipeName);
        }

        ButterKnife.bind(this);

        // save the index of the selected step in the list of steps and set the state of
        // the previous and next buttons as appropriate
        mSteps = getIntent().getParcelableArrayListExtra(EXTRA_STEPS_LIST);
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(CURRENT_INDEX, -1);
        }
        if (mCurrentIndex == -1) {
            mCurrentIndex = getIntent().getIntExtra(EXTRA_STEP_INDEX, -1);
        }
        if (mCurrentIndex == 0) mPreviousButton.setEnabled(false);
        if (mCurrentIndex == mSteps.size() - 1) mNextButton.setEnabled(false);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            if (mCurrentIndex != -1) {
                arguments.putParcelable(RecipeStepDetailFragment.ARG_ITEM_ID, mSteps.get(mCurrentIndex));
            }
            RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipestep_detail_container, fragment)
                    .commit();
        } else {
            updateLayout();
        }
    }

    private void updateLayout() {
        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        LinearLayout buttonBar = findViewById(R.id.button_bar);
        if (buttonBar != null) { // we're in landscape orientation
            if (TextUtils.isEmpty(mSteps.get(mCurrentIndex).getVideoUrl())
                    && TextUtils.isEmpty(mSteps.get(mCurrentIndex).getThumbnailUrl())) {
                // no video, show app bar, description and buttons
                appBarLayout.setVisibility(View.VISIBLE);
                buttonBar.setVisibility(View.VISIBLE);
                ConstraintLayout layout = findViewById(R.id.layout_land_constraint);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(layout);
                constraintSet.connect(R.id.recipestep_detail_container, ConstraintSet.TOP,
                        R.id.app_bar, ConstraintSet.BOTTOM);
                constraintSet.connect(R.id.recipestep_detail_container, ConstraintSet.BOTTOM,
                        R.id.button_bar, ConstraintSet.TOP);
                constraintSet.connect(R.id.recipestep_detail_container, ConstraintSet.START,
                        ConstraintSet.PARENT_ID, ConstraintSet.START);
                constraintSet.applyTo(layout);
            } else {
                appBarLayout.setVisibility(View.GONE);
                buttonBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_INDEX, mCurrentIndex);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, RecipeActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void doPrevious(View view) {
        mCurrentIndex--;
        if (mCurrentIndex == 0) mPreviousButton.setEnabled(false);
        if (mCurrentIndex < mSteps.size() - 1) mNextButton.setEnabled(true);
        updateLayout();
        updateRecipeStepFragment();
    }

    public void doNext(View view) {
        mCurrentIndex++;
        if (mCurrentIndex == mSteps.size() - 1) mNextButton.setEnabled(false);
        if (mCurrentIndex > 0) mPreviousButton.setEnabled(true);
        updateLayout();
        updateRecipeStepFragment();
    }

    private void updateRecipeStepFragment() {
        RecipeStep step = mSteps.get(mCurrentIndex);
        Bundle arguments = new Bundle();
        arguments.putParcelable(RecipeStepDetailFragment.ARG_ITEM_ID, step);
        RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.recipestep_detail_container, fragment)
                .commit();
    }
}
