package com.cheyrouse.gael.go4lunch.views;



import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.RequestManager;
import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.controller.fragment.ListFragment;
import com.cheyrouse.gael.go4lunch.models.Restaurant;
import com.cheyrouse.gael.go4lunch.models.ResultDetail;
import com.cheyrouse.gael.go4lunch.models.User;

import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private final onArticleAdapterListener mListener;

    // FOR DATA
    private List<ResultDetail> results;
    private List<User> userList;
    private RequestManager glide;
    private Context context;
    private List<Restaurant> restaurants;

    // CONSTRUCTOR
    public RecyclerViewAdapter(Context context, List<ResultDetail> resultList, List<User> userList, RequestManager glide, ListFragment listAdapterListener) {
        this.mListener = listAdapterListener;
        this.results = resultList;
        this.glide = glide;
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // CREATE VIEW HOLDER AND INFLATING ITS XML LAYOUT
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_fragment_items, parent, false);

        return new RecyclerViewHolder(view);
    }

    // UPDATE VIEW HOLDER WITH A TOPSTORIES
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder viewHolder, int position) {
        viewHolder.updateWithRestaurants(context, this.results.get(position), this.userList, this.glide, mListener);
    }

    // RETURN THE TOTAL COUNT OF ITEMS IN THE LIST
    @Override
    public int getItemCount() {
        return this.results.size();
    }


    //Callback to items position
    public interface onArticleAdapterListener {
        void onRestaurantClicked(ResultDetail result);
    }
}

