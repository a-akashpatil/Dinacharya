package com.example.dinacharyaapkdemo.Model;

public class SongsModel {
    private String name;
    private int resourceId;

    public SongsModel(String name, int resourceId) {
        this.name = name;
        this.resourceId = resourceId;
    }

    public String getName() {
        return name;
    }

    public int getResourceId() {
        return resourceId;
    }
}
