package com.cheyrouse.gael.go4lunch.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.models.User;
import com.cheyrouse.gael.go4lunch.utils.Prefs;
import java.io.File;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

class WorkMatesViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.imageViewWorkers)
    ImageView imageView;
    @BindView(R.id.textWorker)
    TextView textView;
    @BindView(R.id.constraint_work_list)
    CardView card;


    WorkMatesViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    //Update Items
    @SuppressLint("SetTextI18n")
    void updateWithUsers(Context context, final User user, RequestManager glide, final WorkMatesAdapter.onUserAdapterListener callback, int i) {
        String isEating = context.getResources().getString(R.string.eating);
        String isJoining = context.getResources().getString(R.string.joining);
        String notDecided = context.getResources().getString(R.string.decided);
        String sorryBut = context.getResources().getString(R.string.sorry);
        Prefs prefs = Prefs.get(context);
        if (user != null) {
            if (i == 0) {
                if (user.getUsername() != null && user.getChoice() != null && user.getChoice().length() != 0) {
                    textView.setText(user.getUsername() + isEating + user.getChoice());
                } else {
                    textView.setText(user.getUsername() + notDecided);
                    textView.setTextColor(this.itemView.getResources().getColor(R.color.grey));
                }
            } else {
                if (user.getUsername() != null && user.getChoice() != null && user.getChoice().length() != 0) {
                    textView.setText(user.getUsername() + isJoining);
                }
            }

            if (user.getUrlPicture() != null && !user.getUrlPicture().isEmpty()) {
                glide.load(user.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(imageView);

            } else {
                Uri selectedImage = prefs.getPicture(user.getUsername());
                if (selectedImage != null) {
                    Glide.with(context).load(selectedImage)
                            .apply(RequestOptions.circleCropTransform())
                            .into((imageView));
                } else {
                    imageView.setBackgroundColor(context.getResources().getColor(R.color.green));
                    glide.load(imageView.getResources().getDrawable(R.drawable.baseline_perm_identity_black_18dp)).apply(RequestOptions.circleCropTransform()).into(imageView);
                }
            }
        }
        this.card.setOnClickListener(v -> {
            if(callback != null){
                if (Objects.requireNonNull(user).getChoice() != null) {
                    callback.onArticleClicked(user);
                } else {
                    Toast.makeText(context, sorryBut + user.getUsername() + notDecided, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
