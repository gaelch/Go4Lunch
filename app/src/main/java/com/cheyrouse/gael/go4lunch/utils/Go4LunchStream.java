package com.cheyrouse.gael.go4lunch.utils;

import com.cheyrouse.gael.go4lunch.models.Place;
import com.cheyrouse.gael.go4lunch.models.PlaceDetails;
import com.cheyrouse.gael.go4lunch.models.Predictions;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class Go4LunchStream {


    public static Observable<Place> streamFetchRestaurants(String location){
        Go4LunchService go4LunchService = Objects.requireNonNull(Go4LunchService.retrofit.get()).create(Go4LunchService.class);
        return go4LunchService.getMapPlace(location)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(100, TimeUnit.SECONDS);
    }

    public static Observable<PlaceDetails> streamFetchRestaurantsDetails(String placeId){
        Go4LunchService go4LunchService = Objects.requireNonNull(Go4LunchService.retrofit.get()).create(Go4LunchService.class);
        return go4LunchService.getMapPlaceDetails(placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(100, TimeUnit.SECONDS);
    }
    public static Observable<Predictions> getPlacesAutoComplete(String placeId, String types, String location, int radius, String language, String key){
        Go4LunchService go4LunchService = Objects.requireNonNull(Go4LunchService.retrofit.get()).create(Go4LunchService.class);
        return go4LunchService.getPlacesAutoComplete(placeId, types, location, radius, language, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(100, TimeUnit.SECONDS);
    }

}
