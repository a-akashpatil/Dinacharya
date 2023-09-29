package com.example.dinacharyaapkdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Share extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private  FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        // Initialize views
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        auth = FirebaseAuth.getInstance();

        // Set listeners for bottom navigation and side navigation
        setupBottomNavigation();
        setupSideNavigation();


        Button shareButton = findViewById(R.id.btn_share);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareApp();
            }
        });
    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this awesome app: [Your App Name] [Google Play Store Link]");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share via"));
    }
    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(Share.this, MainActivity.class));
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(Share.this, Meditation.class));
                    return true;
                } else if (itemId == R.id.nav_water) {
                    startActivity(new Intent(Share.this, WaterReminder.class));
                    return true;
                } else if (itemId==R.id.nav_articles) {
                    startActivity(new Intent(Share.this, Articles.class));
                    return true;
                }
                return false;
            }
        });
    }

    private void setupSideNavigation() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.nav_about) {
                    startActivity(new Intent(Share.this, About.class));
                } else if (itemId == R.id.nav_share) {
                    return true;
                } else if (itemId==R.id.nav_profile) {
                    startActivity(new Intent(Share.this, UserProfile.class));

                } else if (itemId==R.id.nav_logout) {
                    FirebaseAuth.getInstance().signOut();

                    // Again open login page
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }
}
