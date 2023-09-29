package com.example.dinacharyaapkdemo;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dinacharyaapkdemo.Adapter.MeditationAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class Meditation extends AppCompatActivity {

    ImageView stopM, pauseM, playM;

    private MediaPlayer mediaPlayer;
    private MeditationViewModel viewModel;
    Meditation.AudioPlayer audioPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditation);





        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(MeditationViewModel.class);
        audioPlayer = new AudioPlayer();

        // Restore state from the saved Bundle
        if (savedInstanceState != null) {
            boolean isPlaying = savedInstanceState.getBoolean("isPlaying", true);
            int audioResourceId = savedInstanceState.getInt("audioResourceId", 0);

            if (isPlaying && audioResourceId != 0) {
                // If MediaPlayer was playing, resume playback
                audioPlayer.play(this, audioResourceId);
            }
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        List<MusicCategory> musicCategories = new ArrayList<>();
        musicCategories.add(new MusicCategory("Meditation Music", R.raw.meditation, R.drawable.meditate));
        musicCategories.add(new MusicCategory("Study Music", R.raw.focus, R.drawable.study));
        musicCategories.add(new MusicCategory("Gym Music", R.raw.newsong, R.drawable.gym));
        musicCategories.add(new MusicCategory("Sleep Music", R.raw.sleep, R.drawable.sleep));
        // Add more categories as needed

        MeditationAdapter adapter = new MeditationAdapter(musicCategories);
        recyclerView.setAdapter(adapter);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_meditation);
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
                    return true;
                } else if (item.getItemId() == R.id.nav_water) {
                    startActivity(new Intent(getApplicationContext(), WaterReminder.class));
                    overridePendingTransition(0, 0);
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
                int itemId = menuItem.getItemId();
                if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(Meditation.this, UserProfile.class));
                } else if (itemId == R.id.nav_about) {
                    startActivity(new Intent(Meditation.this, About.class));
                } else if (itemId == R.id.nav_share) {
                    startActivity(new Intent(Meditation.this, Share.class));
                } else if (itemId == R.id.nav_logout) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume MediaPlayer playback if it was playing when the activity was paused
        if (viewModel.isPlaying() && mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause MediaPlayer and save its state in the ViewModel
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            viewModel.setPlaying(true);
            // Save the audio resource ID in ViewModel if needed
            // viewModel.setAudioResourceId(R.raw.some_audio);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release MediaPlayer when the activity is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);



        // Save the MediaPlayer state
        if (audioPlayer != null) {
            outState.putBoolean("isPlaying", audioPlayer.isPlaying());
            outState.putInt("audioResourceId", viewModel.getAudioResourceId());

        }



    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the MediaPlayer state if it exists in the saved Bundle
        if (savedInstanceState != null) {
            boolean isPlaying = savedInstanceState.getBoolean("isPlaying", false);
            int audioResourceId = savedInstanceState.getInt("audioResourceId", 0);


            if (isPlaying && audioResourceId != 0) {
                // If MediaPlayer was playing, resume playback
                audioPlayer.play(this, audioResourceId);
            }
        }
    }

    public static class MeditationViewModel extends ViewModel {
        private boolean isPlaying = false;
        private int audioResourceId = 0;

        public boolean isPlaying() {
            return isPlaying;
        }

        public void setPlaying(boolean playing) {
            isPlaying = playing;
        }

        public int getAudioResourceId() {
            return audioResourceId;
        }

        public void setAudioResourceId(int resourceId) {
            audioResourceId = resourceId;
        }
    }

    public static class MusicCategory {
        private String title;
        private int imageResource;
        private int audioResourceId;
        private boolean isPlayVisible;
        private boolean isPauseVisible;

//        public boolean isPlayVisible() {
//            return isPlayVisible;
//        }
//
//        public void setPlayVisible(boolean playVisible) {
//            isPlayVisible = playVisible;
//        }
//
//        public boolean isPauseVisible() {
//            return isPauseVisible;
//        }
//
//        public void setPauseVisible(boolean pauseVisible) {
//            isPauseVisible = pauseVisible;
//        }
//
//        public boolean isStopVisible() {
//            return isStopVisible;
//        }
//
//        public void setStopVisible(boolean stopVisible) {
//            isStopVisible = stopVisible;
//        }

        private boolean isStopVisible;


        public MusicCategory(String title, int audioResourceId, int imageResource) {
            this.title = title;
            this.audioResourceId = audioResourceId;
            this.imageResource = imageResource;
            this.imageResource = imageResource;
            this.isPlayVisible = true;  // Initialize play button as visible
            this.isPauseVisible = false; // Initialize pause button as invisible
            this.isStopVisible = false;  // Initialize stop button as invisible
        }

        public String getTitle() {
            return title;
        }

        public int getImageResource() {
            return imageResource;
        }

        public int getAudioResourceId() {
            return audioResourceId;
        }
    }
        public static class AudioPlayer {
            private MediaPlayer mediaPlayer;
            private int resumePosition = 0;

            private boolean isPlaying = false;

            public boolean isPlaying() {
                return isPlaying;
            }

            public void play(Context context, int audioResourceId) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }

                mediaPlayer = MediaPlayer.create(context, audioResourceId);
                mediaPlayer.setOnCompletionListener(mp -> {
                    mediaPlayer.release();
                    mediaPlayer = null;
                    isPlaying = false; // Audio playback is completed
                });
                mediaPlayer.start();
                isPlaying = true; // Audio is now playing
            }

            public void pause() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    isPlaying = false; // Audio is paused
                }
            }

            public void stop() {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                    resumePosition = 0; // Reset position when the audio is stopped
                    isPlaying = false;
                }
            }

            public void resume() {
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(resumePosition); // Set the playback position
                    mediaPlayer.start();
                    isPlaying = true; // Audio is resumed
                }
            }

            public int getDuration() {
                if (mediaPlayer != null) {
                    return mediaPlayer.getDuration();
                }
                return 0;
            }

            public int getCurrentPosition() {
                if (mediaPlayer != null) {
                    return mediaPlayer.getCurrentPosition();
                }
                return 0;
            }
        }

    }


