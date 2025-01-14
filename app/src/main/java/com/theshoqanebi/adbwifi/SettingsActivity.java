package com.theshoqanebi.adbwifi;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
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
    }

    private void viewPrepare() {
        binding.settings1Values.setText(preferences.getString("port", "5555"));
    }
}