package com.theshoqanebi.adbwifi;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.theshoqanebi.adbwifi.databinding.DialogPortBinding;

public class PortDialog extends Dialog {
    DialogPortBinding binding;
    SharedPreferences preferences;

    public PortDialog(@NonNull Activity activity) {
        super(activity);
        binding = DialogPortBinding.inflate(activity.getLayoutInflater());
        this.setContentView(binding.getRoot());
        preferences = activity.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        binding.port.setText(preferences.getString("port", "5555"));

        binding.save.setOnClickListener(v -> {
            preferences.edit().putString("port", binding.port.getText().toString()).apply();
            this.dismiss();
        });

        binding.cancel.setOnClickListener(v -> {
            this.dismiss();
        });
    }

    @Override
    public void setOnShowListener(@Nullable OnShowListener listener) {
        super.setOnShowListener(listener);
        binding.port.requestFocus();
    }
}
