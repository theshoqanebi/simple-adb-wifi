package com.theshoqanebi.adbwifi;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo.State;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Utils {

    public static final int DEFAULT_PORT = 5555;
    private static final String NEW_LINE = "\n";

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            if (activeNetwork != null) {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
                if (networkCapabilities != null) {
                    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                }
            }
        }
        return false;
    }

    /**
     * Returns this phone's IPv4 address on the active Wi-Fi network, or null when there is none.
     * Reads the network's own configuration, so it works on networks without internet access.
     */
    @Nullable
    public static String getWifiIpAddress(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return null;
        }
        Network activeNetwork = connectivityManager.getActiveNetwork();
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
        if (capabilities == null || !capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            return null;
        }
        LinkProperties linkProperties = connectivityManager.getLinkProperties(activeNetwork);
        if (linkProperties == null) {
            return null;
        }
        for (LinkAddress linkAddress : linkProperties.getLinkAddresses()) {
            if (linkAddress.getAddress() instanceof Inet4Address) {
                return linkAddress.getAddress().getHostAddress();
            }
        }
        return null;
    }

    public static boolean getAdbStatus(Context context) {
        try {
            Process process = Runtime.getRuntime().exec("getprop init.svc.adbd");
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
            return result.toString().trim().equals("running");
        } catch (Exception e) {
            Toast.makeText(context, "Make sure you have root access", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public static boolean isDeviceRooted() {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataOutputStream.writeBytes("exit");
            dataOutputStream.flush();
            process.waitFor();
            return process.exitValue() == 0;
        }catch (Exception e) {
            return false;
        }
    }

    public static boolean setAdbWifiStatus(boolean status, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("PORT", Context.MODE_PRIVATE);
        String PORT = preferences.getString("port", String.valueOf(DEFAULT_PORT));
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataOutputStream.writeBytes("setprop service.adb.tcp.port " + PORT + NEW_LINE);
            if (status) {
                dataOutputStream.writeBytes("start adbd" + NEW_LINE);
            } else {
                dataOutputStream.writeBytes("stop adbd" + NEW_LINE);
            }
            dataOutputStream.writeBytes("exit" + NEW_LINE);
            dataOutputStream.flush();
            process.waitFor();
            return process.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
