package com.theshoqanebi.adbwifi;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.theshoqanebi.adbwifi.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private final WifiStateReceiver wifiStateReceiver = new WifiStateReceiver();
    ActivityMainBinding binding;
    private boolean toggleStatus = false;
    private boolean updatingToggle = false;
    boolean isRooted = Utils.isDeviceRooted();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.settings.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        });
        binding.copy.setOnClickListener(v -> copyCommand());

        if (!isRooted) {
            binding.rootless.setOnClickListener(v -> {
                startActivity(new Intent(getApplicationContext(), RootlessActivity.class));
            });
            binding.rootless.setVisibility(View.VISIBLE);
            binding.message.setText(R.string.not_rooted);
            // Without root the switch can never work, so leave only the rootless path.
            binding.status.setVisibility(View.GONE);
            return;
        }

        binding.status.setOnCheckedChangeListener(new ToggleChangeListener());

        wifiStateReceiver.setOnEventListener(new OnEventListener() {
            @Override
            public void onEnabled() {
                resetToggleStatus();
            }

            @Override
            public void onDisabled() {
                lockToggle();
            }

            @Override
            public void onFailure() {
                showMessage(R.string.toggle_error);
            }
        });
        registerReceiver(wifiStateReceiver, new IntentFilter(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION));

        if (Utils.isWifiConnected(getApplicationContext())) {
            resetToggleStatus();
        } else {
            lockToggle();
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(wifiStateReceiver);
        super.onDestroy();
    }

    private void resetToggleStatus() {
        setToggleStatus(Utils.getAdbStatus(getApplicationContext()));
        binding.status.setEnabled(true);
    }

    private void lockToggle() {
        Utils.setAdbWifiStatus(false, getApplicationContext());
        setToggleStatus(false);
        binding.message.setText(R.string.wifi_off);
        binding.status.setEnabled(false);
    }

    private void setToggleStatus(boolean status) {
        toggleStatus = status;
        updatingToggle = true;
        binding.status.setChecked(status);
        updatingToggle = false;
        binding.statusText.setText(status ? R.string.status_on : R.string.status_off);
        ViewCompat.setBackgroundTintList(binding.statusDot,
                ColorStateList.valueOf(getColor(status ? R.color.amber : R.color.slate)));
        if (!status) {
            binding.connectGroup.setVisibility(View.GONE);
            binding.message.setText(R.string.off_message);
            binding.message.setVisibility(View.VISIBLE);
        } else {
            String ip = Utils.getWifiIpAddress(getApplicationContext());
            if (ip == null) {
                binding.connectGroup.setVisibility(View.GONE);
                binding.message.setText(R.string.address_error);
                binding.message.setVisibility(View.VISIBLE);
            } else {
                showAddress(ip, Utils.DEFAULT_PORT);
            }
        }
    }

    private void showAddress(String ip, int port) {
        binding.address.setText(ip);
        binding.port.setText(":" + port);
        binding.command.setText("adb connect " + ip + ":" + port);
        binding.connectGroup.setVisibility(View.VISIBLE);
        binding.message.setVisibility(View.GONE);
    }

    private void copyCommand() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText(getString(R.string.title), binding.command.getText()));
        // Android 13 and newer confirm copies with their own system UI.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            showMessage(R.string.copied);
        }
    }

    public void showMessage(int resource) {
        Toast.makeText(getApplicationContext(), resource, Toast.LENGTH_SHORT).show();
    }

    private class ToggleChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (updatingToggle) {
                return;
            }
            if (Utils.isWifiConnected(MainActivity.this)) {
                boolean ret = Utils.setAdbWifiStatus(isChecked, getApplicationContext());
                if (ret) {
                    setToggleStatus(isChecked);
                } else {
                    setToggleStatus(toggleStatus);
                    showMessage(R.string.toggle_error);
                }
            } else {
                lockToggle();
            }
        }
    }
}
