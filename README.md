# Smart Pantry Manager

Smart Pantry Manager is a Java Android application developed for Mobile App Development 700. It helps users reduce food waste by recording ingredients they already have and suggesting recipes they can make without buying additional ingredients.

## Developer

Uyanda Sibeko  
Third-year student  
Mobile App Development 700

## Main Features

- Add pantry ingredients
- View all pantry ingredients
- Edit pantry ingredients
- Delete pantry ingredients
- Store pantry information permanently using SQLite
- Validate ingredient information
- Store 15 preloaded recipes
- Suggest only recipes for which every required ingredient is available
- Check required ingredient quantities
- Handle kilograms and grams
- Handle litres and millilitres
- Handle simple singular and plural ingredient names
- View recipe ingredients and preparation steps
- Save the expiry-alert setting
- Navigate between Pantry, Recipes and Settings

## Database

The application uses SQLite with `SQLiteOpenHelper`.

The database contains:

- `pantry_items`
- `recipes`
- `recipe_ingredients`

SQLite was selected because it works offline, provides reliable local persistence and is suitable for the structured data used by this application.

## Strict Recipe Matching

A recipe appears in Suggested Recipes only when:

1. Every required ingredient exists in the pantry.
2. Every available quantity is equal to or greater than the required quantity.
3. The pantry and recipe units are compatible.

Partial matches are excluded.

## Technology

- Android Studio
- Java
- XML layouts
- SQLite
- ListView
- Custom Adapters
- Activities and Intents
- SharedPreferences

## Running the Application

1. Open the project in Android Studio.
2. Allow Gradle to finish syncing.
3. Connect an Android device with USB debugging enabled, or start an emulator.
4. Select the device.
5. Run the `app` configuration.

## Minimum Android Version

Android 7.0 (API 24)

## Test Example

Add these pantry items:

- Tomato: 1 item
- Egg: 2 items
- Butter: 10 g

The application should suggest:

- Scrambled Eggs
- Tomato Omelette

Reducing Butter below 10 g should remove both recipes from the suggestions.