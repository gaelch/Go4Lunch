package com.cheyrouse.gael.go4lunch.utils;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.cheyrouse.gael.go4lunch.controller.fragment.ListFragment;
import com.cheyrouse.gael.go4lunch.controller.fragment.MapsFragment;
import com.cheyrouse.gael.go4lunch.controller.fragment.RestauDetailFragment;
import com.cheyrouse.gael.go4lunch.controller.fragment.WorkmatesFragment;
import com.cheyrouse.gael.go4lunch.models.Prediction;
import com.cheyrouse.gael.go4lunch.models.Restaurant;
import com.cheyrouse.gael.go4lunch.models.Result;
import com.cheyrouse.gael.go4lunch.models.ResultDetail;
import com.cheyrouse.gael.go4lunch.models.User;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {

    // To change predictions to resultDetails objects
    public static List<ResultDetail> transforPredictionToResultDetail(List<ResultDetail> resultDetailList, List<Prediction> resultsPredictions){
        List<ResultDetail> newResults = new ArrayList<>();
        if (resultDetailList.size() > 0) {
            if (resultsPredictions != null && resultsPredictions.size() > 0) {
                for (ResultDetail resultDetail : resultDetailList) {
                    for (Prediction prediction : resultsPredictions) {
                        if (resultDetail.getId().equals(prediction.getId())) {
                            newResults.add(resultDetail);
                        }
                    }
                }
            }
        }
        return newResults;
    }

    // Find joining coWorkers in list
    public static List<User> getJoiningCoWorkers(List<User> users, ResultDetail result){
        List<User> usersAreJoining = new ArrayList<>();
        for (User user : users) {
            if (user.getChoice() != null) {
                if (user.getChoice().equals(result.getName())) {
                    usersAreJoining.add(user);
                }
            }
        }
        return usersAreJoining;
    }

    // Find user in rate list to know if update or delete like
    public static boolean findUserInRateList(String userId, List<String> rateList) {
        boolean found = false;
        for (String rate : rateList) {
            if (rate.equals(userId)) {
                found = true;
            }
        }
        return found;
    }

    // Get fragment manager to update good fragment
    public static int getBackStackToRefreshData(Fragment newFragment, List<Fragment> fragments) {
        int frag = 0;
        if (newFragment instanceof RestauDetailFragment) {
            if(fragments.get(fragments.size() - 2) != null){
                newFragment = fragments.get(fragments.size() - 2);
                if(newFragment instanceof MapsFragment){
                    frag = 1;
                }
                if(newFragment instanceof ListFragment){
                    frag = 2;
                }
                if(newFragment instanceof WorkmatesFragment){
                    frag = 3;
                }
            }
        }
        return frag;
    }

    // Get restaurant in list to display choice
    public static ResultDetail getRestaurantToDisplayYourChoiceIfExist(List<ResultDetail> resultDetailList, String choice){
        ResultDetail resultDet = new ResultDetail();
        for (ResultDetail resultDetail : resultDetailList) {
            if (resultDetail.getName().equals(choice)) {
                resultDet = resultDetail;
            }
        }
        return resultDet;
    }

    // Make restaurant list with results objects
    public static List<Restaurant> makeListResultRestaurantWithResultList(List<Result> results, List<Restaurant> restaurants){
        List<Restaurant> restaurantList = new ArrayList<>();
        for (Restaurant restaurant : restaurants) {
            for (Result r : results) {
                if (restaurant.getRestaurantName().equals(r.getName())) {
                    restaurantList.add(restaurant);
                }
            }
            Log.e("restaurantsListSize", String.valueOf(restaurantList.size()));
        }
        return restaurantList;
    }

    // Make restaurant list with resultDetails objects
    public static List<Restaurant> makeListResultRestaurantWithResultDetailList(List<ResultDetail> results, List<Restaurant> restaurants){
        List<Restaurant> restaurantList = new ArrayList<>();
        for (Restaurant restaurant : restaurants) {
            for (ResultDetail r : results) {
                if (restaurant.getRestaurantName().equals(r.getName())) {
                    restaurantList.add(restaurant);
                }
            }
            Log.e("restaurantsListSize", String.valueOf(restaurantList.size()));
        }
        return restaurantList;
    }
}
