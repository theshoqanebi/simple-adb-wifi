package com.theshoqanebi.adbwifi;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

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

        binding.port.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String portString = binding.port.getText().toString();
                int port = Integer.parseInt(portString.isEmpty() ? "0" : portString);
                if (port > 65535 || port < 1024) {
                    binding.port.setBackground(AppCompatResources.getDrawable(activity, R.drawable.port_text_error));
                    binding.save.setClickable(false);
                } else {
                    binding.port.setBackground(AppCompatResources.getDrawable(activity,R.drawable.port_text));
                    binding.save.setClickable(true);
                }
            }
        });
    }

    @Override
    public void setOnShowListener(@Nullable OnShowListener listener) {
        super.setOnShowListener(listener);
        binding.port.requestFocus();
    }
}
