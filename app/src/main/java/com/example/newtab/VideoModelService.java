package com.example.newtab;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoModelService {


    @SerializedName("videos")
    private List<VideoModel> videos;

    List<VideoModel> getVideos() {
        return videos;
    }

    public void setVideos(List<VideoModel> videos) {
        this.videos = videos;
    }
}
