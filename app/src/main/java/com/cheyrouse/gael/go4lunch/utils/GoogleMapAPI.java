package com.cheyrouse.gael.go4lunch.utils;

import com.cheyrouse.gael.go4lunch.models.Prediction;
import com.cheyrouse.gael.go4lunch.models.Predictions;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleMapAPI {

    @GET("place/autocomplete/json?strictbounds&types=establishment")
    public Call<Predictions> getPlacesAutoComplete(
            @Query("input") String input,
            @Query("location") String location,
            @Query("radius") int radius,
            @Query("key") String key
    );

}