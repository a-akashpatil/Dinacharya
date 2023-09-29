package com.example.dinacharyaapkdemo;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dinacharyaapkdemo.Adapter.ArticleAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Articles extends AppCompatActivity {
    Button logout;
    TextView textView;
    FirebaseAuth auth;
    FirebaseUser mUser;

    private LinearLayout articleLayout;

    List<ArticleModel> articleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);

//        logout = findViewById(R.id.btn_logout);
        auth = FirebaseAuth.getInstance();
//        textView = findViewById(R.id.user_details);
        mUser = FirebaseAuth.getInstance().getCurrentUser();



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_articles);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (item.getItemId() == R.id.nav_articles) {
                    return true;
                } else if (item.getItemId() == R.id.nav_meditation) {
                    startActivity(new Intent(getApplicationContext(), Meditation.class));
                    overridePendingTransition(0, 0);
                }else if(item.getItemId() == R.id.nav_water){
                    startActivity(new Intent(getApplicationContext(), WaterReminder.class));
                    overridePendingTransition(0, 0);
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
                if(itemId==R.id.nav_profile){
                    startActivity(new Intent(Articles.this, UserProfile.class));

                } else if (itemId==R.id.nav_about) {
                    startActivity(new Intent(Articles.this, About.class));

                } else if (itemId == R.id.nav_share) {
                    startActivity(new Intent(Articles.this, Share.class));

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

        RecyclerView recyclerView = findViewById(R.id.articleRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        articleList = new ArrayList<>(); // Initialize the articleList

        ArticleAdapter adapter = new ArticleAdapter(articleList);
        recyclerView.setAdapter(adapter);

        DatabaseReference articlesRef = FirebaseDatabase.getInstance().getReference("articles");
        articlesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                articleList.clear(); // Clear the list before adding new data

                for (DataSnapshot articleSnapshot : dataSnapshot.getChildren()) {
                    String title = articleSnapshot.child("title").getValue(String.class);
                    String content = articleSnapshot.child("content").getValue(String.class);
                    String imageUrl = articleSnapshot.child("img").getValue(String.class);
                    String url = articleSnapshot.child("url").getValue(String.class);

                    ArticleModel article = new ArticleModel(title, content, imageUrl, url);
                    articleList.add(article);
                }

                adapter.notifyDataSetChanged(); // Notify the adapter that data has changed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error fetching data: " + error.getMessage());
            }
        });
    }

    public static class ArticleModel {
        private String title;
        private String content;
        private String imageUrl;
        private String url;

        public ArticleModel(String title, String content, String imageUrl, String url) {
            this.title = title;
            this.content = content;
            this.imageUrl = imageUrl;
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getUrl() {
            return url;
        }
    }
}
