package com.sobelman.bakingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sobelman.bakingapp.model.Recipe;

import java.util.List;

/**
 * RecyclerView adapter class for the list of recipes in MainActivity.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private List<Recipe> mRecipes;
    private OnClickHandler mClickHandler;
    private Context mContext;

    public RecipeAdapter(OnClickHandler clickHandler, Context context) {
        mClickHandler = clickHandler;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_card, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String recipeName = mRecipes.get(position).getName();
        if (recipeName == null) recipeName = mContext.getString(R.string.unnamed_recipe);
        holder.nameTextView.setText(recipeName);
    }

    @Override
    public int getItemCount() {
        return (mRecipes == null ? 0 : mRecipes.size());
    }

    public void setRecipes(List<Recipe> recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView nameTextView;

        ViewHolder(CardView cardView) {
            super(cardView);
            nameTextView = cardView.findViewById(R.id.tv_recipe_name);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickHandler.onClick(mRecipes.get(getAdapterPosition()));
        }
    }

    public interface OnClickHandler {
        void onClick(Recipe recipe);
    }

}
