package com.example.dinacharyaapkdemo.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dinacharyaapkdemo.Meditation;
import com.example.dinacharyaapkdemo.R;

import java.util.List;

public class MeditationAdapter extends RecyclerView.Adapter<MeditationAdapter.ViewHolder> {
    private static final String MEDIA_PLAYER_STATE_KEY = "mediaPlayerState";
    private static final String SHARED_PREFS_KEY = "MyPrefs"; // Change to your preference key
    private static final String PLAY_BUTTON_STATE_KEY = "playButtonState";
    private static final String PAUSE_BUTTON_STATE_KEY = "pauseButtonState";
    private static final String STOP_BUTTON_STATE_KEY = "stopButtonState";

    private static boolean isPlayButtonVisible;
    private static boolean isPauseButtonVisible;
    private static boolean isStopButtonVisible;

    private Context context;


    private MediaPlayer mediaPlayer;

    public static List<Meditation.MusicCategory> musicCategories;

    public MeditationAdapter(List<Meditation.MusicCategory> musicCategories) {
        this.musicCategories = musicCategories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_each_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Meditation.MusicCategory category = musicCategories.get(position);
        holder.songTitle.setText(category.getTitle());
        holder.songImage.setImageResource(category.getImageResource());



        int placeholderImage = R.drawable.baseline_music_note_24;

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(placeholderImage)
                .error(R.drawable.baseline_music_note_24);

        Glide.with(holder.songImage.getContext())
                .setDefaultRequestOptions(requestOptions)
                .load(category.getImageResource())
                .into(holder.songImage);



    }


    @Override
    public int getItemCount() {
        return musicCategories.size();
    }

    // Implement onSaveInstanceState to save the MediaPlayer state
    public void onSaveInstanceState(Bundle outState) {

        // Save button states to SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PLAY_BUTTON_STATE_KEY, isPlayButtonVisible);
        editor.putBoolean(PAUSE_BUTTON_STATE_KEY, isPauseButtonVisible);
        editor.putBoolean(STOP_BUTTON_STATE_KEY, isStopButtonVisible);
        editor.apply();
    }

    // Implement onRestoreInstanceState to restore the MediaPlayer state and button states
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // ...

        // Restore button states from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        isPlayButtonVisible = sharedPreferences.getBoolean(PLAY_BUTTON_STATE_KEY, true); // Default is true
        isPauseButtonVisible = sharedPreferences.getBoolean(PAUSE_BUTTON_STATE_KEY, false); // Default is false
        isStopButtonVisible = sharedPreferences.getBoolean(STOP_BUTTON_STATE_KEY, false); // Default is false
    }




    private void playAudio(Context context, int audioResourceId) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(context, audioResourceId);
        mediaPlayer.setOnCompletionListener(mp -> {
            mediaPlayer.release();
            mediaPlayer = null;
        });
        mediaPlayer.start();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView songTitle;
        ImageView songImage;
        ImageView playMusic, pauseMusic, stopMusic;
        Meditation.AudioPlayer audioPlayer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.songTitle);
            songImage = itemView.findViewById(R.id.songImage);
            songTitle = itemView.findViewById(R.id.songTitle);
            songImage = itemView.findViewById(R.id.songImage);
            playMusic = itemView.findViewById(R.id.play);
            pauseMusic = itemView.findViewById(R.id.pause);
            stopMusic = itemView.findViewById(R.id.stop);
            pauseMusic.setVisibility(View.GONE);
            stopMusic.setVisibility(View.GONE);

            audioPlayer = new Meditation.AudioPlayer();
            setupAudioPlayer();
        }
            private void setupAudioPlayer() {
                playMusic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int audioResourceId = musicCategories.get(getAdapterPosition()).getAudioResourceId();
                        audioPlayer.play(itemView.getContext(), audioResourceId);

                        isPlayButtonVisible = false;
                        isPauseButtonVisible = true;
                        isStopButtonVisible = true;

                        playMusic.setVisibility(View.GONE);
                        pauseMusic.setVisibility(View.VISIBLE);
                        stopMusic.setVisibility(View.VISIBLE);

                        // Save button states to SharedPreferences
                        SharedPreferences sharedPreferences = itemView.getContext().getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(PLAY_BUTTON_STATE_KEY, isPlayButtonVisible);
                        editor.putBoolean(PAUSE_BUTTON_STATE_KEY, isPauseButtonVisible);
                        editor.putBoolean(STOP_BUTTON_STATE_KEY, isStopButtonVisible);
                        editor.apply();
                    }
                });

            pauseMusic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        audioPlayer.pause();


                        // Update button states
                        isPlayButtonVisible = true;
                        isPauseButtonVisible = false;

                        pauseMusic.setVisibility(View.GONE);
                        playMusic.setVisibility(View.VISIBLE);

                        // Save button states to SharedPreferences
                        SharedPreferences sharedPreferences = itemView.getContext().getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(PLAY_BUTTON_STATE_KEY, isPlayButtonVisible);
                        editor.putBoolean(PAUSE_BUTTON_STATE_KEY, isPauseButtonVisible);
                        editor.apply();
                    }
                });

                stopMusic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        audioPlayer.stop();


                        // Update button states
                        isPlayButtonVisible = true;
                        isPauseButtonVisible = false;
                        isStopButtonVisible = false;

                        playMusic.setVisibility(View.VISIBLE);
                        pauseMusic.setVisibility(View.GONE);
                        stopMusic.setVisibility(View.GONE);

                        // Save button states to SharedPreferences
                        SharedPreferences sharedPreferences = itemView.getContext().getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(PLAY_BUTTON_STATE_KEY, isPlayButtonVisible);
                        editor.putBoolean(PAUSE_BUTTON_STATE_KEY, isPauseButtonVisible);
                        editor.putBoolean(STOP_BUTTON_STATE_KEY, isStopButtonVisible);
                        editor.apply();
                    }
                });



            }


        }



}

