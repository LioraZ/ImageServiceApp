package com.example.liora.imageserviceapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.UnicodeSetSpanner;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class ImageService extends Service {
    private BroadcastReceiver receiver;
    public ImageService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate(){
        super.onCreate();
        //code of service
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        startWifiBroadcaster();
        Toast.makeText(this, "The service is starting...", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "The service is ending...", Toast.LENGTH_SHORT).show();

    }

    public void startWifiBroadcaster() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.supplicant.CONNECTION_CHANGE");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");

        this.receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                NetworkInfo networkInfo = intent.getParcelableExtra(wifiManager.EXTRA_NETWORK_INFO);
                if (networkInfo != null) {
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        // get the different network states
                        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                            startTransfer();
                        }
                    }
                }
            }
        };
        this.registerReceiver(this.receiver, intentFilter);
    }

    public void startTransfer() {
        ConnectionChannel connection = new ConnectionChannel();
        connection.connect();
    }
}
