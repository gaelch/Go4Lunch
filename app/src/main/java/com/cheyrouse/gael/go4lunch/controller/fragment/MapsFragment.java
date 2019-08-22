package com.cheyrouse.gael.go4lunch.controller.fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.models.ResultDetail;
import com.cheyrouse.gael.go4lunch.services.GPSTracker;
import com.cheyrouse.gael.go4lunch.models.Restaurant;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.constraint.Constraints.TAG;
import static com.cheyrouse.gael.go4lunch.utils.Constants.RESTAURANTS;
import static com.cheyrouse.gael.go4lunch.controller.fragment.ListFragment.RESULT;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    @BindView(R.id.my_location)
    FloatingActionButton buttonMyLocation;

    private GoogleMap mMap;
    private Context context = getApplicationContext();
    private MapsFragmentListener mListener;
    private Location locationCt;
    private List<ResultDetail> results;
    private List<Restaurant> restaurantList;
    private Marker marker;


    public MapsFragment() {
        // Required empty public constructor
    }

    //New instance and Bundle
    public static MapsFragment newInstance(List<ResultDetail> results, List<Restaurant> restaurantList) {
        // Create new fragment
        MapsFragment frag = new MapsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(RESULT, (Serializable) results);
        bundle.putSerializable(RESTAURANTS, (Serializable) restaurantList);
        frag.setArguments(bundle);
        return frag;
    }

    //Attach the listener to activity
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MapsFragmentListener) {
            //Listener to pass userLogin to th activityMain
            mListener = (MapsFragmentListener) context;
        } else {
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
        configureButtonLocation();
        return v;
    }

    private void configureButtonLocation() {
        buttonMyLocation.setOnClickListener(v -> gpsGetLocation());
    }

    // Get data in bundle
    private void getTheBundle() {
        assert getArguments() != null;
        results = (List<ResultDetail>) getArguments().getSerializable(RESULT);
        restaurantList = (List<Restaurant>) getArguments().getSerializable(RESTAURANTS);
    }


    // Handle Map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showSettingsAlert();
            return;
        }
        mMap.setMyLocationEnabled(true);
        LocationManager locationManagerCt = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(Context.LOCATION_SERVICE);
        locationCt = Objects.requireNonNull(locationManagerCt).getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (locationCt == null) {
            gpsGetLocation();
        } else {
            goToMyLocation(null);
        }

        for (ResultDetail r : results) {
            LatLng latLng = new LatLng(r.getGeometry().getLocation().getLat(), r.getGeometry().getLocation().getLng());
            String title = r.getName();
            boolean match = false;
            for (Restaurant restaurant : restaurantList) {
                if (restaurant.getRestaurantName().equals(r.getName())) {
                    if (restaurant.getUsers() != null && restaurant.getUsers().size() > 0) {
                        marker = mMap.addMarker(createMarkersGreen(latLng.latitude, latLng.longitude, title));
                    } else {
                        marker = mMap.addMarker(createMarkers(latLng.latitude, latLng.longitude, title));
                    }
                }
                if(restaurant.getRestaurantName().equals(r.getName())){
                    match = true;
                }
            }
            if(!match){
                marker = mMap.addMarker(createMarkers(latLng.latitude, latLng.longitude, title));
            }

        }
        mMap.setOnMarkerClickListener(this);
    }

    // Create Marker red
    private MarkerOptions createMarkers(double lat, double lng, String s) {
        LatLng latLng = new LatLng(lat, lng);
        Drawable circleDrawable = getResources().getDrawable(R.drawable.ic_maps_red);
        BitmapDescriptor icon = getMarkerIconFromDrawable(circleDrawable);
        return new MarkerOptions().icon(icon)
                .position(latLng)
                .title(s);
    }

    // Create Marker green
    private MarkerOptions createMarkersGreen(double lat, double lng, String s) {
        LatLng latLng = new LatLng(lat, lng);
        Drawable circleDrawable = getResources().getDrawable(R.drawable.ic_maps_green);
        BitmapDescriptor icon = getMarkerIconFromDrawable(circleDrawable);
        return new MarkerOptions().icon(icon)
                .position(latLng)
                .title(s);
    }

    // BitmapDescriptor to build icon
    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        int color = getResources().getColor(R.color.green);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
        canvas.setBitmap(bitmap);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        drawable.setBounds(0, 0, (int) getResources().getDimension(R.dimen._30sdp), (int) getResources().getDimension(R.dimen._30sdp));
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    // Get location from GPS service
    private void gpsGetLocation() {
        GPSTracker gps = new GPSTracker(getApplicationContext());
        if (gps.canGetLocation()) {
            Log.e("testGpsTrue", "true");
            LatLng latLng = new LatLng(gps.getLatitude(), gps.getLongitude());
            goToMyLocation(latLng);
        }
    }

    // Move camera on my location
    private void goToMyLocation(LatLng latLng) {
        if(latLng == null){
             latLng = new LatLng(locationCt.getLatitude(),
                    locationCt.getLongitude());
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
    }

    // show dialog if GPS is not enable
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        // Setting Dialog Title
        alertDialog.setTitle(getResources().getString(R.string.GPS_is_settings));
        // Setting Dialog Message
        alertDialog.setMessage(getResources().getString(R.string.settings_menu));
        // On pressing Settings button
        alertDialog.setPositiveButton(getResources().getString(R.string.settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    // Callback
    @Override
    public boolean onMarkerClick(Marker marker) {
        String restaurantName = marker.getTitle();
        Log.e("testClickMarker", marker.getTitle());
        for (ResultDetail r : results) {
            if (restaurantName.equals(r.getName())) {
                mListener.callbackMaps(r);
            }
        }
        return false;
    }

    // Interface implemented by Home Activity
    public interface MapsFragmentListener {
        void callbackMaps(ResultDetail result);
    }
}
