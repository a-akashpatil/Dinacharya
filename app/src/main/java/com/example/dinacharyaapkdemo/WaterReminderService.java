package com.example.dinacharyaapkdemo;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class WaterReminderService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "ReminderChannel";

    private NotificationManagerCompat notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = NotificationManagerCompat.from(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Implement your reminder logic here
        showReminderNotification();

        // Return START_STICKY to indicate that the service should be restarted if killed
        return START_STICKY;
    }

    private int currentNotificationId = -1;

    private void showReminderNotification() {
        Intent intent = new Intent(getApplicationContext(), WaterReminder.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // Ensure only one instance of the activity is created
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "ReminderChannel")
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setContentTitle("Water Reminder")
                .setContentText("Remember to drink water!")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        intent.putExtra("from_notification", true);


        // Get a unique notification ID
        int notificationId = (int) System.currentTimeMillis();

        // Store the notification ID in the currentNotificationId field
        currentNotificationId = notificationId;


        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Request permissions if needed
            return;
        }
        notificationManager.notify(notificationId, builder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
