package com.theshoqanebi.adbwifi;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.color.MaterialColors;
import com.theshoqanebi.adbwifi.databinding.ActivityMainBinding;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private final WifiStateReceiver wifiStateReceiver = new WifiStateReceiver();
    ActivityMainBinding binding;
    private boolean toggleStatus = false;
    boolean isRooted = Utils.isDeviceRooted();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int dynamicColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorPrimary, Color.BLACK);
        getWindow().setStatusBarColor(dynamicColor);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.settings.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        });

        if (!isRooted) {
            binding.hint.setOnClickListener(v -> {
                startActivity(new Intent(getApplicationContext(), RootlessActivity.class));
            });
            binding.hint.setText(getString(R.string.rootless_error));
            binding.hint.setTextColor(getColor(R.color.colorPrimary));
            return;
        }

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
                showMessage(R.string.error_message);
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
        binding.status.setOnClickListener(new ToggleClickListener());
    }

    private void lockToggle() {
        Utils.setAdbWifiStatus(false, getApplicationContext());
        setToggleStatus(false);
        binding.hint.setText(R.string.wifi_off);
        binding.status.setOnClickListener(null);
    }

    private void setToggleStatus(boolean status) {
        toggleStatus = status;
        if (!status) {
            binding.status.setImageResource(R.drawable.ic_off);
            binding.hint.setText("");
        } else {
            binding.status.setImageResource(R.drawable.ic_on);
            new Thread(() -> {
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress("8.8.8.8", 80));
                    String ip = socket.getLocalAddress().toString();
                    String hint = "adb connect " + ip.substring(1) + ":" + Utils.DEFAULT_PORT;
                    MainActivity.this.runOnUiThread(() -> binding.hint.setText(hint));
                } catch (IOException exception) {
                    MainActivity.this.runOnUiThread(() -> showMessage(R.string.error_message));
                }
            }).start();
        }
    }

    public void showMessage(int resource) {
        Toast.makeText(getApplicationContext(), resource, Toast.LENGTH_SHORT).show();
    }

    private class ToggleClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (Utils.isWifiConnected(MainActivity.this)) {
                boolean ret = Utils.setAdbWifiStatus(!toggleStatus, getApplicationContext());
                if (ret) {
                    toggleStatus = !toggleStatus;
                    setToggleStatus(toggleStatus);

                    if (toggleStatus) {
                        showMessage(R.string.service_started);
                    } else {
                        showMessage(R.string.service_stoped);
                    }
                } else {
                    showMessage(R.string.error_message);
                }
            } else {
                lockToggle();
            }
        }
    }
}
