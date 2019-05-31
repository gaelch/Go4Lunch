package com.cheyrouse.gael.go4lunch.controller.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.Utils.GPSTracker;
import com.cheyrouse.gael.go4lunch.Utils.Go4LunchStream;
import com.cheyrouse.gael.go4lunch.controller.fragment.ListFragment;
import com.cheyrouse.gael.go4lunch.controller.fragment.MapsFragment;
import com.cheyrouse.gael.go4lunch.controller.fragment.WorkmatesFragment;
import com.cheyrouse.gael.go4lunch.models.Place;
import com.cheyrouse.gael.go4lunch.models.Result;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        MapsFragment.MapsFragmentListener, ListFragment.ListFragmentListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.activity_home_bottom_navigation) BottomNavigationView bottomNavigationView;

    public static final int mainFrame = R.id.activity_home_frame_layout;
    private static final int SIGN_OUT_TASK = 10;

    private DrawerLayout drawerLayout;
    private double latitude;
    private double longitude;
    private List<Result> results;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        configureToolbar();
        configureDrawerLayout();
        configureNavigationView();
        configureBottomView();
        gpsGetLocation();
    }

    private void gpsGetLocation() {
        GPSTracker gps = new GPSTracker(this);
        if(gps.canGetLocation()){
            Log.e("testGpsTrue", "true");
            latitude = gps.getLatitude(); // returns latitude
            longitude = gps.getLongitude(); // returns longitude
            convertInString(latitude, longitude);
        }
    }

    private void convertInString(double latitude, double longitude) {
        String location = String.valueOf(latitude) + "," + String.valueOf(longitude);
        executeRequestToPlaceApi(location);
    }

    private void executeRequestToPlaceApi(String location) {
        Disposable disposable = Go4LunchStream.streamFetchRestaurants(location).subscribeWith(new DisposableObserver<Place>() {
            @Override
            public void onNext(Place result) {
                Log.e("testResponse", String.valueOf(result.getResults().size()));
                results = result.getResults();
            }
            @Override
            public void onError(Throwable e) {
            }
            @Override
            public void onComplete() {
                Log.e("Test", "TopStories is charged");
                getFragmentManagerToLaunch(MapsFragment.newInstance(results));
            }
        });
    }

    // To set he toolbar
    private void configureToolbar() {
        // Set the Toolbar
        toolbar.setTitle(R.string.title_toolbar);
        setSupportActionBar(toolbar);}

    //inflate the menu and add it to the Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    // To search
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //3 - Handle actions on menu items
        switch (item.getItemId()) {
            case R.id.menu_activity_home_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Configuration NavigationView of Drawer Menu
    private void configureNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.activity_home_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    //Configure Drawer layout
    private void configureDrawerLayout() {
        this.drawerLayout = (DrawerLayout) findViewById(R.id.activity_home_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    //Switch to menu Drawer items
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // 4 - Handle Navigation Item Click
        int id = item.getItemId();
        item.getTitle();
        switch (id) {
            case R.id.activity_main_drawer_lunch:

                break;
            case R.id.activity_main_drawer_settings:

                break;
            case R.id.activity_main_drawer_logout:
                signOutUserFromFirebase();
                Intent logoutIntent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(logoutIntent);
                break;
            default:
                break;
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOutUserFromFirebase(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted());
    }

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(){
        return aVoid -> {
            switch (HomeActivity.SIGN_OUT_TASK){
                case SIGN_OUT_TASK:
                    finish();
                    break;
                default:
                    break;
            }
        };
    }

    //To manage drawerLayout on back button
    @Override
    public void onBackPressed() {
        // 5 - Handle back click to close menu
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // 2 - Configure BottomNavigationView Listener
    private void configureBottomView(){
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return updateMainFragment(item.getItemId());
            }
        });
    }

    // -------------------
    // UI
    // -------------------

    // 3 - Update Main Fragment design

    private Boolean updateMainFragment(Integer integer){
        Fragment newFragment = new Fragment();
        switch (integer) {
            case R.id.action_maps_view:
                newFragment = MapsFragment.newInstance(results);
                break;
            case R.id.action_list_view:
                newFragment = ListFragment.newInstance(results);
                break;
            case R.id.action_workmates:
                newFragment = WorkmatesFragment.newInstance();
                break;
        }
        getFragmentManagerToLaunch(newFragment);
        return true;
    }

    //---------------------------------------
    // CONFIGURATION FRAGMENTS AND CALLBACKS
    //---------------------------------------

    private void getFragmentManagerToLaunch(Fragment newFragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.activity_home_frame_layout, newFragment);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void callbackArticle() {

    }

    @Override
    public void callbackList(Result result) {
        Log.e("test result click", "result returned on homeActivity!");
    }
}
