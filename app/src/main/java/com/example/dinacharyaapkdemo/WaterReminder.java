package com.example.dinacharyaapkdemo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class WaterReminder extends AppCompatActivity {
    private static final int NOTIFICATION_ID = 1;
    private static final long REMINDER_INTERVAL =  120000; // 1.3 hours in milliseconds

    private Button remindMe;
    private Button stopNotification;

    private Button stopService;
    private Handler handler;
    private Runnable reminderRunnable;
    private View fillingRectangle;
    private double fillIncrement = 16.23;

    private NotificationManagerCompat notificationManager;
    private MediaPlayer mediaPlayer;
    public boolean isReminderActive = false;
    private static final String PREF_NAME = "ReminderPrefs";
    private static final String PREF_REMINDERS_ENABLED = "reminders_enabled";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_reminder);
        remindMe = findViewById(R.id.remind_me);
        stopNotification = findViewById(R.id.stopRemider);
        stopNotification.setVisibility(View.GONE);
        fillingRectangle = findViewById(R.id.fillingRectangle);


        // Create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "ReminderChannel",
                    "Reminder Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Initialize notification manager
        notificationManager = NotificationManagerCompat.from(this);

        handler = new Handler();



        boolean fromNotification = getIntent().getBooleanExtra("from_notification", false);
        if (fromNotification) {
            stopNotification.setVisibility(View.VISIBLE);
            remindMe.setVisibility(View.GONE);
        } else {
            // Set click listener for the "Remind Me" button
            remindMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    remindMe.setVisibility(View.GONE);
                    stopNotification.setVisibility(View.GONE);
                    startReminders();
                }
            });

            // Set click listener for the "Stop Notification" button
            stopNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentNotificationId != -1) {
                        stopNotification(currentNotificationId);
                        updateFilling();
                        currentNotificationId = -1;
                        stopNotification.setVisibility(View.GONE);
                    }
                }
            });

            stopService = findViewById(R.id.stopService);

            stopService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stopReminders();
                    stopService.setVisibility(View.GONE);
                    remindMe.setVisibility(View.VISIBLE);
                }
            });
        }


        // Other code remains unchanged

        // Restore reminder status from shared preferences
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean remindersEnabled = preferences.getBoolean(PREF_REMINDERS_ENABLED, false);

        if (remindersEnabled) {
            startReminders();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_water);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (item.getItemId() == R.id.nav_articles) {
                    startActivity(new Intent(getApplicationContext(), Articles.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (item.getItemId() == R.id.nav_meditation) {
                    startActivity(new Intent(getApplicationContext(), Meditation.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (item.getItemId() == R.id.nav_water) {
                    return true;
                }
                return false;
            }
        });

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // Handle item clicks here
                int itemId = menuItem.getItemId();
                if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(WaterReminder.this, UserProfile.class));
                } else if (itemId == R.id.nav_about) {
                    startActivity(new Intent(WaterReminder.this, About.class));
                } else if (itemId == R.id.nav_share) {
                    startActivity(new Intent(WaterReminder.this, Share.class));
                } else if (itemId == R.id.nav_logout) {
                    FirebaseAuth.getInstance().signOut();

                    // Again open login page
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();
                }

                // Close the drawer after handling the click
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    // Method to update the reminders enabled status in SharedPreferences
    private void updateRemindersEnabled(boolean isEnabled) {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        preferences.edit().putBoolean(PREF_REMINDERS_ENABLED, isEnabled).apply();
    }

    // Method to get the reminders enabled status from SharedPreferences
    private boolean getRemindersEnabled() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return preferences.getBoolean(PREF_REMINDERS_ENABLED, false); // Default value is false
    }

    private void updateFilling() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fillingRectangle.getLayoutParams();
        int currentFilling = layoutParams.width;
        int newFilling = (int) Math.min(currentFilling + dpToPx(fillIncrement), dpToPx(200));

        layoutParams.width = newFilling;
        fillingRectangle.setLayoutParams(layoutParams);
    }

    private int dpToPx(double dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private void startReminders() {
        // Save reminder status to SharedPreferences
        updateRemindersEnabled(true);


        // Start the foreground service
        Intent serviceIntent = new Intent(getApplicationContext(), WaterReminderService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        // Start reminders
        if (reminderRunnable == null) {
            reminderRunnable = new Runnable() {
                @Override
                public void run() {
                    // Get the current hour of the day
                    Calendar calendar = Calendar.getInstance();
                    int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

                    // Check if the current hour is within the time frame (8 am to 10 pm)
                    if (currentHour >= 6 && currentHour < 22) {
                        // Implement your reminder logic here (e.g., show a notification)
                        // You can use NotificationManager to show a notification
                        showReminderNotification();

                        isReminderActive = true;
                    }

                    // Post the runnable again after REMINDER_INTERVAL
                    handler.postDelayed(this, REMINDER_INTERVAL);
                }
            };

            handler.postDelayed(reminderRunnable, REMINDER_INTERVAL);
        }
    }

    public void stopReminders() {
        // Save reminder status to SharedPreferences
        updateRemindersEnabled(false);

        // Stop reminders
        handler.removeCallbacks(reminderRunnable);

        // Stop the foreground service
        Intent serviceIntent = new Intent(this, WaterReminderService.class);
        stopService(serviceIntent);

        isReminderActive = false;
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
        stopNotification.setVisibility(View.VISIBLE);
        remindMe.setVisibility(View.GONE);
    }
    private void stopNotification(int notificationId) {
        // Cancel the specific notification using its ID
        notificationManager.cancel(notificationId);
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        // Save any relevant data or state here
//        outState.putBoolean("isReminderActive", isReminderActive);
//        outState.putInt("currentNotificationId", currentNotificationId);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//
//        // Restore the saved data and update the activity's state
//        isReminderActive = savedInstanceState.getBoolean("isReminderActive");
//        currentNotificationId = savedInstanceState.getInt("currentNotificationId");
//
//        // Update your UI or perform any necessary actions based on the restored data
//        if (isReminderActive) {
//            // Handle the case where the reminder was active
//            startReminders(); // Restart reminders if they were active
//            // You can also update your UI here as needed
//            stopNotification.setVisibility(View.VISIBLE);
//            remindMe.setVisibility(View.GONE);
//        } else {
//
//            Log.d("WaterReminder", "Reminder was not active when restored.");
//        }
//    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

}
