package com.uyandasibeko.smartpantry;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddEditIngredientActivity extends AppCompatActivity {

    private TextView textFormTitle;
    private EditText editIngredientName;
    private EditText editQuantity;
    private EditText editUnit;
    private EditText editExpiryDate;

    private Button buttonSaveIngredient;
    private Button buttonCancel;

    private DatabaseHelper databaseHelper;

    private int itemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_ingredient);

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

        textFormTitle = findViewById(R.id.textFormTitle);
        editIngredientName = findViewById(R.id.editIngredientName);
        editQuantity = findViewById(R.id.editQuantity);
        editUnit = findViewById(R.id.editUnit);
        editExpiryDate = findViewById(R.id.editExpiryDate);

        buttonSaveIngredient = findViewById(R.id.buttonSaveIngredient);
        buttonCancel = findViewById(R.id.buttonCancel);

        databaseHelper = new DatabaseHelper(this);

        loadItemForEditing();

        buttonSaveIngredient.setOnClickListener(view -> saveIngredient());
        buttonCancel.setOnClickListener(view -> finish());
    }

    private void loadItemForEditing() {
        itemId = getIntent().getIntExtra("item_id", -1);

        if (itemId == -1) {
            textFormTitle.setText("Add Ingredient");
            buttonSaveIngredient.setText("Save Ingredient");
            return;
        }

        textFormTitle.setText("Edit Ingredient");
        buttonSaveIngredient.setText("Update Ingredient");

        editIngredientName.setText(
                getIntent().getStringExtra("item_name")
        );

        editQuantity.setText(
                String.valueOf(
                        getIntent().getDoubleExtra("item_quantity", 0)
                )
        );

        editUnit.setText(
                getIntent().getStringExtra("item_unit")
        );

        String expiryDate =
                getIntent().getStringExtra("item_expiry");

        if (expiryDate != null) {
            editExpiryDate.setText(expiryDate);
        }
    }

    private void saveIngredient() {
        String name = editIngredientName.getText().toString().trim();
        String quantityText = editQuantity.getText().toString().trim();
        String unit = editUnit.getText().toString().trim().toLowerCase();
        String expiryDate = editExpiryDate.getText().toString().trim();

        if (name.isEmpty()) {
            editIngredientName.setError("Ingredient name is required");
            editIngredientName.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {
            editQuantity.setError("Quantity is required");
            editQuantity.requestFocus();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException exception) {
            editQuantity.setError("Enter a valid number");
            editQuantity.requestFocus();
            return;
        }

        if (quantity <= 0) {
            editQuantity.setError("Quantity must be greater than zero");
            editQuantity.requestFocus();
            return;
        }

        if (unit.isEmpty()) {
            editUnit.setError("Unit is required");
            editUnit.requestFocus();
            return;
        }

        if (!isValidUnit(unit)) {
            editUnit.setError("Use items, g, kg, ml or l");
            editUnit.requestFocus();
            return;
        }

        if (!expiryDate.isEmpty()
                && !expiryDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            editExpiryDate.setError("Use the format YYYY-MM-DD");
            editExpiryDate.requestFocus();
            return;
        }

        if (itemId == -1) {
            addNewIngredient(name, quantity, unit, expiryDate);
        } else {
            updateIngredient(name, quantity, unit, expiryDate);
        }
    }

    private void addNewIngredient(
            String name,
            double quantity,
            String unit,
            String expiryDate
    ) {
        long result = databaseHelper.addPantryItem(
                name,
                quantity,
                unit,
                expiryDate
        );

        if (result != -1) {
            Toast.makeText(
                    this,
                    "Ingredient saved successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        } else {
            Toast.makeText(
                    this,
                    "Ingredient could not be saved",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void updateIngredient(
            String name,
            double quantity,
            String unit,
            String expiryDate
    ) {
        int rowsUpdated = databaseHelper.updatePantryItem(
                itemId,
                name,
                quantity,
                unit,
                expiryDate
        );

        if (rowsUpdated > 0) {
            Toast.makeText(
                    this,
                    "Ingredient updated successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        } else {
            Toast.makeText(
                    this,
                    "Ingredient could not be updated",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private boolean isValidUnit(String unit) {
        return unit.equals("items")
                || unit.equals("g")
                || unit.equals("kg")
                || unit.equals("ml")
                || unit.equals("l");
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}