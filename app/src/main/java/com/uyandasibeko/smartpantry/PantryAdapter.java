package com.uyandasibeko.smartpantry;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PantryAdapter extends ArrayAdapter<PantryItem> {

    private static final String PREFERENCES_NAME =
            "smart_pantry_settings";
    private static final String KEY_EXPIRY_ALERTS =
            "expiry_alerts_enabled";

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
            displayPantryItem(viewHolder, pantryItem);
        }

        return convertView;
    }

    private void displayPantryItem(
            ViewHolder viewHolder,
            PantryItem pantryItem
    ) {
        DecimalFormat quantityFormat = new DecimalFormat("0.##");

        viewHolder.textName.setText(pantryItem.getName());

        String quantityText =
                quantityFormat.format(pantryItem.getQuantity())
                        + " "
                        + pantryItem.getUnit();

        viewHolder.textQuantity.setText(quantityText);

        String expiryDate = pantryItem.getExpiryDate();

        viewHolder.textExpiry.setTextColor(
                Color.parseColor("#666666")
        );

        if (expiryDate == null || expiryDate.isEmpty()) {
            viewHolder.textExpiry.setVisibility(View.GONE);
            return;
        }

        viewHolder.textExpiry.setVisibility(View.VISIBLE);

        SharedPreferences preferences =
                getContext().getSharedPreferences(
                        PREFERENCES_NAME,
                        Context.MODE_PRIVATE
                );

        boolean alertsEnabled = preferences.getBoolean(
                KEY_EXPIRY_ALERTS,
                false
        );

        String expiryMessage = "Expires: " + expiryDate;

        if (alertsEnabled) {
            String warning = getExpiryWarning(expiryDate);

            if (!warning.isEmpty()) {
                expiryMessage += "\n" + warning;
                viewHolder.textExpiry.setTextColor(
                        Color.parseColor("#B00020")
                );
            }
        }

        viewHolder.textExpiry.setText(expiryMessage);
    }

    private String getExpiryWarning(String expiryDate) {
        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.ROOT
                );

        dateFormat.setLenient(false);

        try {
            Date expiry = dateFormat.parse(expiryDate);
            Date today = dateFormat.parse(
                    dateFormat.format(new Date())
            );

            if (expiry == null || today == null) {
                return "";
            }

            long difference = expiry.getTime() - today.getTime();

            long daysRemaining = TimeUnit.MILLISECONDS.toDays(
                    difference
            );

            if (daysRemaining < 0) {
                return "Expired";
            }

            if (daysRemaining == 0) {
                return "Expires today";
            }

            if (daysRemaining <= 7) {
                return "Expiring soon: "
                        + daysRemaining
                        + " day(s) remaining";
            }

            return "";
        } catch (ParseException exception) {
            return "";
        }
    }

    private static class ViewHolder {
        TextView textName;
        TextView textQuantity;
        TextView textExpiry;
    }
}