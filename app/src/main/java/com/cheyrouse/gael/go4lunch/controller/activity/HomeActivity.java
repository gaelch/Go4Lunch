package com.cheyrouse.gael.go4lunch.controller.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.controller.fragment.SettingsFragment;
import com.cheyrouse.gael.go4lunch.models.Prediction;
import com.cheyrouse.gael.go4lunch.models.Predictions;
import com.cheyrouse.gael.go4lunch.services.GPSTracker;
import com.cheyrouse.gael.go4lunch.utils.Go4LunchStream;
import com.cheyrouse.gael.go4lunch.utils.ListUtils;
import com.cheyrouse.gael.go4lunch.utils.Prefs;
import com.cheyrouse.gael.go4lunch.utils.RestaurantHelper;
import com.cheyrouse.gael.go4lunch.utils.StringHelper;
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
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

import static com.cheyrouse.gael.go4lunch.utils.Constants.LINE_BREAK;
import static com.cheyrouse.gael.go4lunch.utils.Constants.SIGN_OUT_TASK;
import static com.facebook.login.widget.ProfilePictureView.TAG;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        MapsFragment.MapsFragmentListener, ListFragment.ListFragmentListener, WorkmatesFragment.WorkMateFragmentListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_home_bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.search_view)
    android.support.v7.widget.SearchView searchView;


    private DrawerLayout drawerLayout;
    private String choice = null;
    private List<Result> results;
    private User user;
    private List<User> users;
    private List<Restaurant> restaurantList;
    private List<ResultDetail> resultDetailList;
    private ResultDetail resultDetail;
    private String location;
    private List<Prediction> resultsPredictions = null;
    private Prefs prefs;
    private MapsFragment mapsFragment;
    String choiceUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        configureToolbar();
        getPrefs();
        getUsersInDatabase(0);
        configureDrawerLayout();
        configureNavigationView();
        configureBottomView();
        gpsGetLocation();
        handleIntent(getIntent());
    }

    // Get user in prefs
    private void getPrefs() {
        prefs = Prefs.get(this);
        user = prefs.getPrefsUser();

    }

    // Get current user location
    private void gpsGetLocation() {
        GPSTracker gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            Log.e("testGpsTrue", "true");
            double latitude = gps.getLatitude(); // returns latitude
            double longitude = gps.getLongitude(); // returns longitude
            location = StringHelper.convertInString(latitude, longitude);
            executeRequestToPlaceApi(location);
        }
    }

    // Request to Place API to find restaurant list
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
                getRestaurantsFromDataBase(results);
            }
        });
    }

    // request to Place Detail API to build list of restaurants
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
                    prefs.storeListResults(resultDetailList);
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
        MenuItem item = menu.findItem(R.id.menu_activity_home_search);
        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        searchView = new android.support.v7.widget.SearchView(Objects.requireNonNull(this.getSupportActionBar()).getThemedContext());
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setActionView(searchView);
        searchView.setQueryHint(getResources().getString(R.string.search_view));
        searchView.setSearchableInfo(Objects.requireNonNull(searchManager).getSearchableInfo(this.getComponentName()));
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery(newText);
                return false;
            }
        });
        searchView.setOnCloseListener(() -> {
            Log.e("test close searchView", "close");
            hideSoftKeyboard(HomeActivity.this);
            return true;
        });
        return true;
    }

    // Intent to search with voice
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    //handle intent to search with voice
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchQuery(query);
        }
    }

    // request results of searchView in Place Autocomplete API
    private void searchQuery(String query) {
        if (query.length() > 2) {
            Disposable disposable = Go4LunchStream.getPlacesAutoComplete(query, location, 5500, getString(R.string.google_maps_key)).subscribeWith(new DisposableObserver<Predictions>() {
                @Override
                public void onNext(Predictions predictions) {
                    resultsPredictions = predictions.getPredictions();
                    Log.e("test result search", String.valueOf(resultsPredictions.size()));
                }

                @Override
                public void onError(Throwable e) {
                    e.getMessage();
                }

                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onComplete() {
                    updateUI();
                }
            });
        }
    }

    // Update view with results of Place Autocomplete
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateUI() {
        List<ResultDetail> newResults = ListUtils.transforPredictionToResultDetail(resultDetailList, resultsPredictions);
        if (newResults.size() > 0) {
            FragmentManager fm = getSupportFragmentManager();
            List<Fragment> fragments = fm.getFragments();
            Fragment newFragment = fragments.get(fragments.size() - 1);
            if (newFragment instanceof MapsFragment) {
                newFragment = MapsFragment.newInstance(newResults, restaurantList);
                mapsFragment = (MapsFragment) newFragment;
                getFragmentManagerToLaunch(newFragment);
            }
            if (newFragment instanceof ListFragment) {
                newFragment = ListFragment.newInstance(newResults, users);
                getFragmentManagerToLaunch(newFragment);
            }
        }
    }

    // To search
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //3 - Handle actions on menu items
        if (item.getItemId() == R.id.menu_activity_home_search) {
            searchView.setVisibility(View.VISIBLE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Configuration NavigationView of Drawer Menu
    @SuppressLint("SetTextI18n")
    private void configureNavigationView() {
        NavigationView navigationView = findViewById(R.id.activity_home_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        TextView nav_user = hView.findViewById(R.id.text_login);
        ImageView imageView = hView.findViewById(R.id.imageViewNavDraw);
        if (user != null) {
            nav_user.setText(user.getUsername() + LINE_BREAK + user.geteMail());
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
    }


    //Configure Drawer layout
    private void configureDrawerLayout() {
        this.drawerLayout = findViewById(R.id.activity_home_drawer_layout);
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
                getUsersInDatabase(3);
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

    // Launch Notification Fragment
    private void showNotificationsSettings() {
        Fragment newFragment = SettingsFragment.newInstance();
        getFragmentManagerToLaunch(newFragment);
    }

    // SignOut from Firebase
    private void signOutUserFromFirebase() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted());

    }

    // SignOut from Firebase
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
        FragmentManager fm = getSupportFragmentManager();
        List<Fragment> fragments = fm.getFragments();
        Fragment newFragment = fragments.get(fragments.size() - 1);
        if (newFragment instanceof MapsFragment) {
            finish();
        }
        getBackStackToRefreshData(newFragment, fragments);
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }
    }

    // Get FragmentManager to refresh data in fragments
    private void getBackStackToRefreshData(Fragment newFragment, List<Fragment> fragments) {
        int frag = ListUtils.getBackStackToRefreshData(newFragment, fragments);
        if (frag == 1) {
            getRestaurantsFromDataBase(results);
        }
        if (frag == 2) {
            getUsersInDatabase(1);
        }
        if (frag == 3) {
            getUsersInDatabase(2);
        }
    }

    // Configure BottomNavigationView Listener
    private void configureBottomView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> updateMainFragment(item.getItemId()));
    }

    // Get users in database to make list or refresh list
    private void getUsersInDatabase(int i) {
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
                        if(user != null && user.getUsername() != null) {
                            if (userToAdd.getUsername().equals(user.getUsername())) {
                                choiceUser = userToAdd.getChoice();
                            }
                        }
                    }
                    if (i == 1) {
                        getFragmentManagerToLaunch(ListFragment.newInstance(resultDetailList, users));
                    }
                    if (i == 2) {
                        getFragmentManagerToLaunch(WorkmatesFragment.newInstance(users));
                    }
                    if (i == 3) {
                        if (choiceUser != null && !choiceUser.isEmpty()) {
                            getRestaurantIfExist(choiceUser);
                        } else {
                            Toast.makeText(HomeActivity.this, getResources().getString(R.string.not_choice_today), Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
                Log.e("listMates", String.valueOf(users.size()));
            }
        });
    }

    // Get restaurant choice if exist
    private void getRestaurantIfExist(String choice) {
        ResultDetail resultDet = new ResultDetail();
        resultDet = ListUtils.getRestaurantToDisplayYourChoiceIfExist(resultDetailList, choice);
        showDetailRestaurantFragment(resultDet, users, user);
    }

    // Get restaurants from database to make list
    private void getRestaurantsFromDataBase(List<Result> results) {
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
                        restaurantList = ListUtils.makeListResultRestaurantWithResultList(results, restaurants);
                    } else {
                        restaurantList = ListUtils.makeListResultRestaurantWithResultDetailList(resultDetailList, restaurants);
                    }

                    if (results != null) {
                        mapsFragment = MapsFragment.newInstance(resultDetailList, restaurantList);
                        getFragmentManagerToLaunch(mapsFragment);
                    } else {
                        showDetailRestaurantFragment(resultDetail, users, user);
                        hideSoftKeyboard(HomeActivity.this);
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

    // Update Fragments
    private Boolean updateMainFragment(Integer integer) {
        switch (integer) {
            case R.id.action_maps_view:
                getRestaurantsFromDataBase(results);
                break;
            case R.id.action_list_view:
                getUsersInDatabase(1);
                break;
            case R.id.action_workmates:
                getUsersInDatabase(2);
                break;
        }
        return true;
    }

    public void recreate() {
        if (resultDetailList != null) {
            this.recreate();
        }
    }

    //---------------------------------------
    // CONFIGURATION FRAGMENTS AND CALLBACKS
    //---------------------------------------

    //Get fragment Manager to launch and add fragments to backStack
    private void getFragmentManagerToLaunch(Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (newFragment instanceof RestauDetailFragment) {
            hideToolBarAndBottomBar();
        }
        transaction.add(R.id.activity_home_frame_layout, newFragment, String.valueOf(newFragment));
        transaction.addToBackStack(String.valueOf(newFragment));
        transaction.commit();
    }

    // Hide toolbar
    private void hideToolBarAndBottomBar() {
        toolbar.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.GONE);
    }

    // Callback of ListFragment if restaurant is clicked
    @Override
    public void callbackList(ResultDetail result) {
        showDetailRestaurantFragment(result, users, user);
        Log.e("test result click", "result returned on homeActivity!");
    }

    // Show RestaurantDetailFragment
    private void showDetailRestaurantFragment(ResultDetail result, List<User> users, User user) {
        Fragment newFragment = RestauDetailFragment.newInstance(result, users, user, restaurantList);
        getFragmentManagerToLaunch(newFragment);
    }

    // Callback to MapsFragment if marker is clicked
    @Override
    public void callbackMaps(ResultDetail result) {
        showDetailRestaurantFragment(result, users, user);
    }

    // Hide keyboard
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(
                Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
    }

    // Callback of MatesFragment if workMate is clicked
    @Override
    public void callbackMates(User user) {
        getRestaurantInListResultDetails(user);
    }

    // To find restaurant in the list
    private void getRestaurantInListResultDetails(User user) {
        for (ResultDetail r : resultDetailList) {
            if (r.getName().equals(user.getChoice())) {
                showDetailRestaurantFragment(r, users, user);
            }
        }
    }
}
