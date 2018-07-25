package com.sobelman.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sobelman.bakingapp.model.Recipe;
import com.sobelman.bakingapp.model.RecipeIngredient;
import com.sobelman.bakingapp.model.RecipeStep;
import com.sobelman.bakingapp.util.DisplayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of RecipeSteps. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeStepDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeActivity extends AppCompatActivity {

    public static final String RECIPE_EXTRA = "com.sobelman.bakingapp.ExtraRecipe";

    // the recipe displayed in this activity
    private Recipe mRecipe;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipestep_list);

        Intent intent = getIntent();
        if (intent != null) {
            mRecipe = intent.getParcelableExtra(RECIPE_EXTRA);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        CharSequence title = getTitle();
        if (mRecipe != null && mRecipe.getName() != null) title = mRecipe.getName();
        setTitle(title);

        if (findViewById(R.id.recipestep_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        populateIngredientsList();

        View recyclerView = findViewById(R.id.recipestep_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void populateIngredientsList() {
        List<RecipeIngredient> ingredients = mRecipe.getIngredients();
        if (ingredients != null) {
            String ingredientsDisplayText = DisplayUtils.getIngredientsDisplayText(ingredients);
            TextView ingredientsListTextView = findViewById(R.id.tv_ingredients_list);
            ingredientsListTextView.setText(ingredientsDisplayText);
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new RecipeStepAdapter(this, mRecipe.getSteps(), mTwoPane));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    // Adapter class for the RecyclerView containing the list of recipe steps.
    public static class RecipeStepAdapter
            extends RecyclerView.Adapter<RecipeStepAdapter.ViewHolder> {

        private final RecipeActivity mParentActivity;
        private final List<RecipeStep> mValues;
        private final boolean mTwoPane;

        private int mSelected = RecyclerView.NO_POSITION;

        RecipeStepAdapter(RecipeActivity parent,
                          List<RecipeStep> items,
                          boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recipestep_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            holder.mContentView.setText(mValues.get(position).getShortDescription());

            // set the item view tag to the position of the step in the steps list
            // this is used by the previous and next buttons in the detail activity and
            // to determine which step to display in the detail fragment
            holder.itemView.setTag(position);

            holder.itemView.setBackgroundColor((mSelected == position) ? Color.GREEN : Color.TRANSPARENT);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mContentView = view.findViewById(R.id.content);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                if (mTwoPane) { // tablet layout
                    // unhighlight the previously selected step and highlight the current selection
                    notifyItemChanged(mSelected);
                    mSelected = getAdapterPosition();
                    notifyItemChanged(mSelected);

                    // change the fragment in the fragment container
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(RecipeStepDetailFragment.ARG_ITEM_ID, mValues.get(position));
                    RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recipestep_detail_container, fragment)
                            .commit();
                } else { // phone layout
                    // launch the detail activity
                    Context context = view.getContext();
                    Intent intent = new Intent(context, RecipeStepDetailActivity.class);
                    intent.putExtra(RecipeStepDetailActivity.EXTRA_RECIPE_NAME,
                            mParentActivity.mRecipe.getName());
                    intent.putExtra(RecipeStepDetailActivity.EXTRA_STEP_INDEX, position);
                    intent.putParcelableArrayListExtra(RecipeStepDetailActivity.EXTRA_STEPS_LIST,
                            (ArrayList<RecipeStep>)mParentActivity.mRecipe.getSteps());

                    context.startActivity(intent);
                }
            }
        }
    }
}
