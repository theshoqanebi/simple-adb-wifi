package com.theshoqanebi.adbwifi;

import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.theshoqanebi.adbwifi.databinding.ActivityMainBinding;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private final WifiStateReceiver wifiStateReceiver = new WifiStateReceiver();
    ActivityMainBinding binding;
    private boolean toggleStatus = false;
    private boolean updatingToggle = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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
        binding.status.setEnabled(true);
    }

    private void lockToggle() {
        Utils.setAdbWifiStatus(false);
        setToggleStatus(false);
        binding.hint.setText(R.string.wifi_off);
        binding.status.setEnabled(false);
    }

    private void setToggleStatus(boolean status) {
        toggleStatus = status;
        updatingToggle = true;
        binding.status.setChecked(status);
        updatingToggle = false;
        if (!status) {
            binding.hint.setText("");
        } else {
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

    private class ToggleChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (updatingToggle) {
                return;
            }
            if (Utils.isWifiConnected(MainActivity.this)) {
                boolean ret = Utils.setAdbWifiStatus(isChecked);
                if (ret) {
                    setToggleStatus(isChecked);

                    if (toggleStatus) {
                        showMessage(R.string.service_started);
                    } else {
                        showMessage(R.string.service_stoped);
                    }
                } else {
                    setToggleStatus(toggleStatus);
                    showMessage(R.string.error_message);
                }
            } else {
                lockToggle();
            }
        }
    }
}
