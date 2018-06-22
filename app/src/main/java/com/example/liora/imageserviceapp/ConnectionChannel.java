package com.example.liora.imageserviceapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
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
import java.nio.ByteBuffer;
import java.util.Observable;
import java.util.Observer;

/**
 * Defining a connection class to transfer images to computer via tcp connection.
 */
public class ConnectionChannel extends Observable {
    //private Socket socket;

    public ConnectionChannel() { }

    /**
     * The method connects to socket, and sends the images to computer.
     * @param images The list of images.
     */
    public void connect(File[] images) {
        if (images == null) return;
        try {
            InetAddress serverAddr = InetAddress.getByName("10.0.2.2");
            Socket socket = new Socket(serverAddr, 7234);
            try {
                System.out.println("Connected to Server:\n");
                final OutputStream os = socket.getOutputStream();
                for (File image : images) {
                    FileInputStream fis = new FileInputStream(image);
                    Bitmap bm = BitmapFactory.decodeStream(fis);
                    byte[] imageName = image.getName().getBytes();
                    os.write(ByteBuffer.allocate(4).putInt(imageName.length).array());
                    os.write(imageName);
                    byte[] imgByte = getBytesFromBitmap(bm);
                    os.write(ByteBuffer.allocate(4).putInt(imgByte.length).array());
                    os.write(imgByte);
                    os.flush();
                    notifyObservers();
                }
            } catch (Exception e) {
                Log.getStackTraceString(e);
            } finally {
                System.out.println("finished sending photo batch\n");
                socket.close();
            }
        } catch (Exception e) {
            Log.getStackTraceString(e);
        }
    }

    /**
     * The method notifies all observers to connection that an image was transferred.
     */
    @Override
    public void notifyObservers() {
        setChanged();
        super.notifyObservers();
    }

    /**
     * The method converts an image to a bytes array.
     * @param bitmap The image.
     * @return The bytes array.
     */
    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
        return stream.toByteArray();
    }
}