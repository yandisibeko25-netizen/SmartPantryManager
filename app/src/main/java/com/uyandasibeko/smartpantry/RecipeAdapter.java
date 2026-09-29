package com.uyandasibeko.smartpantry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class RecipeAdapter extends ArrayAdapter<Recipe> {

    public RecipeAdapter(
            @NonNull Context context,
            @NonNull List<Recipe> recipes
    ) {
        super(context, 0, recipes);
    }

    @NonNull
    @Override
    public View getView(
            int position,
            @Nullable View convertView,
            @NonNull ViewGroup parent
    ) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.row_recipe,
                    parent,
                    false
            );
        }

        TextView textRecipeName =
                convertView.findViewById(R.id.textRecipeName);

        Recipe recipe = getItem(position);

        if (recipe != null) {
            textRecipeName.setText(recipe.getName());
        }

        return convertView;
    }
}