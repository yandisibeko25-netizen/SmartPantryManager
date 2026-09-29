package com.uyandasibeko.smartpantry;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFERENCES_NAME =
            "smart_pantry_settings";
    private static final String KEY_EXPIRY_ALERTS =
            "expiry_alerts_enabled";

    private Switch switchExpiryAlerts;
    private Button buttonBackFromSettings;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

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

        switchExpiryAlerts =
                findViewById(R.id.switchExpiryAlerts);
        buttonBackFromSettings =
                findViewById(R.id.buttonBackFromSettings);

        preferences = getSharedPreferences(
                PREFERENCES_NAME,
                MODE_PRIVATE
        );

        boolean alertsEnabled = preferences.getBoolean(
                KEY_EXPIRY_ALERTS,
                false
        );

        switchExpiryAlerts.setChecked(alertsEnabled);

        switchExpiryAlerts.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    preferences.edit()
                            .putBoolean(
                                    KEY_EXPIRY_ALERTS,
                                    isChecked
                            )
                            .apply();

                    String message = isChecked
                            ? "Expiry alerts enabled"
                            : "Expiry alerts disabled";

                    Toast.makeText(
                            this,
                            message,
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );

        buttonBackFromSettings.setOnClickListener(
                view -> finish()
        );
    }
}