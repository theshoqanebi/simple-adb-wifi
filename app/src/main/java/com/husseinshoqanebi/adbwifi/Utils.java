package com.husseinshoqanebi.adbwifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Utils {

	public static final int DEFAULT_PORT = 5555;
	private static final String NEW_LINE = "\n";

	public static boolean isWifiConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		State state = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		return state == State.CONNECTED;
	}

	public static boolean getAdbStatus() {
		try {
			Process process = Runtime.getRuntime().exec("getprop init.svc.adbd");
			Scanner scanner = new Scanner(process.getInputStream(), StandardCharsets.UTF_8.name());
			return (scanner.useDelimiter("\\A").next()).trim().equals("running");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
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
