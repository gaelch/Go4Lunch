package com.cheyrouse.gael.go4lunch.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.controller.fragment.RestauDetailFragment;
import com.cheyrouse.gael.go4lunch.models.User;

import java.util.List;

public class DetailAdapter extends RecyclerView.Adapter<DetailViewHolder> {


    // FOR DATA
    private List<User> users;
    private RequestManager glide;

    // CONSTRUCTOR
    public DetailAdapter(List<User> userList, RequestManager glide) {
        this.users = userList;
        this.glide = glide;
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // CREATE VIEW HOLDER AND INFLATING ITS XML LAYOUT
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.work_mates_frament_items, parent, false);

        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
        holder.updateWithUsers(this.users.get(position), this.glide);
    }

    @Override
    public int getItemCount() {
        return this.users.size();
    }

    //Callback to items position
    public interface onUserAdapterListener {
        void onArticleClicked(User user);
    }
}
