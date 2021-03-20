package com.example.newtab;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GitHubService {

    @GET("restService/personOne.json")
    Call<JsonModel> listRepos();

    @GET("restService/login.json")
    Call<UserLoginModel> login();

    @GET("restService/images.json")
    Call<PhotoModelService> photos();

    @GET("restService/videos.json")
    Call<VideoModelService> videos();
}