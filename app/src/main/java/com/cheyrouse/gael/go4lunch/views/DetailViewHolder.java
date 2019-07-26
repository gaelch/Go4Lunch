package com.cheyrouse.gael.go4lunch.views;

import android.annotation.SuppressLint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.models.User;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.imageViewWorkers) ImageView imageView;
    @BindView(R.id.textWorker) TextView textView;
    @BindView(R.id.constraint_work_list) CardView cardView;

    public static final String isEating = " is eating ";


    public DetailViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    //Update Items
    @SuppressLint("SetTextI18n")
    void updateWithUsers(final User user, RequestManager glide) {
        if(user != null){
            if(user.getUsername() != null){
                textView.setText(user.getUsername() + isEating + user.getChoice());
            }else {
                textView.setText("Mikasa is joining !");
            }
            if(user.getUrlPicture() != null && !user.getUrlPicture().isEmpty()){
                glide.load(user.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(imageView);

            }else{
                glide.load(imageView.getResources().getDrawable(R.drawable.baseline_perm_identity_black_18dp)).apply(RequestOptions.circleCropTransform()).into(imageView);
            }
        }
    }
}
