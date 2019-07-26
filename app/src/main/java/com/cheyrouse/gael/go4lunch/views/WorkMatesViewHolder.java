package com.cheyrouse.gael.go4lunch.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.models.User;
import com.cheyrouse.gael.go4lunch.utils.Prefs;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkMatesViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.imageViewWorkers)
    ImageView imageView;
    @BindView(R.id.textWorker)
    TextView textView;
    @BindView(R.id.constraint_work_list)
    CardView card;

    public static final String isEating = " is eating ";
    public static final String isJoining = " is joining ! ";
    private Prefs prefs;


    public WorkMatesViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    //Update Items
    @SuppressLint("SetTextI18n")
    void updateWithUsers(Context context, final User user, RequestManager glide, final WorkMatesAdapter.onUserAdapterListener callback,  int i) {
        prefs = Prefs.get(context);
        if (user != null) {
            if(i == 0){
                if (user.getUsername() != null && user.getChoice() != null && user.getChoice().length() != 0) {
                    textView.setText(user.getUsername() + isEating + user.getChoice());
                } else {
                    textView.setText(user.getUsername() + " hasn't decided yet");
                    textView.setTextColor(this.itemView.getResources().getColor(R.color.grey));
                }
            }else{
                if (user.getUsername() != null && user.getChoice() != null && user.getChoice().length() != 0) {
                    textView.setText(user.getUsername() + isJoining);
                }
            }

            if (user.getUrlPicture() != null && !user.getUrlPicture().isEmpty()) {
                glide.load(user.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(imageView);

            } else {
                Uri selectedImage = prefs.getPicture(user.getUsername());
                if (selectedImage != null) {
                    Picasso.get().load(selectedImage).into((imageView));
                }else{
                    imageView.setBackgroundColor(context.getResources().getColor(R.color.green));
                    glide.load(imageView.getResources().getDrawable(R.drawable.baseline_perm_identity_black_18dp)).apply(RequestOptions.circleCropTransform()).into(imageView);
                }
            }
        }
        this.card.setOnClickListener(v -> {
            if(Objects.requireNonNull(user).getChoice()!=null){
                callback.onArticleClicked(user);
            }else{
                Toast.makeText(context, "Sorry but " + user.getUsername() + " hasn't decided yet", Toast.LENGTH_LONG).show();
            }
        });
    }

}
