package com.example.bookapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class Notification {
    Context _context;
    public Notification(Context _context, String msg){
        this._context = _context;
        sendNotification(msg);
    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    void makeNotificationChannel(String id, String name, int importance)
    {
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setShowBadge(true);

        NotificationManager notificationManager =
                (NotificationManager)_context.getSystemService(_context.NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }

    public void sendNotification(String msg) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannel("CHANNEL_1", "App Channel", NotificationManager.IMPORTANCE_DEFAULT);
        }

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(_context, "CHANNEL_1");

        notification.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

        notification
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Book World")
                .setContentText(msg)
                .setNumber(3);

        NotificationManager notificationManager =  (NotificationManager)_context.getSystemService(_context.NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.notify(1, notification.build());
    }


}