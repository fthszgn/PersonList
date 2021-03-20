package com.example.newtab;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class PersonModel extends RealmObject {

    @SerializedName("id")
    private int id;
    @SerializedName("fragmentId")
    private int fragmentId;
    @SerializedName("imageId")
    private int imageId;
    @SerializedName("videoId")
    private int videoId;
    @SerializedName("name")
    private String name;
    @SerializedName("company")
    private String company;

    private boolean isSelected = false;


    private PersonModel(Integer imageId, String name, String company) {
        this.imageId = imageId;
        this.name = name;
        this.company = company;
    }

    PersonModel(int imageId, int fragmentId, String name, String company) {
        this.imageId = imageId;
        this.name = name;
        this.company = company;
        this.fragmentId = fragmentId;
    }

    public PersonModel() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String getCompany() {
        return company;
    }

    boolean isSelected() {
        return isSelected;
    }

    void setSelected(boolean selected) {
        isSelected = selected;
    }

    static PersonModel getNullModel() {
        return new PersonModel(5, "", "");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFragmentId() {
        return fragmentId;
    }

    public void setFragmentId(int fragmentId) {
        this.fragmentId = fragmentId;
    }

    int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
