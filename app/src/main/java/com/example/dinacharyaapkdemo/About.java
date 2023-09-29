package com.example.dinacharyaapkdemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class About extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private FirebaseAuth auth;
    TextView emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Initialize views
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        auth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.emailTextView);

        emailText.setOnClickListener(v -> {
            String emailAddress = "akashpatil5928@gmail.com"; // Replace with your desired email address
            composeEmail(emailAddress);
        });

        // Set listeners for bottom navigation and side navigation
        setupBottomNavigation();
        setupSideNavigation();
    }

    private void composeEmail(String emailAddress) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + emailAddress)); // Specify the email address
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject"); // Specify the email subject (optional)
        intent.putExtra(Intent.EXTRA_TEXT, "Hello,"); // Specify the email body (optional)

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(About.this, MainActivity.class));
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(About.this, Meditation.class));
                    return true;
                } else if (itemId == R.id.nav_water) {
                    startActivity(new Intent(About.this, WaterReminder.class));
                    return true;
                } else if (itemId==R.id.nav_articles) {
                    startActivity(new Intent(About.this, Articles.class));
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
                    return true;
                } else if (itemId == R.id.nav_share) {
                    startActivity(new Intent(About.this, Share.class));
                } else if (itemId==R.id.nav_profile) {
                    startActivity(new Intent(About.this, UserProfile.class));

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