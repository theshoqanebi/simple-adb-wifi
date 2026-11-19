package com.theshoqanebi.adbwifi;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;

import androidx.annotation.NonNull;

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
        binding.port.setSelection(binding.port.length());
        binding.portLayout.setHelperText(activity.getString(R.string.port_helper));

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
                    binding.portLayout.setError(activity.getString(R.string.port_error));
                    binding.save.setEnabled(false);
                } else {
                    binding.portLayout.setError(null);
                    binding.portLayout.setHelperText(activity.getString(R.string.port_helper));
                    binding.save.setEnabled(true);
                }
            }
        });

        // Open with the keyboard up, since editing the port is the only thing to do here.
        binding.port.requestFocus();
        if (getWindow() != null) {
            getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }
}
