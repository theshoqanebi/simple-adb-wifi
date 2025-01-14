package com.theshoqanebi.adbwifi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.Spanned;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.color.MaterialColors;
import com.theshoqanebi.adbwifi.databinding.ActivityRootlessBinding;

public class RootlessActivity extends AppCompatActivity {
    ActivityRootlessBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRootlessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int dynamicColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorPrimary, Color.BLACK);
        getWindow().setStatusBarColor(dynamicColor);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.openDeveloper.setOnClickListener(v -> {
            openDeveloperOptions();
        });

        StringBuilder steps = new StringBuilder();
        String newLine = "<br>";
        String startBold = "<b>";
        String endBold = "</b>";
        String startRedColor = "<font color=\"#FF0000\">";
        String endRedColor = "</font>";
        steps.append(startBold).append("1. Connect the device via USB:").append(endBold).append(newLine);
        steps.append("Ensure the device is connected to your computer via USB, and USB debugging is enabled in the device's developer options.").append(newLine);
        steps.append(newLine);
        steps.append(startBold).append("2. Find the device's IP address:").append(endBold).append(newLine);
        steps.append("On the Android device, go to Settings > About phone > Status (or similar) to find the IP address, or use ").append(startRedColor).append(" adb shell ip addr show wlan0 ").append(endRedColor).append("in the terminal.").append(newLine);
        steps.append(newLine);
        steps.append(startBold).append("3. Restart ADB in TCP/IP mode:").append(endBold).append(newLine);
        steps.append("Run the following command in your terminal or command prompt:").append(startRedColor).append(" adb tcpip 5555 ").append(endRedColor).append("Replace 5555 with your desired port number if needed (default is 5555).").append(newLine);
        steps.append(newLine);
        steps.append(startBold).append("4. Connect ADB over Wi-Fi:").append(endBold).append(newLine);
        steps.append("Use the following command to connect to the device over Wi-Fi:").append(startRedColor).append(" adb connect <device-ip>:5555 ").append(endRedColor).append("Replace <device-ip> with the actual IP address of your device.").append(newLine);
        steps.append(newLine);
        steps.append(startBold).append("5. Verify the connection:").append(endBold).append(newLine);
        steps.append("Check connected devices with:").append(startRedColor).append(" adb devices ").append(endRedColor).append("The device should appear in the list with its IP address.");

        Spanned spanned;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            spanned = Html.fromHtml(steps.toString(), Html.FROM_HTML_MODE_LEGACY);
        } else {
            spanned = Html.fromHtml(steps.toString());
        }

        binding.steps.setText(spanned);
    }

    private void openDeveloperOptions() {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getText(R.string.developer_options_error), Toast.LENGTH_SHORT).show();
        }
    }
}