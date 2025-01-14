package com.theshoqanebi.adbwifi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.color.MaterialColors;
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

        int dynamicColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorPrimary, Color.BLACK);
        getWindow().setStatusBarColor(dynamicColor);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.settings1Values.setOnClickListener(v -> {
            PortDialog portDialog = new PortDialog(this);
            portDialog.show();
            portDialog.setOnDismissListener(dialog -> {
                binding.settings1Values.setText(preferences.getString("port", "5555"));
            });
        });

        binding.settings2Values.setOnCheckedChangeListener((group, checkedId) -> {
            if (binding.settings2Value1.isChecked()) {
                preferences.edit().putInt("su", 1).apply();
            } else {
                preferences.edit().putInt("su", 2).apply();
            }
        });
    }

    private void viewPrepare() {
        binding.settings1Values.setText(preferences.getString("port", "5555"));
        if (preferences.getInt("su", 1) == 1) {
            binding.settings2Value1.setChecked(true);
            binding.settings2Value2.setChecked(false);
        } else {
            binding.settings2Value1.setChecked(false);
            binding.settings2Value2.setChecked(true);
        }
    }
}