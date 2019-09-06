package com.cheyrouse.gael.go4lunch.controller.fragment;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.utils.ListUtils;
import com.cheyrouse.gael.go4lunch.utils.Prefs;
import com.cheyrouse.gael.go4lunch.utils.RestaurantHelper;
import com.cheyrouse.gael.go4lunch.utils.UserHelper;
import com.cheyrouse.gael.go4lunch.utils.StarsUtils;
import com.cheyrouse.gael.go4lunch.controller.activity.RestaurantWebSiteActivity;
import com.cheyrouse.gael.go4lunch.models.Restaurant;
import com.cheyrouse.gael.go4lunch.models.ResultDetail;
import com.cheyrouse.gael.go4lunch.models.User;
import com.cheyrouse.gael.go4lunch.views.WorkMatesAdapter;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.security.interfaces.RSAKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.constraint.Constraints.TAG;
import static com.cheyrouse.gael.go4lunch.utils.Constants.API_KEY;
import static com.cheyrouse.gael.go4lunch.utils.Constants.BASE_URL;
import static com.cheyrouse.gael.go4lunch.utils.Constants.MAX_HEIGHT;
import static com.cheyrouse.gael.go4lunch.utils.Constants.MAX_WIDTH;
import static com.cheyrouse.gael.go4lunch.utils.Constants.RESTAURANTS;
import static com.cheyrouse.gael.go4lunch.utils.Constants.RESULT;
import static com.cheyrouse.gael.go4lunch.utils.Constants.USER;
import static com.cheyrouse.gael.go4lunch.utils.Constants.USERS;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestauDetailFragment extends Fragment implements FloatingActionButton.OnClickListener {


    public static final String WEB_SITE_EXTRA = "web_site";
    @BindView(R.id.image_restaurant)
    ImageView imageViewRestaurant;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.tv_restaurant_name)
    TextView tvRestaurantName;
    @BindView(R.id.tv_restaurant_address)
    TextView tvRestaurantAddress;
    @BindView(R.id.rate_star1)
    ImageView imageViewRate1;
    @BindView(R.id.rate_star2)
    ImageView imageViewRate2;
    @BindView(R.id.rate_star3)
    ImageView imageViewRate3;
    @BindView(R.id.bottomNavigationDetailView)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.recycler_view_detail)
    RecyclerView recyclerView;
    @BindView(R.id.fragment_restau_swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    private List<User> users;
    private List<User> usersAreJoining;
    private boolean var = false;
    private User user;
    //private User userDatabase;
    private String choice;
    private Restaurant restaurant;
    private ResultDetail resultDetail;
    private List<Restaurant> restaurantList;
    private boolean isFound = false;


    public static RestauDetailFragment newInstance(ResultDetail result, List<User> users, User user, List<Restaurant> restaurants) {
        // Create new fragment
        RestauDetailFragment frag = new RestauDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(RESULT, (Serializable) result);
        bundle.putSerializable(USERS, (Serializable) users);
        bundle.putSerializable(USER, (Serializable) user);
        bundle.putSerializable(RESTAURANTS, (Serializable) restaurants);
        frag.setArguments(bundle);
        return frag;
    }

    public RestauDetailFragment() {
        // Required empty public constructor
    }


    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restau_detail, container, false);
        ButterKnife.bind(this, view);
        getTheBundleData();
        getUsersInDatabase();
        getRestaurantFromFirestore();
        setTransparentStatusBar();
        setImages();
        configureTextView();
        configureRecyclerView();
        configureBottomView();
        configureTextView();
        configureSwipeRefreshLayout();
        return view;
    }

    // Get restaurant from Firebase database
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressWarnings("unchecked")
    private void getRestaurantFromFirestore() {
        RestaurantHelper.getRestaurant(resultDetail.getName()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                restaurant = new Restaurant();
                restaurant.setRestaurantName((String) Objects.requireNonNull(task.getResult()).get("restaurantName"));
                restaurant.setRate((List<String>) task.getResult().get("rate"));
                restaurant.setUsers((List<String>) task.getResult().get("users"));
                setStars();
            }
        }).addOnFailureListener(e -> Log.e("fail", e.getMessage()));
    }

    // FAB configuration
    private void configureFab() {
        floatingActionButton.setOnClickListener(this);
        if (resultDetail.getName().equals(choice)) {
            floatingActionButton.setColorFilter(getResources().getColor(R.color.green));
        } else {
            floatingActionButton.setColorFilter(getResources().getColor(R.color.grey));
        }
    }

    // Display image
    private void setImages() {
        if (!(resultDetail.getPhotos() == null)) {
            if (!(resultDetail.getPhotos().isEmpty())) {
                // Photo restaurant
                Glide.with(this)
                        .load(BASE_URL + "?maxwidth=" + MAX_WIDTH + "&maxheight=" + MAX_HEIGHT + "&photoreference=" + resultDetail
                                .getPhotos().get(0).getPhotoReference() + "&key=" + API_KEY).into(imageViewRestaurant);
            }
        } else {
            Glide.with(this).load(resultDetail.getIcon()).apply(RequestOptions.centerCropTransform()).into(imageViewRestaurant);
        }
    }

    // transparent status bar

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setTransparentStatusBar() {
        Objects.requireNonNull(getActivity()).getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    // Get data in bundle
    @SuppressWarnings("unchecked")
    private void getTheBundleData() {
        assert getArguments() != null;
        resultDetail = (ResultDetail) getArguments().getSerializable(RESULT);
        users = (List<User>) getArguments().getSerializable(USERS);
        user = (User) getArguments().getSerializable(USER);
        restaurantList = (List<Restaurant>) getArguments().getSerializable(RESTAURANTS);
        getRestaurantJoiningUsers();
    }

    // find users are joining
    private void getRestaurantJoiningUsers() {
        usersAreJoining = new ArrayList<>();
        for (User user : users) {
            if (user.getChoice() != null) {
                if (user.getChoice().equals(resultDetail.getName())) {
                    usersAreJoining.add(user);
                }
            }
            if (user.getUid().equals(this.user.getUid())) {
                choice = user.getChoice();
            }
        }
    }

    // textView configuration
    private void configureTextView() {
        tvRestaurantName.setText(resultDetail.getName());
        tvRestaurantAddress.setText(resultDetail.getFormattedAddress());
    }

    //configure recyclerView and Tabs
    private void configureRecyclerView() {
        // Create adapter passing in the sample user data
        WorkMatesAdapter adapter = new WorkMatesAdapter(getActivity(), usersAreJoining, Glide.with(this), null, 1);
        // Attach the adapter to the recyclerView to populate items
        this.recyclerView.setAdapter(adapter);
        // Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    //Configure SwipeRefreshLayout
    private void configureSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            getUsersInDatabase();
            configureRecyclerView();
        });
    }

    // Configure BottomNavigationView Listener
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void configureBottomView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> getBottomChoice(item.getItemId()));
        bottomNavigationView.getMenu().getItem(0).setCheckable(false);
    }

    // Bottom bar Buttons
    @RequiresApi(api = Build.VERSION_CODES.N)
    private Boolean getBottomChoice(Integer integer) {
        switch (integer) {
            case R.id.action_phone:
                if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    //Creating intents for making a call
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + resultDetail.getFormattedPhoneNumber()));
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.not_phone_permission), Toast.LENGTH_SHORT).show();
                    showSettingsAlert();
                }
                break;
            case R.id.action_like:
                String userId = user.getUid();
                updateTableRestaurants(userId, user.getUsername(), resultDetail.getName(), 1);
                break;
            case R.id.action_website:
                if (resultDetail.getWebsite() != null) {
                    Intent detailActivityIntent = new Intent(getActivity(), RestaurantWebSiteActivity.class);
                    detailActivityIntent.putExtra(WEB_SITE_EXTRA, resultDetail.getWebsite());
                    startActivity(detailActivityIntent);
                } else {
                    showBoxInfo();
                }
                break;
        }
        return true;
    }

    // Display dialog if restaurant doesn't have url
    private void showBoxInfo() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(getResources().getString(R.string.no_url_found));
        alertDialog.setMessage(getResources().getString(R.string.no_website));
        alertDialog.setPositiveButton("ok", (dialog, which) -> {
        });
        // Showing Alert Message
        alertDialog.show();
    }

    // To display stars
    private void setStars() {
        StarsUtils.setStars(getActivity(), imageViewRate1, imageViewRate2, imageViewRate3, users, restaurant);
    }

    // If GPS is disable
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        // Setting Dialog Title
        alertDialog.setTitle(getResources().getString(R.string.GPS_is_settings));
        // Setting Dialog Message
        alertDialog.setMessage(getResources().getString(R.string.settings_menu));
        // On pressing Settings button
        alertDialog.setPositiveButton(getResources().getString(R.string.settings), (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", "com.cheyrouse.gael.Go4lunch", null));
            startActivity(intent);
        });
        // on pressing cancel button
        alertDialog.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> dialog.cancel());
        // Showing Alert Message
        alertDialog.show();
    }

    // FAB onCLick
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        Prefs prefs = Prefs.get(getActivity());
        if (v.getId() == R.id.fab) {
            if (!var) {
                var = true;
                floatingActionButton.setColorFilter(getResources().getColor(R.color.green));
                if (choice != null && choice.length() != 0) {
                    deleteChoice(user.getUsername(), choice);
                }
                //set user table restaurant et set restaurant table users
                user.setChoice(resultDetail.getName());
                prefs.storeChoicePrefs(resultDetail);
                prefs.storeUserPrefs(user);
                updateTableUsers();
                updateTableRestaurants("", user.getUsername(), resultDetail.getName(), 0);
                getUsersInDatabase();
            } else {
                var = false;
                if (choice != null) {
                    deleteChoice(user.getUsername(), choice);
                }
                floatingActionButton.setColorFilter(getResources().getColor(R.color.grey));
                user.setChoice("");
                prefs.storeChoicePrefs(null);
                prefs.storeUserPrefs(user);
                updateTableUsers();
                getUsersInDatabase();
            }
        }
    }

    // Get users in Firebase database
    private void getUsersInDatabase() {
        UserHelper.getUsersCollection().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                users = new ArrayList<>();
                String urlPicture = null;
                String choice = null;
                String eMail = null;
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    String uid = document.getData().get("uid").toString();
                    String username = document.getData().get("username").toString();
                    if (document.getData().get("eMail") != null) {
                        eMail = document.getData().get("eMail").toString();
                    }
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
                    User userToAdd = new User(uid, username, urlPicture, eMail);
                    userToAdd.setChoice(choice);
                    users.add(userToAdd);
                    getRestaurantJoiningUsers();
                    configureRecyclerView();
                    configureFab();
                }
            } else {
                Log.d(ProfilePictureView.TAG, "Error getting documents: ", task.getException());
            }
            Log.e("listMates", String.valueOf(users.size()));
        });
    }

    // Refresh restaurant table in Firebase
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateTableRestaurants(String userId, String userName, String choice, int i) {
        for (Restaurant r : restaurantList) {
            if (r.getRestaurantName().equals(restaurant.getRestaurantName())) {
                isFound = true;
                List<String> rateList = restaurant.getRate();
                if (rateList != null && rateList.size() != 0) {
                    boolean found = ListUtils.findUserInRateList(userId, rateList);
                    if (found) {
                        deleteLike(userId, r.getRestaurantUid());
                    } else {
                        updateChoiceOrLike(i, userName, choice, userId);
                    }
                } else {
                    updateChoiceOrLike(i, userName, choice, userId);
                }
            }
        }
        if (!isFound) {
            createRestaurantAndUpdateIt(i, userName, userId);
        }
        getRestaurantFromFirestore();
        refreshRestaurantList();
    }

    // Refresh UI
    @SuppressWarnings("unchecked")
    private void refreshRestaurantList() {
        restaurantList = new ArrayList<>();
        RestaurantHelper.getRestaurantsCollection().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
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
                    restaurantList.add(r);
                }
            } else {
                Log.e("error", "Error getting documents: ", task.getException());
            }
        });
    }

    // Store restaurant in database if not exist
    private void createRestaurantAndUpdateIt(int i, String userName, String userId) {
        if (i == 0) {
            RestaurantHelper.createRestaurant(resultDetail.getName(), resultDetail.id, resultDetail.name,
                    resultDetail.getGeometry().getLocation().getLat(), resultDetail.getGeometry().getLocation().getLng());
            RestaurantHelper.updateRestaurantChoice(userName, choice);
        } else {
            RestaurantHelper.createRestaurant(resultDetail.getName(), resultDetail.id, resultDetail.name,
                    resultDetail.getGeometry().getLocation().getLat(), resultDetail.getGeometry().getLocation().getLng());
            RestaurantHelper.updateRestaurantRate(userId, resultDetail.getName());
            Toast.makeText(getActivity(), getResources().getString(R.string.liked), Toast.LENGTH_SHORT).show();
        }
    }

    // Update database
    private void updateChoiceOrLike(int i, String userName, String choice, String userId) {
        if (i == 0) {
            RestaurantHelper.updateRestaurantChoice(userName, choice);
        } else {
            RestaurantHelper.updateRestaurantRate(userId, resultDetail.getName());
            Toast.makeText(getActivity(), getResources().getString(R.string.liked), Toast.LENGTH_SHORT).show();
        }
    }

    // delete like in database
    private void deleteLike(String userId, String restaurantUid) {
        RestaurantHelper.deleteRestaurantRate(userId, restaurantUid);
        Toast.makeText(getActivity(),getResources().getString(R.string.unlike), Toast.LENGTH_SHORT).show();
    }


    // delete user restaurant choice
    private void deleteChoice(String userName, String choice) {
        RestaurantHelper.deleteUserChoice(userName, choice);
    }

    // update user restaurant choice
    private void updateTableUsers() {
        UserHelper.updateChoice(user.getChoice(), user.getUid());
    }

}
