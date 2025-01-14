package com.theshoqanebi.adbwifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

public class WifiStateReceiver extends BroadcastReceiver {
    private OnEventListener onEventListener;

    public void setOnEventListener(OnEventListener onEventListener) {
        this.onEventListener = onEventListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
            if (!intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {
                if (onEventListener != null) {
                    onEventListener.onDisabled();
                }
            }
            try {
                if (onEventListener != null) {
                    onEventListener.onEnabled();
                }
            } catch (Exception e) {
                if (onEventListener != null) {
                    onEventListener.onFailure();
                }
            }
        }
    }
}
