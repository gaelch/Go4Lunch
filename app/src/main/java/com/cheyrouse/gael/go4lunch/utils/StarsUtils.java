package com.cheyrouse.gael.go4lunch.utils;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.models.Restaurant;
import com.cheyrouse.gael.go4lunch.models.User;

import java.util.List;

public class StarsUtils {

    // Calculate rate
    public static int getRate(double rate, List<User> users) {
        int newRate = 0;
        double i = rate / users.size() * 100;
        if (i >= 25 && i < 50) {
            newRate = 1;
        }
        if (i >= 50 && i < 75) {
            newRate = 2;
        }
        if (i >= 75) {
            newRate = 3;
        }
        Log.e("resultOfRateStars", String.valueOf(i));
        return newRate;
    }

    // Display stars in imageViews
    public static String setStars(Context context, ImageView imageViewStars1, ImageView imageViewStars2, ImageView imageViewStars3, List<User> users, Restaurant restaurant) {
        String number = "0";
        if (restaurant != null && restaurant.getRate() != null) {
            if (restaurant.getRate() == null || getRate(restaurant.getRate().size(), users) == 0) {
                imageViewStars1.setImageDrawable(null);
                imageViewStars2.setImageDrawable(null);
                imageViewStars3.setImageDrawable(null);
            }
            if (getRate(restaurant.getRate().size(), users) == 1) {
                if (imageViewStars3 != null) {
                    imageViewStars1.setImageDrawable(null);
                    imageViewStars2.setImageDrawable(null);
                    imageViewStars3.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_rate_white_18dp));
                }
                number = "1";
            }
            if (getRate(restaurant.getRate().size(), users) == 2) {
                if (imageViewStars2 != null && imageViewStars3 != null) {
                    imageViewStars1.setImageDrawable(null);
                    imageViewStars3.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_rate_white_18dp));
                    imageViewStars2.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_rate_white_18dp));
                }
                number = "2";
            }
            if (getRate(restaurant.getRate().size(), users) == 3) {
                if (imageViewStars1 != null && imageViewStars2 != null && imageViewStars3 != null) {
                    imageViewStars1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_rate_white_18dp));
                    imageViewStars2.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_rate_white_18dp));
                    imageViewStars3.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_rate_white_18dp));
                }
                number = "3";
            }
        }
        return number;
    }
}
