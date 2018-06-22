package com.example.liora.imageserviceapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSION = 101;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        context = this;
        if (ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        int storagePermission = ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE);
        if (storagePermission == PackageManager.PERMISSION_GRANTED) {
            finish();
            startActivity(getIntent());
        } else {
            finish();
        }
    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannelId();
    }*/

    private void createNotificationChannelId() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "ImageBackup",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Alert backing up photos");
            mNotificationManager.createNotificationChannel(channel);
        }
    }

    public void startService(View view) {
        Intent intent = new Intent(this, ImageService.class);
        startService(intent);
    }

    public void stopService(View view) {
        Intent intent = new Intent(this, ImageService.class);
        stopService(intent);
    }
}
