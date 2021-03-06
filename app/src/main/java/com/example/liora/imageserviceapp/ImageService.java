package com.example.liora.imageserviceapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.UnicodeSetSpanner;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Defining an ImageServicec to transfer the phone images to computer when wifi event occurs.
 */
public class ImageService extends Service implements Observer {
    //private BroadcastReceiver receiver;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private final int notificationID = 100;
    private int numImages;
    private int progress;

    public ImageService() { }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startWifiBroadcaster();
        Toast.makeText(this, "The service is starting...", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "The service is ending...", Toast.LENGTH_SHORT).show();
    }

    /**
     * The method initializes the notification manager, and creates a broadcast receiver to listen
     * to wifi events, and send the images to the connection.
     */
    public void startWifiBroadcaster() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //notificationManager = NotificationManagerCompat.from(this);
        builder = new NotificationCompat.Builder(this, "default");
        progress = 0;
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.supplicant.CONNECTION_CHANGE");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
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
        this.registerReceiver(receiver, intentFilter);
    }

    /**
     * The method is invoked every time an image is transferred, to update the notification
     * progress bar.
     * @param o The observable.
     * @param arg The args- we won't be receiving any.
     */
    @Override
    public void update(Observable o, Object arg)
    {
        progress++;
        String contextMsg;
        if (progress == numImages) contextMsg = "Image transfer completed";
        else contextMsg = progress + " images out of "+ numImages + " images transferred";

        builder.setProgress(numImages, progress, false).setContentText(contextMsg);

        //Send the notification:
        Notification notification = builder.build();
        notificationManager.notify(notificationID, notification);
    }

    /**
     * The method geets all images from phone, and sends them to the connection.
     */
    public void startTransfer() {
        //Update notification information:
        ArrayList<File> imagesList = new ArrayList<>();
        getImages(imagesList, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
        File[] tempImages = new File[imagesList.size()];
        tempImages = imagesList.toArray(tempImages);
        final File[] images = tempImages;
        numImages = images.length;
                //Integer notificationID = 1;
        builder.setOngoing(true).setContentTitle("Image Transfer")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText("Image transfer starting...")
                .setProgress(numImages, progress, false);
        //Send the notification:
        Notification notification = builder.build();
        notificationManager.notify(notificationID, notification);
        final ConnectionChannel connection = new ConnectionChannel();
        connection.addObserver(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                connection.connect(images);
            }
        }).start();

    }

    /**
     * The method recursively gets all images on phone.
     * @param images The list of images.
     * @param dir The current directory which in it images are searched for.
     */
    private void getImages(List<File> images, File dir) {
        File[] listFile = dir.listFiles();
        if (listFile == null || listFile.length == 0) return;
        for (File file : listFile) {
            boolean isDir = file.isDirectory();
            if (isDir) { getImages(images, file); }
            else { images.add(file); }
        }
    }
}
