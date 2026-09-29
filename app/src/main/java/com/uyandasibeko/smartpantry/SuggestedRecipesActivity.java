package com.uyandasibeko.smartpantry;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Locale;
import android.content.Intent;
public class SuggestedRecipesActivity extends AppCompatActivity {

    private ListView listSuggestedRecipes;
    private TextView textNoRecipes;
    private Button buttonBackToPantry;

    private DatabaseHelper databaseHelper;
    private RecipeAdapter recipeAdapter;

    private final ArrayList<Recipe> suggestedRecipes =
            new ArrayList<>();

    private final ArrayList<PantryItem> pantryItems =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_suggested_recipes);

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

        listSuggestedRecipes =
                findViewById(R.id.listSuggestedRecipes);
        textNoRecipes = findViewById(R.id.textNoRecipes);
        buttonBackToPantry =
                findViewById(R.id.buttonBackToPantry);

        databaseHelper = new DatabaseHelper(this);

        recipeAdapter = new RecipeAdapter(
                this,
                suggestedRecipes
        );

        listSuggestedRecipes.setAdapter(recipeAdapter);
        listSuggestedRecipes.setEmptyView(textNoRecipes);

        buttonBackToPantry.setOnClickListener(view -> finish());
        listSuggestedRecipes.setOnItemClickListener(
                (parent, view, position, id) -> {
                    Recipe selectedRecipe =
                            suggestedRecipes.get(position);

                    Intent intent = new Intent(
                            SuggestedRecipesActivity.this,
                            RecipeDetailActivity.class
                    );

                    intent.putExtra(
                            "recipe_id",
                            selectedRecipe.getId()
                    );

                    intent.putExtra(
                            "recipe_name",
                            selectedRecipe.getName()
                    );

                    intent.putExtra(
                            "recipe_steps",
                            selectedRecipe.getSteps()
                    );

                    startActivity(intent);
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {
        loadPantryItems();
        suggestedRecipes.clear();

        Cursor recipeCursor = databaseHelper.getAllRecipes();

        try {
            int idIndex = recipeCursor.getColumnIndexOrThrow(
                    DatabaseHelper.RECIPE_ID
            );

            int nameIndex = recipeCursor.getColumnIndexOrThrow(
                    DatabaseHelper.RECIPE_NAME
            );

            int stepsIndex = recipeCursor.getColumnIndexOrThrow(
                    DatabaseHelper.RECIPE_STEPS
            );

            while (recipeCursor.moveToNext()) {
                int recipeId = recipeCursor.getInt(idIndex);

                if (canMakeRecipe(recipeId)) {
                    Recipe recipe = new Recipe(
                            recipeId,
                            recipeCursor.getString(nameIndex),
                            recipeCursor.getString(stepsIndex)
                    );

                    suggestedRecipes.add(recipe);
                }
            }
        } finally {
            recipeCursor.close();
        }

        recipeAdapter.notifyDataSetChanged();
    }

    private void loadPantryItems() {
        pantryItems.clear();

        Cursor pantryCursor =
                databaseHelper.getAllPantryItems();

        try {
            int idIndex = pantryCursor.getColumnIndexOrThrow(
                    DatabaseHelper.COLUMN_ID
            );

            int nameIndex = pantryCursor.getColumnIndexOrThrow(
                    DatabaseHelper.COLUMN_NAME
            );

            int quantityIndex = pantryCursor.getColumnIndexOrThrow(
                    DatabaseHelper.COLUMN_QUANTITY
            );

            int unitIndex = pantryCursor.getColumnIndexOrThrow(
                    DatabaseHelper.COLUMN_UNIT
            );

            int expiryIndex = pantryCursor.getColumnIndexOrThrow(
                    DatabaseHelper.COLUMN_EXPIRY_DATE
            );

            while (pantryCursor.moveToNext()) {
                PantryItem pantryItem = new PantryItem(
                        pantryCursor.getInt(idIndex),
                        pantryCursor.getString(nameIndex),
                        pantryCursor.getDouble(quantityIndex),
                        pantryCursor.getString(unitIndex),
                        pantryCursor.getString(expiryIndex)
                );

                pantryItems.add(pantryItem);
            }
        } finally {
            pantryCursor.close();
        }
    }

    private boolean canMakeRecipe(int recipeId) {
        Cursor ingredientCursor =
                databaseHelper.getRecipeIngredients(recipeId);

        try {
            int nameIndex =
                    ingredientCursor.getColumnIndexOrThrow(
                            DatabaseHelper.INGREDIENT_NAME
                    );

            int quantityIndex =
                    ingredientCursor.getColumnIndexOrThrow(
                            DatabaseHelper.REQUIRED_QUANTITY
                    );

            int unitIndex =
                    ingredientCursor.getColumnIndexOrThrow(
                            DatabaseHelper.INGREDIENT_UNIT
                    );

            while (ingredientCursor.moveToNext()) {
                String requiredName =
                        ingredientCursor.getString(nameIndex);
                double requiredQuantity =
                        ingredientCursor.getDouble(quantityIndex);
                String requiredUnit =
                        ingredientCursor.getString(unitIndex);

                double availableQuantity =
                        getAvailableQuantity(
                                requiredName,
                                requiredUnit
                        );

                double convertedRequiredQuantity =
                        convertToBaseUnit(
                                requiredQuantity,
                                requiredUnit
                        );

                if (availableQuantity
                        < convertedRequiredQuantity) {
                    return false;
                }
            }

            return true;
        } finally {
            ingredientCursor.close();
        }
    }

    private double getAvailableQuantity(
            String requiredName,
            String requiredUnit
    ) {
        double totalQuantity = 0;

        String normalizedRequiredName =
                normalizeIngredientName(requiredName);

        String requiredCategory =
                getUnitCategory(requiredUnit);

        for (PantryItem pantryItem : pantryItems) {
            String normalizedPantryName =
                    normalizeIngredientName(
                            pantryItem.getName()
                    );

            String pantryCategory =
                    getUnitCategory(pantryItem.getUnit());

            boolean sameIngredient =
                    normalizedRequiredName.equals(
                            normalizedPantryName
                    );

            boolean compatibleUnits =
                    requiredCategory.equals(pantryCategory);

            if (sameIngredient && compatibleUnits) {
                totalQuantity += convertToBaseUnit(
                        pantryItem.getQuantity(),
                        pantryItem.getUnit()
                );
            }
        }

        return totalQuantity;
    }

    private String normalizeIngredientName(String name) {
        String normalized = name
                .trim()
                .toLowerCase(Locale.ROOT);

        switch (normalized) {
            case "tomatoes":
                return "tomato";
            case "potatoes":
                return "potato";
            case "eggs":
                return "egg";
            case "bananas":
                return "banana";
            case "apples":
                return "apple";
            case "oranges":
                return "orange";
            case "beans":
                return "beans";
            default:
                return normalized;
        }
    }

    private String getUnitCategory(String unit) {
        String normalizedUnit =
                unit.trim().toLowerCase(Locale.ROOT);

        if (normalizedUnit.equals("g")
                || normalizedUnit.equals("kg")) {
            return "mass";
        }

        if (normalizedUnit.equals("ml")
                || normalizedUnit.equals("l")) {
            return "liquid";
        }

        if (normalizedUnit.equals("item")
                || normalizedUnit.equals("items")) {
            return "count";
        }

        return "unknown";
    }

    private double convertToBaseUnit(
            double quantity,
            String unit
    ) {
        String normalizedUnit =
                unit.trim().toLowerCase(Locale.ROOT);

        switch (normalizedUnit) {
            case "kg":
                return quantity * 1000;
            case "l":
                return quantity * 1000;
            default:
                return quantity;
        }
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}