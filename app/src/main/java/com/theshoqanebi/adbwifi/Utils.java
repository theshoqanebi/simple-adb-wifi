package com.theshoqanebi.adbwifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo.State;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    public static boolean setAdbWifiStatus(boolean status) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataOutputStream.writeBytes("setprop service.adb.tcp.port " + DEFAULT_PORT + NEW_LINE);
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
