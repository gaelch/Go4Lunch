package com.cheyrouse.gael.go4lunch.services;

import com.cheyrouse.gael.go4lunch.BuildConfig;
import com.cheyrouse.gael.go4lunch.models.Place;
import com.cheyrouse.gael.go4lunch.models.PlaceDetails;
import com.cheyrouse.gael.go4lunch.models.Predictions;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.cheyrouse.gael.go4lunch.utils.Constants.API_KEY;


public interface Go4LunchService {

    //Requests HTTP
    @GET("nearbysearch/json?radius=5500&type=restaurant&key="+ API_KEY)
    Observable<Place> getMapPlace(@Query(value = "location", encoded = true) String location);

    @GET("details/json?key="+ API_KEY)
    Observable<PlaceDetails> getMapPlaceDetails(@Query(value = "placeid", encoded = true) String placeId);

    @GET("autocomplete/json?strictbounds&types=establishment")
    Observable<Predictions> getPlacesAutoComplete(
            @Query("input") String input,
            @Query(value = "location", encoded = true) String location,
            @Query("radius") int radius,
            @Query("key") String key
    );

    //Request with RetroFit, RxJava and OkHttp
    ThreadLocal<Retrofit> retrofit = new ThreadLocal<Retrofit>() {
        @Override
        protected Retrofit initialValue() {
            return new Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/maps/api/place/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
    };

    //  Http Client
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build();
}
