package com.cheyrouse.gael.go4lunch.views;



import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.RequestManager;
import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.controller.fragment.ListFragment;
import com.cheyrouse.gael.go4lunch.models.Result;

import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private final onArticleAdapterListener mListener;

    // FOR DATA
    private List<Result> results;
    private RequestManager glide;
    private Context context;
    // CONSTRUCTOR
    public RecyclerViewAdapter(Context context, List<Result> results, RequestManager glide, ListFragment listAdapterListener) {
        this.mListener = listAdapterListener;
        this.results = results;
        this.glide = glide;
        this.context = context;
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
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder viewHolder, int position) {
        viewHolder.updateWithUssers(context, this.results.get(position), this.glide, mListener);
    }

    // RETURN THE TOTAL COUNT OF ITEMS IN THE LIST
    @Override
    public int getItemCount() {
        return this.results.size();
    }


    //Callback to items position
    public interface onArticleAdapterListener {
        void onRestaurantClicked(Result result);
    }
}

