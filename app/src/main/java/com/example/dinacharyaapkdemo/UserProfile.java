package com.example.dinacharyaapkdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class UserProfile extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private  FirebaseAuth auth;
    private FirebaseUser mUser;
    private TextView user_email, user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize views
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();
        user_email = findViewById(R.id.user_email);
        user_name = findViewById(R.id.user_name);


                if (mUser != null) {
                    String userEmail = mUser.getEmail();
                    user_email.setText(userEmail);


                }


        // Set listeners for bottom navigation and side navigation
        setupBottomNavigation();
        setupSideNavigation();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(UserProfile.this, MainActivity.class));
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(UserProfile.this, Meditation.class));
                    return true;
                } else if (itemId == R.id.nav_water) {
                    startActivity(new Intent(UserProfile.this, WaterReminder.class));
                    return true;
                } else if (itemId==R.id.nav_articles) {
                    startActivity(new Intent(UserProfile.this, Articles.class));
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
                    startActivity(new Intent(UserProfile.this, About.class));
                } else if (itemId == R.id.nav_share) {
                    startActivity(new Intent(UserProfile.this, Share.class));
                } else if (itemId==R.id.nav_profile) {
                    return  true;

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