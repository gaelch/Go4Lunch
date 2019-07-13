package com.cheyrouse.gael.go4lunch.views;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.utils.GeometryUtil;
import com.cheyrouse.gael.go4lunch.utils.RestaurantHelper;
import com.cheyrouse.gael.go4lunch.utils.starsUtils;
import com.cheyrouse.gael.go4lunch.models.Restaurant;
import com.cheyrouse.gael.go4lunch.models.ResultDetail;
import com.cheyrouse.gael.go4lunch.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.cheyrouse.gael.go4lunch.utils.Constants.BASE_URL;
import static com.cheyrouse.gael.go4lunch.utils.Constants.MAX_HEIGHT;
import static com.cheyrouse.gael.go4lunch.utils.Constants.MAX_WIDTH;
import static com.cheyrouse.gael.go4lunch.utils.Go4LunchService.API_KEY;

class RecyclerViewHolder extends RecyclerView.ViewHolder  {

    @BindView(R.id.card) CardView constraintLayout;
    @BindView(R.id.restaurant_name) TextView tvRestaurantName;
    @BindView(R.id.address) TextView tvAddress;
    @BindView(R.id.textViewSchedule) TextView tvSchedule;
    @BindView(R.id.imageViewRestaurant) ImageView imageViewRestaurant;
    @BindView(R.id.tV_coWorkers) TextView tvCoWorkers;
    @BindView(R.id.imageViewStars1) ImageView imageViewStars1;
    @BindView(R.id.imageViewStars2) ImageView imageViewStars2;
    @BindView(R.id.imageViewStars3) ImageView imageViewStars3;
    @BindView(R.id.distanceToMe) TextView textViewDistance;

    private Restaurant restaurant;

    RecyclerViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    //Update Items
    @RequiresApi(api = Build.VERSION_CODES.N)
    void updateWithUssers(Context context, final ResultDetail result, List<User> users, RequestManager glide, final RecyclerViewAdapter.onArticleAdapterListener callback) {

        if(result.getName() != null){
            tvRestaurantName.setText(result.getName());
        }else{
            if(restaurant != null){
                tvRestaurantName.setText(result.getAddressComponents().get(0).getLongName());
            }
        }

        if(result.getFormattedAddress() != null){
            tvAddress.setText(result.getFormattedAddress());
        }
        if(result.getOpeningHours() != null){
         //   tvSchedule.setText(result.getOpeningHours().getPeriods().toString());
        }

        if (!(result.getPhotos() == null)){
            if (!(result.getPhotos().isEmpty())){
                glide.load(BASE_URL+"?maxwidth="+MAX_WIDTH+"&maxheight="+MAX_HEIGHT+"&photoreference="+result
                        .getPhotos().get(0).getPhotoReference()+"&key="+ API_KEY).apply(new RequestOptions().override(75, 75)).into(imageViewRestaurant);
            }
        }else{
            glide.load(result.getIcon()).apply(RequestOptions.centerCropTransform()).into(imageViewRestaurant);
        }
        double distance = GeometryUtil.calculateDistance(context, result.getGeometry().getLocation().getLng(), result.getGeometry().getLocation().getLat());
        int distanceInMeters = (int) distance;
        if(distanceInMeters > 999){
            textViewDistance.setText(GeometryUtil.getString1000Less(distance));
        }else{
            String distanceString = distanceInMeters + " m";
            textViewDistance.setText(distanceString);
        }
        //pass list mates and see if they choose this restaurant

        List<User> usersAreJoining = new ArrayList<>();
        for (User user : users) {
            if (user.getChoice() != null) {
                if (user.getChoice().equals(result.getName())) {
                    usersAreJoining.add(user);
                }
            }
        }

        //get Restaurants from fire store to rating
        restaurant = new Restaurant();
        RestaurantHelper.getRestaurant(result.getName()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    restaurant.setRestaurantName((String) Objects.requireNonNull(task.getResult()).get("restaurantName"));
                    restaurant.setRate((List<String>) task.getResult().get("rate"));
                    restaurant.setUsers((List<String>) task.getResult().get("users"));
                    setStars(context, users, restaurant);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("fail", e.getMessage());
            }
        });
        if(usersAreJoining.size() != 0){
            tvCoWorkers.setText(String.valueOf("(" + usersAreJoining.size() + ")"));
        }else{
            tvCoWorkers.setText("(0)");
        }
        this.constraintLayout.setOnClickListener(v -> callback.onRestaurantClicked(result));
    }

    private void setStars(Context context, List<User> users, Restaurant restaurant) {
        if(restaurant != null){
            if(restaurant.getRate()!= null && restaurant.getRate().size()!=0){
                if (starsUtils.getRate(restaurant.getRate().size(), users) == 1) {
                    imageViewStars3.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_rate_white_18dp));
                }
                if (starsUtils.getRate(restaurant.getRate().size(), users) == 2) {
                    imageViewStars3.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_rate_white_18dp));
                    imageViewStars2.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_rate_white_18dp));
                }
                if (starsUtils.getRate(restaurant.getRate().size(), users) == 3) {
                    imageViewStars1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_rate_white_18dp));
                    imageViewStars2.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_rate_white_18dp));
                    imageViewStars3.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_rate_white_18dp));
                }
            }
        }


    }


}
