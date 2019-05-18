package com.cheyrouse.gael.go4lunch.controller.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.controller.fragment.ListFragment;
import com.cheyrouse.gael.go4lunch.controller.fragment.MapsFragment;
import com.cheyrouse.gael.go4lunch.controller.fragment.WorkmatesFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.activity_home_bottom_navigation) BottomNavigationView bottomNavigationView;

    private DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        configureToolbar();
        configureDrawerLayout();
        configureNavigationView();
        configureBottomView();
        configureAndShowFragmentMaps();
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
                Intent logoutIntent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(logoutIntent);
                break;
            default:
                break;
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
                return HomeActivity.this.updateMainFragment(item.getItemId());
            }
        });
    }

    // -------------------
    // UI
    // -------------------

    // 3 - Update Main Fragment design
    private Boolean updateMainFragment(Integer integer){
        switch (integer) {
            case R.id.action_maps_view:
               configureAndShowFragmentMaps();
                break;
            case R.id.action_list_view:
                configureAndShowFragmentList();
                break;
            case R.id.action_workmates:
                configureAndShowFragmentWorkmates();
                break;
        }
        return true;
    }

    //---------------------------------------
    // CONFIGURATION FRAGMENTS AND CALLBACKS
    //---------------------------------------

    private void configureAndShowFragmentMaps() {
        MapsFragment mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.activity_home_frame_layout);
        mapsFragment = new MapsFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_home_frame_layout, mapsFragment)
                .addToBackStack(String.valueOf(mapsFragment))
                .commit();
    }

    private void configureAndShowFragmentList() {
        ListFragment listFragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.activity_home_frame_layout);
        listFragment = new ListFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_home_frame_layout, listFragment)
                .addToBackStack(String.valueOf(listFragment))
                .commit();
    }

    private void configureAndShowFragmentWorkmates() {
        WorkmatesFragment workmatesFragment = (WorkmatesFragment) getSupportFragmentManager().findFragmentById(R.id.activity_home_frame_layout);
        workmatesFragment = new WorkmatesFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_home_frame_layout, workmatesFragment)
                .addToBackStack(String.valueOf(workmatesFragment))
                .commit();
    }
}
