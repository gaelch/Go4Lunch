package com.cheyrouse.gael.go4lunch.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.controller.fragment.SettingsFragment;
import com.cheyrouse.gael.go4lunch.models.Prediction;
import com.cheyrouse.gael.go4lunch.models.Predictions;
import com.cheyrouse.gael.go4lunch.utils.GPSTracker;
import com.cheyrouse.gael.go4lunch.utils.Go4LunchStream;
import com.cheyrouse.gael.go4lunch.utils.Prefs;
import com.cheyrouse.gael.go4lunch.utils.RestaurantHelper;
import com.cheyrouse.gael.go4lunch.utils.UserHelper;
import com.cheyrouse.gael.go4lunch.controller.fragment.ListFragment;
import com.cheyrouse.gael.go4lunch.controller.fragment.MapsFragment;
import com.cheyrouse.gael.go4lunch.controller.fragment.RestauDetailFragment;
import com.cheyrouse.gael.go4lunch.controller.fragment.WorkmatesFragment;
import com.cheyrouse.gael.go4lunch.models.Place;
import com.cheyrouse.gael.go4lunch.models.PlaceDetails;
import com.cheyrouse.gael.go4lunch.models.Restaurant;
import com.cheyrouse.gael.go4lunch.models.Result;
import com.cheyrouse.gael.go4lunch.models.ResultDetail;
import com.cheyrouse.gael.go4lunch.models.User;
import com.cheyrouse.gael.go4lunch.views.PlacesAutoCompleteAdapter;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

import static com.cheyrouse.gael.go4lunch.utils.Constants.SIGN_OUT_TASK;
import static com.facebook.login.widget.ProfilePictureView.TAG;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        MapsFragment.MapsFragmentListener, ListFragment.ListFragmentListener,
        PlacesAutoCompleteAdapter.onTextViewAdapterListener, WorkmatesFragment.WorkMateFragmentListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_home_bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.searchView)
    AutoCompleteTextView searchView;

    private DrawerLayout drawerLayout;
    private String choice = null;
    private double latitude;
    private double longitude;
    private List<Result> results;
    private User user;
    private List<User> users;
    private List<Restaurant> restaurantList;
    private List<ResultDetail> resultDetailList;
    private List<ResultDetail> resultDetails;
    private ResultDetail resultDetail;
    private ResultDetail restaurantChoice;
    private Predictions predictionList;
    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        configureToolbar();
        getPrefs();
        getUsersInDatabase();
        configureDrawerLayout();
        configureNavigationView();
        configureBottomView();
        gpsGetLocation();
    }

    private void getPrefs() {
        Prefs prefs = Prefs.get(this);
        user = prefs.getPrefsUser();
        restaurantChoice = prefs.getChoice();
    }

    private void gpsGetLocation() {
        GPSTracker gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            Log.e("testGpsTrue", "true");
            latitude = gps.getLatitude(); // returns latitude
            longitude = gps.getLongitude(); // returns longitude
            convertInString(latitude, longitude);
        }
    }

    private void convertInString(double latitude, double longitude) {
        location = String.valueOf(latitude) + "," + String.valueOf(longitude);
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
                buildListPlaceDetail();
                getRestaurantsFromDataBase(results, null);
            }
        });
    }

    private void buildListPlaceDetail() {
        resultDetailList = new ArrayList<>();
        for (Result result : results) {
            Disposable disposable = Go4LunchStream.streamFetchRestaurantsDetails(result.getPlaceId()).subscribeWith(new DisposableObserver<PlaceDetails>() {
                @Override
                public void onNext(PlaceDetails detailsResults) {
                    resultDetail = detailsResults.getResult();
                    Log.e("testResponseDetail", String.valueOf(detailsResults));
                }

                @Override
                public void onError(Throwable e) {
                    Log.e("errorRequestPlaceDetail", e.getMessage());
                }

                @Override
                public void onComplete() {
                    resultDetailList.add(resultDetail);
                    Log.e("Test", "detail is charged");
                }
            });
        }
    }

    // To set he toolbar
    private void configureToolbar() {
        // Set the Toolbar
        toolbar.setTitle(R.string.title_toolbar);
        setSupportActionBar(toolbar);
    }

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
                searchView.setVisibility(View.VISIBLE);
                loadData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadData() {
        List<Prediction> predictions = new ArrayList<>();
        PlacesAutoCompleteAdapter placesAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this, predictions, location, this);
        searchView.setThreshold(1);
        searchView.setAdapter(placesAutoCompleteAdapter);
        searchView.setHint(R.string.search_view);
    }


    //Configuration NavigationView of Drawer Menu
    private void configureNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.activity_home_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        TextView nav_user = (TextView) hView.findViewById(R.id.text_login);
        ImageView imageView = (ImageView) hView.findViewById(R.id.imageViewNavDraw);
        nav_user.setText(user.getUsername() + "\n" + user.geteMail());
        if (user.getUrlPicture() != null) {
            Glide.with(this)
                    .load(user.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(imageView);
        } else {
            Prefs prefs = Prefs.get(this);
            Uri selectedImage = prefs.getPicture(user.getUsername());
            if (selectedImage != null) {
                Picasso.get().load(selectedImage).into((ImageView) hView.findViewById(R.id.imageViewNavDraw));
            } else {
                imageView.setBackgroundColor(this.getResources().getColor(R.color.green));
                Glide.with(this).load(imageView.getResources().getDrawable(R.drawable.baseline_perm_identity_black_18dp)).apply(RequestOptions.circleCropTransform()).into(imageView);
            }
        }
    }


    //Configure Drawer layout
    private void configureDrawerLayout() {
        this.drawerLayout = (DrawerLayout) findViewById(R.id.activity_home_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.recreate();

    }

    //Switch to menu Drawer items
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // 4 - Handle Navigation Item Click
        int id = item.getItemId();
        item.getTitle();
        switch (id) {
            case R.id.activity_main_drawer_lunch:
                if (restaurantChoice != null) {
                    showDetailRestaurantFragment(restaurantChoice, users, user);
                } else {
                    Toast.makeText(this, "sorry but you did not choose a restaurant today", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.activity_main_drawer_settings:
                showNotificationsSettings();
                break;
            case R.id.activity_main_drawer_logout:
                signOutUserFromFirebase();
                break;
            default:
                break;
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showNotificationsSettings() {
        Fragment newFragment = SettingsFragment.newInstance();
        getFragmentManagerToLaunch(newFragment);
    }

    private void signOutUserFromFirebase() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted());

    }

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted() {
        return aVoid -> {
            switch (SIGN_OUT_TASK) {
                case SIGN_OUT_TASK:
                    finish();
                    Intent logoutIntent = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(logoutIntent);
                    break;
                default:
                    break;
            }
        };
    }

    //To manage drawerLayout on back button
    @Override
    public void onBackPressed() {
        toolbar.setVisibility(View.VISIBLE);
        bottomNavigationView.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.GONE);
        // 5 - Handle back click to close menu
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();
        else
            finish();    // Finish the activity
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            // this.recreate();
        }
    }

    // 2 - Configure BottomNavigationView Listener
    private void configureBottomView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> updateMainFragment(item.getItemId()));
    }

    private void getUsersInDatabase() {
        UserHelper.getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    users = new ArrayList<>();
                    String urlPicture = null;
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        String uid = document.getData().get("uid").toString();
                        String username = document.getData().get("username").toString();
                        if (document.getData().get("urlPicture") != null) {
                            urlPicture = document.getData().get("urlPicture").toString();
                        } else {
                            urlPicture = null;
                        }
                        if (document.getData().get("choice") != null) {
                            choice = document.getData().get("choice").toString();
                        } else {
                            choice = null;
                        }
                        User userToAdd = new User(uid, username, urlPicture);
                        userToAdd.setChoice(choice);
                        users.add(userToAdd);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
                Log.e("listMates", String.valueOf(users.size()));
            }
        });
    }

    private void getRestaurantsFromDataBase(List<Result> results, List<ResultDetail> resultDetails) {
        restaurantList = new ArrayList<>();
        RestaurantHelper.getRestaurantsCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Restaurant> restaurants = new ArrayList<>();
                    for (DocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                        Restaurant r = doc.toObject(Restaurant.class);
                        Objects.requireNonNull(r).setRestaurantUid(doc.getId());
                        r.setRestaurantName(doc.getString("restaurantName"));
                        r.setRate((List<String>) doc.get("rate"));
                        r.setUsers((List<String>) doc.get("users"));
                        if (doc.getDouble("lat") != null) {
                            r.setLat(doc.getDouble("lat"));
                        } else {
                            r.setLat(0);
                        }
                        if (doc.getDouble("lng") != null) {
                            r.setLng(doc.getDouble("lng"));
                        } else {
                            r.setLng(0);
                        }
                        restaurants.add(r);
                    }
                    if (results != null) {
                        for (Restaurant restaurant : restaurants) {
                            for (Result r : results) {
                                if (restaurant.getRestaurantName().equals(r.getName())) {
                                    restaurantList.add(restaurant);
                                }
                            }
                            Log.e("restaurantsListSize", String.valueOf(restaurantList.size()));
                        }
                    } else {
                        for (Restaurant restaurant : restaurants) {
                            for (ResultDetail r : resultDetails) {
                                if (restaurant.getRestaurantName().equals(r.getName())) {
                                    restaurantList.add(restaurant);
                                }
                            }
                            Log.e("restaurantsListSize", String.valueOf(restaurantList.size()));
                        }
                    }

                    if (results != null) {
                        getFragmentManagerToLaunch(MapsFragment.newInstance(resultDetailList, restaurantList));
                    } else {
                        showDetailRestaurantFragment(resultDetail, users, user);
                        hideSoftKeyboard(HomeActivity.this);
                        hideAutoCompleteTextView();
                    }
                    //do something with list of pojos retrieved

                } else {
                    Log.e("error", "Error getting documents: ", task.getException());
                }
            }
        });
    }


    // -------------------
    // UI
    // -------------------

    // 3 - Update Main Fragment design

    private Boolean updateMainFragment(Integer integer) {
        Fragment newFragment = new Fragment();
        switch (integer) {
            case R.id.action_maps_view:
                if (resultDetails != null) {
                    this.recreate();
                }
                newFragment = MapsFragment.newInstance(resultDetailList, restaurantList);
                break;
            case R.id.action_list_view:
                newFragment = ListFragment.newInstance(resultDetailList, users);
                break;
            case R.id.action_workmates:
                newFragment = WorkmatesFragment.newInstance(users);
                break;
        }
        getFragmentManagerToLaunch(newFragment);
        return true;
    }

    //---------------------------------------
    // CONFIGURATION FRAGMENTS AND CALLBACKS
    //---------------------------------------

    private void getFragmentManagerToLaunch(Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (newFragment instanceof RestauDetailFragment) {
            hideToolBarAndBottomBar();
        }
        transaction.replace(R.id.activity_home_frame_layout, newFragment);
        transaction.addToBackStack(String.valueOf(newFragment));
        transaction.commit();
    }

    private void hideToolBarAndBottomBar() {
        toolbar.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.GONE);
    }

    @Override
    public void callbackList(ResultDetail result) {
        showDetailRestaurantFragment(result, users, user);
        Log.e("test result click", "result returned on homeActivity!");
    }

    private void showDetailRestaurantFragment(ResultDetail result, List<User> users, User user) {
        Fragment newFragment = RestauDetailFragment.newInstance(result, users, user, restaurantList);
        getFragmentManagerToLaunch(newFragment);
    }

    @Override
    public void callbackMaps(ResultDetail result) {
        showDetailRestaurantFragment(result, users, user);
    }

    @Override
    public void onRestaurantClicked(Prediction prediction) {
        searchView.setText(prediction.getDescription());
        Log.e("prediction in home", prediction.getDescription());
        getPredictionsAndPassDataToDetailFragment(prediction);
    }

    private void getPredictionsAndPassDataToDetailFragment(Prediction prediction) {
        List<ResultDetail> resultDetails = new ArrayList<>();
        Disposable disposable = Go4LunchStream.streamFetchRestaurantsDetails(prediction.getPlaceId()).subscribeWith(new DisposableObserver<PlaceDetails>() {
            @Override
            public void onNext(PlaceDetails detailsResults) {
                resultDetail = detailsResults.getResult();
                Log.e("testResponseDetail", String.valueOf(detailsResults));
            }

            @Override
            public void onError(Throwable e) {
                Log.e("errorRequestPlaceDetail", e.getMessage());
            }

            @Override
            public void onComplete() {
                resultDetails.add(resultDetail);
                getRestaurantsFromDataBase(null, resultDetails);
            }
        });
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(
                Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
    }

    public void hideAutoCompleteTextView() {
        searchView.setText("");
        searchView.setVisibility(View.GONE);
    }

    @Override
    public void callbackMates(User user) {
        getRestaurantInListResultDetails(user);
    }

    private void getRestaurantInListResultDetails(User user) {
        for (ResultDetail r : resultDetailList){
            if(r.getName().equals(user.getChoice())){
                showDetailRestaurantFragment(r, users, user);
            }
        }
    }
}
