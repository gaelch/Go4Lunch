package com.cheyrouse.gael.go4lunch.controller.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.models.ResultDetail;
import com.cheyrouse.gael.go4lunch.models.User;
import com.cheyrouse.gael.go4lunch.views.RecyclerViewAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.constraint.Constraints.TAG;
import static com.cheyrouse.gael.go4lunch.utils.Constants.USERS;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment implements RecyclerViewAdapter.onArticleAdapterListener{

    @BindView(R.id.fragment_list_recycler_view) RecyclerView recyclerView;

    public static final String RESULT = "result";

    private ListFragmentListener mListener;
    private RecyclerViewAdapter adapter;
    private List<ResultDetail> resultListUpdated;
    private List<User> userList;

    public ListFragment() {
        // Required empty public constructor
    }

    //To attach fragment to Activity
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ListFragmentListener) {
            //Listener to pass userLogin to th activityMain
            mListener = (ListFragmentListener) context;
        } else {
            Log.d(TAG, "onAttach: parent Activity must implement MainFragmentListener");
        }
    }

    // New instance and Bundle
    public static ListFragment newInstance(List<ResultDetail> results, List<User> users) {
        // Create new fragment
        ListFragment frag = new ListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(RESULT, (Serializable) results);
        bundle.putSerializable(USERS, (Serializable) users);
        frag.setArguments(bundle);
        return frag;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, v);
        getTheBundle();
        return v;
    }

    // get the data Bundle
    @SuppressWarnings("unchecked")
    private void getTheBundle() {
        assert getArguments() != null;
        List<ResultDetail> results = (List<ResultDetail>) getArguments().getSerializable(RESULT);
        userList = (List<User>) getArguments().getSerializable(USERS);
        configureRecyclerView();
        updateUI(results);
    }

    //configure recyclerView and Tabs
    private void configureRecyclerView() {
        resultListUpdated = new ArrayList<>();
        // Create adapter passing in the sample user data
        this.adapter = new RecyclerViewAdapter(getActivity(), resultListUpdated, userList, Glide.with(this), this);
        // Attach the adapter to the recyclerView to populate items
        this.recyclerView.setAdapter(this.adapter);
        // Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    // Callback
    @Override
    public void onRestaurantClicked(ResultDetail result) {
        mListener.callbackList(result);
        Log.e("test ressult click", "result returned !");
    }


    // Update the recyclerView adapter
    private void updateUI(List<ResultDetail> resultList){
        if(resultListUpdated != null){
            resultListUpdated.clear();
        }
        if(resultList != null) {
            Objects.requireNonNull(resultListUpdated).addAll(resultList);
            if(resultListUpdated.size() == 0){
                resultListUpdated.clear();
            }
            adapter.notifyDataSetChanged();
        }
    }


    // Interface implemented in activity home
    public interface ListFragmentListener {
        void callbackList(ResultDetail result);
    }
}
