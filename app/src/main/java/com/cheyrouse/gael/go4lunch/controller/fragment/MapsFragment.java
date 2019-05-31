package com.cheyrouse.gael.go4lunch.controller.fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.models.Result;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import butterknife.ButterKnife;
import static android.support.constraint.Constraints.TAG;
import static com.cheyrouse.gael.go4lunch.controller.fragment.ListFragment.RESULT;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Context context = getApplicationContext();
    private MapsFragmentListener mListener;
    private Location locationCt;
    private List<Result> results;
    private List<com.cheyrouse.gael.go4lunch.models.Location> locationList;
    private Marker marker;

    public MapsFragment() {
        // Required empty public constructor
    }


    public static MapsFragment newInstance(List<Result> results) {
        // Create new fragment
        MapsFragment frag = new MapsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(RESULT, (Serializable) results);
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
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return ;
        }
        mMap.setMyLocationEnabled(true);
        LocationManager locationManagerCt = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(Context.LOCATION_SERVICE);
        assert locationManagerCt != null;
        locationCt = locationManagerCt.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(locationCt == null){
            showSettingsAlert();
        } else{
            goToMyLocation();

        }
        if(locationCt == null){
            goToMyLocation();
        }
        locationList = new ArrayList<>();
        for (Result r : results){
            LatLng latLng = new LatLng(r.getGeometry().getLocation().getLat(),r.getGeometry().getLocation().getLng());
            String title = r.getName();
            mMap.addMarker(new MarkerOptions().position(latLng).title(title));
        }
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

    //Callback to ArticleFragment
    public interface MapsFragmentListener{
        void callbackArticle();
    }
}
