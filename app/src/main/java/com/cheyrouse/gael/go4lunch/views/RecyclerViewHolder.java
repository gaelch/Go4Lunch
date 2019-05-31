package com.cheyrouse.gael.go4lunch.views;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.cheyrouse.gael.go4lunch.BuildConfig;
import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.Utils.StringHelper;
import com.cheyrouse.gael.go4lunch.controller.fragment.MapsFragment;
import com.cheyrouse.gael.go4lunch.models.Result;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.cheyrouse.gael.go4lunch.Utils.Go4LunchService.API_KEY;

class RecyclerViewHolder extends RecyclerView.ViewHolder  {

    @BindView(R.id.list_rest_constraint) ConstraintLayout constraintLayout;
    @BindView(R.id.restaurant_name) TextView tvRestaurantName;
    @BindView(R.id.distance) TextView tvDistance;
    @BindView(R.id.address) TextView tvAddress;
    @BindView(R.id.textViewSchedule) TextView tvSchedule;
    @BindView(R.id.imageViewRestaurant) ImageView imageViewRestaurant;
    @BindView(R.id.textView_co_workers) TextView tvCoWorkers;
    @BindView(R.id.imageViewStars) ImageView imageViewStars;

    public static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/photo";
    public static final int MAX_WIDTH = 75;
    public static final int MAX_HEIGHT = 75;



    RecyclerViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    //Update Items
    void updateWithResult(final Result result, RequestManager glide, final RecyclerViewAdapter.onArticleAdapterListener callback) {
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
        this.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onArticleClicked(result);
            }
        });
    }

    }
