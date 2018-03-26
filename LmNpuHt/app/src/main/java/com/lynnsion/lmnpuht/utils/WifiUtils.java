package com.lynnsion.lmnpuht.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

/**
 * Created by dongjiang on 2017/8/16.
 */

public class WifiUtils {
    public void setWifiNeverSleep(Context context) {
        int wifiSleepPolicy = 0;
        wifiSleepPolicy = Settings.System.getInt(context.getContentResolver(),
                Settings.System.WIFI_SLEEP_POLICY,
                Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.WIFI_SLEEP_POLICY,
                Settings.System.WIFI_SLEEP_POLICY_NEVER_WHILE_PLUGGED);
        wifiSleepPolicy = Settings.System.getInt(context.getContentResolver(),
                Settings.System.WIFI_SLEEP_POLICY,
                Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
    }

    public void restoreWifiDormancy(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences("wifi_sleep_policy", Context.MODE_PRIVATE);
        int defaultPolicy = prefs.getInt("WIFI_SLEEP_POLICY_DEFAULT", Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
        Settings.System.putInt(context.getContentResolver(), Settings.System.WIFI_SLEEP_POLICY, defaultPolicy);
    }

    public void setWifiDormancy(Context context) {
        int value = Settings.System.getInt(context.getContentResolver(), Settings.System.WIFI_SLEEP_POLICY, Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
        final SharedPreferences prefs = context.getSharedPreferences("wifi_sleep_policy", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("WIFI_SLEEP_POLICY_DEFAULT", value);
        editor.commit();
    }

    public NetworkInfo checkNetWorkConnection(Context context){
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiNetworkInfo;
    }
}
