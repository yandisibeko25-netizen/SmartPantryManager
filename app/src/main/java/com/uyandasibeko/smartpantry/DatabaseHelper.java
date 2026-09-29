package com.uyandasibeko.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_PANTRY = "pantry_items";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_UNIT = "unit";
    public static final String COLUMN_EXPIRY_DATE = "expiry_date";

    public static final String TABLE_RECIPES = "recipes";
    public static final String RECIPE_ID = "id";
    public static final String RECIPE_NAME = "name";
    public static final String RECIPE_STEPS = "steps";

    public static final String TABLE_RECIPE_INGREDIENTS =
            "recipe_ingredients";
    public static final String RECIPE_INGREDIENT_ID = "id";
    public static final String INGREDIENT_RECIPE_ID = "recipe_id";
    public static final String INGREDIENT_NAME = "ingredient_name";
    public static final String REQUIRED_QUANTITY = "required_quantity";
    public static final String INGREDIENT_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        createPantryTable(database);
        createRecipeTables(database);
        seedRecipes(database);
    }

    private void createPantryTable(SQLiteDatabase database) {
        String sql =
                "CREATE TABLE " + TABLE_PANTRY + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME + " TEXT NOT NULL, " +
                        COLUMN_QUANTITY + " REAL NOT NULL, " +
                        COLUMN_UNIT + " TEXT NOT NULL, " +
                        COLUMN_EXPIRY_DATE + " TEXT" +
                        ")";

        database.execSQL(sql);
    }

    private void createRecipeTables(SQLiteDatabase database) {
        String recipesSql =
                "CREATE TABLE " + TABLE_RECIPES + " (" +
                        RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        RECIPE_NAME + " TEXT NOT NULL, " +
                        RECIPE_STEPS + " TEXT NOT NULL" +
                        ")";

        String ingredientsSql =
                "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                        RECIPE_INGREDIENT_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        INGREDIENT_RECIPE_ID + " INTEGER NOT NULL, " +
                        INGREDIENT_NAME + " TEXT NOT NULL, " +
                        REQUIRED_QUANTITY + " REAL NOT NULL, " +
                        INGREDIENT_UNIT + " TEXT NOT NULL" +
                        ")";

        database.execSQL(recipesSql);
        database.execSQL(ingredientsSql);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase database,
            int oldVersion,
            int newVersion
    ) {
        if (oldVersion < 2) {
            createRecipeTables(database);
            seedRecipes(database);
        }
    }

    public long addPantryItem(
            String name,
            double quantity,
            String unit,
            String expiryDate
    ) {
        SQLiteDatabase database = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_UNIT, unit);
        values.put(COLUMN_EXPIRY_DATE, expiryDate);

        return database.insert(TABLE_PANTRY, null, values);
    }

    public Cursor getAllPantryItems() {
        SQLiteDatabase database = getReadableDatabase();

        return database.query(
                TABLE_PANTRY,
                null,
                null,
                null,
                null,
                null,
                COLUMN_NAME + " ASC"
        );
    }

    public int updatePantryItem(
            int id,
            String name,
            double quantity,
            String unit,
            String expiryDate
    ) {
        SQLiteDatabase database = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_UNIT, unit);
        values.put(COLUMN_EXPIRY_DATE, expiryDate);

        return database.update(
                TABLE_PANTRY,
                values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public int deletePantryItem(int id) {
        SQLiteDatabase database = getWritableDatabase();

        return database.delete(
                TABLE_PANTRY,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public Cursor getAllRecipes() {
        SQLiteDatabase database = getReadableDatabase();

        return database.query(
                TABLE_RECIPES,
                null,
                null,
                null,
                null,
                null,
                RECIPE_NAME + " ASC"
        );
    }

    public Cursor getRecipeIngredients(int recipeId) {
        SQLiteDatabase database = getReadableDatabase();

        return database.query(
                TABLE_RECIPE_INGREDIENTS,
                null,
                INGREDIENT_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                INGREDIENT_NAME + " ASC"
        );
    }

    private void seedRecipes(SQLiteDatabase database) {
        insertRecipe(
                database,
                "Scrambled Eggs",
                "Beat the eggs. Melt the butter in a pan. Add the eggs and stir until cooked.",
                new String[]{"egg", "butter"},
                new double[]{2, 10},
                new String[]{"items", "g"}
        );

        insertRecipe(
                database,
                "Tomato Omelette",
                "Beat the eggs. Chop the tomato. Cook everything with butter until set.",
                new String[]{"egg", "tomato", "butter"},
                new double[]{2, 1, 10},
                new String[]{"items", "items", "g"}
        );

        insertRecipe(
                database,
                "Cheese Omelette",
                "Beat the eggs. Cook with butter and add cheese before folding.",
                new String[]{"egg", "cheese", "butter"},
                new double[]{2, 50, 10},
                new String[]{"items", "g", "g"}
        );

        insertRecipe(
                database,
                "Cheese Sandwich",
                "Butter the bread, add cheese and close the sandwich.",
                new String[]{"bread", "cheese", "butter"},
                new double[]{2, 40, 5},
                new String[]{"items", "g", "g"}
        );

        insertRecipe(
                database,
                "Tomato Sandwich",
                "Slice the tomato. Butter the bread and add the tomato slices.",
                new String[]{"bread", "tomato", "butter"},
                new double[]{2, 1, 5},
                new String[]{"items", "items", "g"}
        );

        insertRecipe(
                database,
                "Egg Sandwich",
                "Cook the egg, place it on the bread and add mayonnaise.",
                new String[]{"bread", "egg", "mayonnaise"},
                new double[]{2, 1, 15},
                new String[]{"items", "items", "g"}
        );

        insertRecipe(
                database,
                "Tomato Pasta",
                "Cook the pasta. Cook chopped tomato and garlic, then combine.",
                new String[]{"pasta", "tomato", "garlic"},
                new double[]{100, 2, 1},
                new String[]{"g", "items", "items"}
        );

        insertRecipe(
                database,
                "Garlic Pasta",
                "Cook the pasta. Melt butter with garlic and mix with the pasta.",
                new String[]{"pasta", "garlic", "butter"},
                new double[]{100, 2, 10},
                new String[]{"g", "items", "g"}
        );

        insertRecipe(
                database,
                "Tuna Pasta",
                "Cook the pasta. Add tuna and chopped tomato, then mix.",
                new String[]{"pasta", "tuna", "tomato"},
                new double[]{100, 100, 1},
                new String[]{"g", "g", "items"}
        );

        insertRecipe(
                database,
                "Rice and Beans",
                "Cook the rice and beans separately, then combine and serve.",
                new String[]{"rice", "beans"},
                new double[]{100, 100},
                new String[]{"g", "g"}
        );

        insertRecipe(
                database,
                "Egg Fried Rice",
                "Cook the egg in oil, add cooked rice and stir until hot.",
                new String[]{"rice", "egg", "oil"},
                new double[]{100, 1, 10},
                new String[]{"g", "items", "ml"}
        );

        insertRecipe(
                database,
                "Mashed Potatoes",
                "Boil the potatoes. Mash them with butter and milk.",
                new String[]{"potato", "butter", "milk"},
                new double[]{3, 20, 100},
                new String[]{"items", "g", "ml"}
        );

        insertRecipe(
                database,
                "Baked Potato",
                "Coat the potato with oil and bake until soft.",
                new String[]{"potato", "oil"},
                new double[]{1, 5},
                new String[]{"items", "ml"}
        );

        insertRecipe(
                database,
                "Banana Smoothie",
                "Blend the banana and milk until smooth.",
                new String[]{"banana", "milk"},
                new double[]{1, 250},
                new String[]{"items", "ml"}
        );

        insertRecipe(
                database,
                "Fruit Salad",
                "Chop the banana, apple and orange. Mix and serve.",
                new String[]{"banana", "apple", "orange"},
                new double[]{1, 1, 1},
                new String[]{"items", "items", "items"}
        );
    }

    private void insertRecipe(
            SQLiteDatabase database,
            String name,
            String steps,
            String[] ingredientNames,
            double[] quantities,
            String[] units
    ) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(RECIPE_NAME, name);
        recipeValues.put(RECIPE_STEPS, steps);

        long recipeId = database.insert(
                TABLE_RECIPES,
                null,
                recipeValues
        );

        for (int index = 0; index < ingredientNames.length; index++) {
            ContentValues ingredientValues = new ContentValues();
            ingredientValues.put(INGREDIENT_RECIPE_ID, recipeId);
            ingredientValues.put(
                    INGREDIENT_NAME,
                    ingredientNames[index]
            );
            ingredientValues.put(
                    REQUIRED_QUANTITY,
                    quantities[index]
            );
            ingredientValues.put(INGREDIENT_UNIT, units[index]);

            database.insert(
                    TABLE_RECIPE_INGREDIENTS,
                    null,
                    ingredientValues
            );
        }
    }
}