package com.example.dinacharyaapkdemo;

import androidx.lifecycle.ViewModel;

public class MeditationViewModel extends ViewModel {
    private boolean isPlaying = false;
    private int audioResourceId = 0;
    // Add other state variables as needed

    // Getters and setters for state variables
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

