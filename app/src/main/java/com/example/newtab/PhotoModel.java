package com.example.newtab;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class PhotoModel extends RealmObject {


    @SerializedName("id")
    private int photoId;

    @SerializedName("url")
    private String photoUrl;

    int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

}
