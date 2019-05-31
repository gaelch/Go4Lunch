package com.cheyrouse.gael.go4lunch.Utils;

import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.models.Place;
import com.cheyrouse.gael.go4lunch.models.Result;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface Go4LunchService {

    public static final String API_KEY = "AIzaSyDzaDQWlbBi78NFOUhG7mS8UIgdeyd17GY";

    //Requests HTTP
    @GET("place/nearbysearch/json?radius=5500&type=restaurant&key="+ API_KEY)
    Observable<Place> getBySection(@Query(value = "location", encoded = true) String location);


    //Request with RetroFit, RxJava and OkHttp
    ThreadLocal<Retrofit> retrofit = new ThreadLocal<Retrofit>() {
        @Override
        protected Retrofit initialValue() {
            return new Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/maps/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
    };

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build();

}
