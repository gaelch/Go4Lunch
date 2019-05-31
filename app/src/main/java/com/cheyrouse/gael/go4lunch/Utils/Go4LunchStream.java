package com.cheyrouse.gael.go4lunch.Utils;

import com.cheyrouse.gael.go4lunch.models.Place;
import com.cheyrouse.gael.go4lunch.models.Result;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class Go4LunchStream {


    public static Observable<Place> streamFetchRestaurants(String location){
        Go4LunchService go4LunchService = Objects.requireNonNull(Go4LunchService.retrofit.get()).create(Go4LunchService.class);
        return go4LunchService.getBySection(location)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(100, TimeUnit.SECONDS);
    }
}
