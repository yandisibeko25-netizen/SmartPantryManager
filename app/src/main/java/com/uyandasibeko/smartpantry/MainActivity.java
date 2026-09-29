package com.uyandasibeko.smartpantry;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listPantryItems;
    private TextView textEmptyPantry;
    private Button buttonAddIngredient;
    private Button buttonSuggestedRecipes;

    private DatabaseHelper databaseHelper;
    private PantryAdapter pantryAdapter;
    private ArrayList<PantryItem> pantryItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (view, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    view.setPadding(
                            systemBars.left + 24,
                            systemBars.top + 24,
                            systemBars.right + 24,
                            systemBars.bottom + 24
                    );

                    return insets;
                }
        );

        listPantryItems = findViewById(R.id.listPantryItems);
        textEmptyPantry = findViewById(R.id.textEmptyPantry);
        buttonAddIngredient = findViewById(R.id.buttonAddIngredient);
        buttonSuggestedRecipes =
                findViewById(R.id.buttonSuggestedRecipes);
        databaseHelper = new DatabaseHelper(this);
        pantryItems = new ArrayList<>();

        pantryAdapter = new PantryAdapter(this, pantryItems);
        listPantryItems.setAdapter(pantryAdapter);
        listPantryItems.setEmptyView(textEmptyPantry);

        buttonAddIngredient.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AddEditIngredientActivity.class
            );
            startActivity(intent);
        });
        buttonSuggestedRecipes.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );

            startActivity(intent);
        });
        listPantryItems.setOnItemClickListener(
                (parent, view, position, id) -> {
                    PantryItem selectedItem = pantryItems.get(position);
                    openEditScreen(selectedItem);
                }
        );

        listPantryItems.setOnItemLongClickListener(
                (parent, view, position, id) -> {
                    PantryItem selectedItem = pantryItems.get(position);
                    showDeleteConfirmation(selectedItem);
                    return true;
                }
        );
    }

    private void openEditScreen(PantryItem pantryItem) {
        Intent intent = new Intent(
                MainActivity.this,
                AddEditIngredientActivity.class
        );

        intent.putExtra("item_id", pantryItem.getId());
        intent.putExtra("item_name", pantryItem.getName());
        intent.putExtra(
                "item_quantity",
                pantryItem.getQuantity()
        );
        intent.putExtra("item_unit", pantryItem.getUnit());
        intent.putExtra(
                "item_expiry",
                pantryItem.getExpiryDate()
        );

        startActivity(intent);
    }

    private void showDeleteConfirmation(PantryItem pantryItem) {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient")
                .setMessage(
                        "Delete "
                                + pantryItem.getName()
                                + " from your pantry?"
                )
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    int rowsDeleted = databaseHelper.deletePantryItem(
                            pantryItem.getId()
                    );

                    if (rowsDeleted > 0) {
                        Toast.makeText(
                                this,
                                "Ingredient deleted",
                                Toast.LENGTH_SHORT
                        ).show();

                        loadPantryItems();
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems();
    }

    private void loadPantryItems() {
        pantryItems.clear();

        Cursor cursor = databaseHelper.getAllPantryItems();

        try {
            int idIndex =
                    cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID);
            int nameIndex =
                    cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME);
            int quantityIndex =
                    cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QUANTITY);
            int unitIndex =
                    cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_UNIT);
            int expiryIndex =
                    cursor.getColumnIndexOrThrow(
                            DatabaseHelper.COLUMN_EXPIRY_DATE
                    );

            while (cursor.moveToNext()) {
                PantryItem pantryItem = new PantryItem(
                        cursor.getInt(idIndex),
                        cursor.getString(nameIndex),
                        cursor.getDouble(quantityIndex),
                        cursor.getString(unitIndex),
                        cursor.getString(expiryIndex)
                );

                pantryItems.add(pantryItem);
            }
        } finally {
            cursor.close();
        }

        pantryAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}