package com.theshoqanebi.adbwifi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.theshoqanebi.adbwifi.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding binding;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        viewPrepare();
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.portRow.setOnClickListener(v -> {
            PortDialog portDialog = new PortDialog(this);
            portDialog.show();
            portDialog.setOnDismissListener(dialog -> viewPrepare());
        });
        binding.themeRow.setOnClickListener(v -> showThemePicker());
    }

    private void viewPrepare() {
        binding.portValue.setText(preferences.getString("port", "5555"));
        binding.themeValue.setText(ThemeSettings.LABELS[ThemeSettings.get(this)]);
    }

    private void showThemePicker() {
        String[] labels = new String[ThemeSettings.LABELS.length];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = getString(ThemeSettings.LABELS[i]);
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.settings_theme)
                .setSingleChoiceItems(labels, ThemeSettings.get(this), (dialog, which) -> {
                    dialog.dismiss();
                    if (which != ThemeSettings.get(this)) {
                        // Recreates this screen in the chosen theme.
                        ThemeSettings.set(this, which);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
