package com.uyandasibeko.smartpantry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.List;

public class PantryAdapter extends ArrayAdapter<PantryItem> {

    public PantryAdapter(
            @NonNull Context context,
            @NonNull List<PantryItem> pantryItems
    ) {
        super(context, 0, pantryItems);
    }

    @NonNull
    @Override
    public View getView(
            int position,
            @Nullable View convertView,
            @NonNull ViewGroup parent
    ) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.row_pantry_item,
                    parent,
                    false
            );

            viewHolder = new ViewHolder();
            viewHolder.textName =
                    convertView.findViewById(R.id.textIngredientName);
            viewHolder.textQuantity =
                    convertView.findViewById(R.id.textIngredientQuantity);
            viewHolder.textExpiry =
                    convertView.findViewById(R.id.textIngredientExpiry);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PantryItem pantryItem = getItem(position);

        if (pantryItem != null) {
            DecimalFormat quantityFormat = new DecimalFormat("0.##");

            viewHolder.textName.setText(pantryItem.getName());

            String quantityText =
                    quantityFormat.format(pantryItem.getQuantity())
                            + " "
                            + pantryItem.getUnit();

            viewHolder.textQuantity.setText(quantityText);

            String expiryDate = pantryItem.getExpiryDate();

            if (expiryDate == null || expiryDate.isEmpty()) {
                viewHolder.textExpiry.setVisibility(View.GONE);
            } else {
                viewHolder.textExpiry.setVisibility(View.VISIBLE);
                viewHolder.textExpiry.setText(
                        "Expires: " + expiryDate
                );
            }
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView textName;
        TextView textQuantity;
        TextView textExpiry;
    }
}