package com.cheyrouse.gael.go4lunch.views;

import android.content.Context;
import android.widget.Filter;

import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.models.Prediction;
import com.cheyrouse.gael.go4lunch.models.Predictions;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cheyrouse.gael.go4lunch.controller.activity.HomeActivity;
import com.cheyrouse.gael.go4lunch.utils.APIClient;
import com.cheyrouse.gael.go4lunch.utils.GoogleMapAPI;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PlacesAutoCompleteAdapter extends ArrayAdapter<Prediction> {

    @BindView(R.id.constraintLayout_autComp)
    ConstraintLayout constraintLayout;

    private Context context;
    private List<Prediction> predictions;
    private String location;
    private final onTextViewAdapterListener mListener;
    private TextView textViewName;

    public PlacesAutoCompleteAdapter(Context context, List<Prediction> predictions,
                                     String location, HomeActivity listener) {
        super(context, R.layout.place_row_layout, predictions);
        this.context = context;
        this.predictions = predictions;
        this.location = location;
        this.mListener = listener;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(context).inflate(R.layout.place_row_layout, null);
        ButterKnife.bind(this, view);
        if (predictions != null && predictions.size() > 0) {
            Prediction prediction = predictions.get(position);
            Log.e("click search item: ", prediction.getDescription());
            textViewName = view.findViewById(R.id.textViewName);
            textViewName.setBackground(context.getDrawable(R.drawable.rouned_corner));
            textViewName.setText(prediction.getDescription());

            this.constraintLayout.setOnClickListener(v -> mListener.onRestaurantClicked(prediction));

        }
        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new PlacesAutoCompleteFilter(this, context, location);
    }


    //Callback to items position
    public interface onTextViewAdapterListener {
        void onRestaurantClicked(Prediction prediction);
    }


    private class PlacesAutoCompleteFilter extends Filter {
        private PlacesAutoCompleteAdapter placesAutoCompleteAdapter;
        private Context context;
        private String location;


        public PlacesAutoCompleteFilter(PlacesAutoCompleteAdapter placesAutoCompleteAdapter, Context context, String location) {
            super();
            this.placesAutoCompleteAdapter = placesAutoCompleteAdapter;
            this.context = context;
            this.location = location;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            try {
                placesAutoCompleteAdapter.predictions.clear();
                FilterResults filterResults = new FilterResults();
                if (charSequence == null || charSequence.length() == 0) {
                    filterResults.values = new ArrayList<Prediction>();
                    filterResults.count = 0;
                } else {
                    if(charSequence.length() > 1){
                        GoogleMapAPI googleMapAPI = APIClient.getClient().create(GoogleMapAPI.class);
                        Predictions predictions = googleMapAPI.getPlacesAutoComplete(charSequence.toString(), location, 5500, context.getString(R.string.google_maps_key)).execute().body();
                        filterResults.values = predictions.getPredictions();
                        filterResults.count = predictions.getPredictions().size();
                    }
                }
                return filterResults;
            } catch (Exception e) {
                return null;
            }
        }


        @Override
        protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
            placesAutoCompleteAdapter.predictions.clear();
            placesAutoCompleteAdapter.predictions.addAll((List<Prediction>) filterResults.values);
            placesAutoCompleteAdapter.notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            Prediction prediction = (Prediction) resultValue;
            return prediction.getDescription();
        }
    }
}

