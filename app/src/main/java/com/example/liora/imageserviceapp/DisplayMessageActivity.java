package com.example.liora.imageserviceapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        String msg = intent.getStringExtra("message");

        TextView textView = (TextView)findViewById(R.id.service_on);
        textView.setText(msg);
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
            Socket socket = new Socket(servAddr, 1234);
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

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
        return stream.toByteArray();
    }
}

