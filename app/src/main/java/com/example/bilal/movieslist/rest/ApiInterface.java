package com.example.bilal.movieslist.rest;



import com.google.gson.JsonObject;
import com.example.bilal.movieslist.Models.ResponseData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiInterface {

    @GET("/3/discover/movie?")
    Call<ResponseData> getMoviesData(@Query("primary_release_year") String year,
                                       @Query("sort_by") String sortBy,
                                       @Query("api_key") String apiKey,
                                       @Query("page") int page);
}
