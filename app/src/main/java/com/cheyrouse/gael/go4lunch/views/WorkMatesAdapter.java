package com.cheyrouse.gael.go4lunch.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.controller.fragment.WorkmatesFragment;
import com.cheyrouse.gael.go4lunch.models.User;

import java.util.List;

public class WorkMatesAdapter extends RecyclerView.Adapter<WorkMatesViewHolder> {
    private final onUserAdapterListener mListener;

    // FOR DATA
    private List<User> users;
    private RequestManager glide;

    // CONSTRUCTOR
    public WorkMatesAdapter(List<User> userList, RequestManager glide, WorkmatesFragment workMatesAdapterListener) {
        this.mListener = workMatesAdapterListener;
        this.users = userList;
        this.glide = glide;
    }

    @NonNull
    @Override
    public WorkMatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // CREATE VIEW HOLDER AND INFLATING ITS XML LAYOUT
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.work_mates_frament_items, parent, false);

        return new WorkMatesViewHolder(view);
    }

    // UPDATE VIEW HOLDER WITH A USERS
    @Override
    public void onBindViewHolder(@NonNull WorkMatesViewHolder viewHolder, int position) {
        viewHolder.updateWithUsers(this.users.get(position), this.glide, mListener);
    }

    // RETURN THE TOTAL COUNT OF ITEMS IN THE LIST
    @Override
    public int getItemCount() {
        return this.users.size();
    }


    //Callback to items position
    public interface onUserAdapterListener {
        void onArticleClicked(User user);
    }
}
