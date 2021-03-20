package com.example.newtab;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PhotoModelService {

    @SerializedName("images")
    private List<PhotoModel> images;

    List<PhotoModel> getImages() {
        return images;
    }

    public void setImages(List<PhotoModel> images) {
        this.images = images;
    }
}
