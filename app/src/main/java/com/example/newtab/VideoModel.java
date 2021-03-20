package com.example.newtab;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class VideoModel extends RealmObject {

    @SerializedName("id")
    private int videoId;

    @SerializedName("url")
    private String videoUrl;


    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

}
