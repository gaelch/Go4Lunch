package com.cheyrouse.gael.go4lunch.views;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.Utils.GeometryUtil;
import com.cheyrouse.gael.go4lunch.models.Result;
import com.cheyrouse.gael.go4lunch.models.User;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.cheyrouse.gael.go4lunch.Utils.Constants.BASE_URL;
import static com.cheyrouse.gael.go4lunch.Utils.Constants.MAX_HEIGHT;
import static com.cheyrouse.gael.go4lunch.Utils.Constants.MAX_WIDTH;
import static com.cheyrouse.gael.go4lunch.Utils.Go4LunchService.API_KEY;

class RecyclerViewHolder extends RecyclerView.ViewHolder  {

    @BindView(R.id.card) CardView constraintLayout;
    @BindView(R.id.restaurant_name) TextView tvRestaurantName;
    @BindView(R.id.address) TextView tvAddress;
    @BindView(R.id.textViewSchedule) TextView tvSchedule;
    @BindView(R.id.imageViewRestaurant) ImageView imageViewRestaurant;
    @BindView(R.id.textView_coWorkers) TextView tvCoWorkers;
    @BindView(R.id.imageViewStars) ImageView imageViewStars;
    @BindView(R.id.distanceToMe) TextView textViewDistance;


    RecyclerViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    //Update Items
    void updateWithUssers(Context context, final Result result, RequestManager glide, final RecyclerViewAdapter.onArticleAdapterListener callback) {
        String open = "";
        tvRestaurantName.setText(result.getName());
        tvAddress.setText(result.getVicinity());
        if(result.getOpeningHours() != null) {
            if (result.getOpeningHours().getOpenNow()) {
                open = "ouvert en ce moment";
            } else {
                open = "fermé";
            }
        }

        tvSchedule.setText(open);

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
        tvCoWorkers.setText("0");

        this.constraintLayout.setOnClickListener(v -> callback.onRestaurantClicked(result));
    }


}
