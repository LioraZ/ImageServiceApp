package com.example.liora.imageserviceapp;

import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ConnectionChannel {
    private Socket socket;

    public ConnectionChannel() {
    }

    public void connect() {
        try {
            this.socket = new Socket("10.0.2.2", 12345);
            new Thread(()->sendImages()).run();
           // System.out.println("Connected to Server:\n");
        } catch (Exception e) { Log.getStackTraceString(e); }
    }

    private void sendImages() {

    }

    public void GetPhotos(){
        File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (dcim == null) return;
        File[] pics = dcim.listFiles();
        int count = 0;
        if (pics != null) {
            for (File pic : pics){ connectTCP(pic); }
        }
    }

    public void showProgressBar(){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "default");
        builder.setContentTitle("Picture Tranfer").setContentText("Transfer in progress").
                setPriority(NotificationCompat.PRIORITY_LOW);
        builder.setContentText("Half way through").setProgress(100, 50, false);
        notificationManager.notify(1, builder.build());
        builder.setContentText("Download complete").setProgress(0, 0, false);
        notificationManager.notify(1, builder.build());
    }

    public void connectTCP(File pic) {
        try {
            InetAddress servAddr = InetAddress.getByName("10.0.0.2");
            Socket socket = new Socket(servAddr, 12345);
            try {
                OutputStream output = socket.getOutputStream();
                FileInputStream fis = new FileInputStream(pic);
                Bitmap bm = BitmapFactory.decodeStream(fis);
                byte[] imgbyte = getBytesFromBitmap(bm);
                output.write(imgbyte);
                output.flush();
            } catch (IOException e) { Log.e("TCP", "S: Error", e); }
        } catch (Exception e) { Log.e("TCP", "C: Error", e); }

    }

    private void sendImage(File pic) {
        try {
            OutputStream output = socket.getOutputStream();
            FileInputStream fis = new FileInputStream(pic);
            Bitmap bm = BitmapFactory.decodeStream(fis);
            byte[] imgbyte = getBytesFromBitmap(bm);
            output.write(imgbyte);
            output.flush();
        } catch (IOException e) { Log.e("TCP", "S: Error", e); }
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
        return stream.toByteArray();
    }
}
