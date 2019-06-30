package com.cheyrouse.gael.go4lunch.controller.fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.Utils.RestaurantHelper;
import com.cheyrouse.gael.go4lunch.models.Restaurant;
import com.cheyrouse.gael.go4lunch.models.Result;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import butterknife.ButterKnife;
import static android.support.constraint.Constraints.TAG;
import static com.cheyrouse.gael.go4lunch.Utils.Constants.RESTAURANTS;
import static com.cheyrouse.gael.go4lunch.Utils.Constants.UID_DOC_RESTAURANTS;
import static com.cheyrouse.gael.go4lunch.controller.fragment.ListFragment.RESULT;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private Context context = getApplicationContext();
    private MapsFragmentListener mListener;
    private Location locationCt;
    private List<Result> results;
    private List<Restaurant> restaurantList;
    private List<com.cheyrouse.gael.go4lunch.models.Location> locationList;
    private Marker marker;


    public MapsFragment() {
        // Required empty public constructor
    }


    public static MapsFragment newInstance(List<Result> results, List<Restaurant> restaurantList) {
        // Create new fragment
        MapsFragment frag = new MapsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(RESULT, (Serializable) results);
        bundle.putSerializable(RESTAURANTS, (Serializable) restaurantList);
        frag.setArguments(bundle);
        return frag;
    }

    //Attach the callback tto activity
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof MapsFragmentListener){
            //Listener to pass userLogin to th activityMain
            mListener = (MapsFragmentListener) context;
        }
        else{
            Log.d(TAG, "onAttach: parent Activity must implement MainFragmentListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View v = inflater.inflate(R.layout.fragment_maps, container, false);
        ButterKnife.bind(this, v);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        getTheBundle();
        map.getMapAsync(this);
         return v;
    }

    private void getTheBundle() {
        assert getArguments() != null;
        results = (List<Result>) getArguments().getSerializable(RESULT);
        restaurantList = (List<Restaurant>) getArguments().getSerializable(RESTAURANTS);
        Log.e("restoListSizeInMap", String.valueOf(restaurantList.size()));
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showSettingsAlert();
            return ;
        }
        mMap.setMyLocationEnabled(true);
        LocationManager locationManagerCt = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(Context.LOCATION_SERVICE);
        assert locationManagerCt != null;
        locationCt = locationManagerCt.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(locationCt == null){
            showSettingsAlert();
        } else{
            goToMyLocation();
        }
        locationList = new ArrayList<>();
        for (Result r : results){
            LatLng latLng = new LatLng(r.getGeometry().getLocation().getLat(),r.getGeometry().getLocation().getLng());
            String title = r.getName();
            boolean found = false;
            for (Restaurant restaurant : restaurantList){
                if(restaurant.getRestaurantName().equals(r.getName())){
                    found = true;
                }
            }if (!found) {
                storeInDatabase(r.getName(), r.getId(), r.getName(), r.getGeometry().getLocation().getLat(),
                        r.getGeometry().getLocation().getLng());
            }
        }
        for (Restaurant restaurant : restaurantList){
            if(restaurant.getUsers() != null){
                if(restaurant.getUsers().size() > 0){
                    marker = mMap.addMarker(createMarkersGreen(restaurant.getLat(), restaurant.getLng(), restaurant.getRestaurantName()));
                }else{
                    marker = mMap.addMarker(createMarkers(restaurant.getLat(), restaurant.getLng(), restaurant.getRestaurantName()));
                }
            }else{
                marker = mMap.addMarker(createMarkers(restaurant.getLat(), restaurant.getLng(), restaurant.getRestaurantName()));
            }
        }
        mMap.setOnMarkerClickListener(this);
    }

    private MarkerOptions createMarkers(double lat, double lng, String s) {
        LatLng latLng = new LatLng(lat, lng);
        Drawable circleDrawable = getResources().getDrawable(R.drawable.ic_restaurant_white_18dp);
        BitmapDescriptor icon = getMarkerIconFromDrawable(circleDrawable);
        //icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_white_18dp);
        return new MarkerOptions().icon(icon)
                .position(latLng)
                .title(s)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
    }

    private MarkerOptions createMarkersGreen(double lat, double lng, String s) {
        LatLng latLng = new LatLng(lat, lng);
        Drawable circleDrawable = getResources().getDrawable(R.drawable.ic_restaurant_white_18dp);
        BitmapDescriptor icon = getMarkerIconFromDrawable(circleDrawable);
        //icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_white_18dp);
        return new MarkerOptions().icon(icon)
                .position(latLng)
                .title(s)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
    }


    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        int color = getResources().getColor(R.color.green);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
        canvas.setBitmap(bitmap);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        drawable.setBounds(0, 0, (int)getResources().getDimension(R.dimen._30sdp), (int)getResources().getDimension(R.dimen._30sdp));
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void storeInDatabase(String uid, String restaurantUid, String restaurantName , double lat, double lng) {
        RestaurantHelper.createRestaurant(uid, restaurantUid, restaurantName, lat, lng);
        Log.d("testResultResto", restaurantName);
    }

    private void goToMyLocation() {
        assert locationCt != null;
        LatLng latLng = new LatLng(locationCt.getLatitude(),
                locationCt.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.e("testClickMarker", marker.getTitle());
        for(Result r: results){
            if(marker.getTitle().equals(r.getName())){
                mListener.callbackMaps(r);
            }
        }
        return false;
    }

    //Callback to ArticleFragment
    public interface MapsFragmentListener{
        void callbackMaps(Result result);
    }
}
