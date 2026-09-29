package com.uyandasibeko.smartpantry;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView textRecipeDetailName;
    private TextView textRecipeIngredients;
    private TextView textRecipeSteps;
    private Button buttonBackToSuggestions;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (view, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    view.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        textRecipeDetailName =
                findViewById(R.id.textRecipeDetailName);
        textRecipeIngredients =
                findViewById(R.id.textRecipeIngredients);
        textRecipeSteps =
                findViewById(R.id.textRecipeSteps);
        buttonBackToSuggestions =
                findViewById(R.id.buttonBackToSuggestions);

        databaseHelper = new DatabaseHelper(this);

        int recipeId = getIntent().getIntExtra("recipe_id", -1);
        String recipeName =
                getIntent().getStringExtra("recipe_name");
        String recipeSteps =
                getIntent().getStringExtra("recipe_steps");

        textRecipeDetailName.setText(recipeName);
        textRecipeSteps.setText(recipeSteps);

        loadRecipeIngredients(recipeId);

        buttonBackToSuggestions.setOnClickListener(
                view -> finish()
        );
    }

    private void loadRecipeIngredients(int recipeId) {
        Cursor cursor =
                databaseHelper.getRecipeIngredients(recipeId);

        StringBuilder ingredientList = new StringBuilder();
        DecimalFormat quantityFormat = new DecimalFormat("0.##");

        try {
            int nameIndex = cursor.getColumnIndexOrThrow(
                    DatabaseHelper.INGREDIENT_NAME
            );

            int quantityIndex = cursor.getColumnIndexOrThrow(
                    DatabaseHelper.REQUIRED_QUANTITY
            );

            int unitIndex = cursor.getColumnIndexOrThrow(
                    DatabaseHelper.INGREDIENT_UNIT
            );

            while (cursor.moveToNext()) {
                String name = cursor.getString(nameIndex);
                double quantity = cursor.getDouble(quantityIndex);
                String unit = cursor.getString(unitIndex);

                ingredientList
                        .append("• ")
                        .append(quantityFormat.format(quantity))
                        .append(" ")
                        .append(unit)
                        .append(" ")
                        .append(name)
                        .append("\n");
            }
        } finally {
            cursor.close();
        }

        textRecipeIngredients.setText(
                ingredientList.toString().trim()
        );
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}