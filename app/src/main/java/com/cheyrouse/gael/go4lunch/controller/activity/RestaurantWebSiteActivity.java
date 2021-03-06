package com.cheyrouse.gael.go4lunch.controller.activity;

import android.annotation.SuppressLint;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.cheyrouse.gael.go4lunch.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import static com.cheyrouse.gael.go4lunch.controller.fragment.RestauDetailFragment.WEB_SITE_EXTRA;

public class RestaurantWebSiteActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.webview)
    WebView webView;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_web_site);
        ButterKnife.bind(this);

        String url = getIntent().getStringExtra(WEB_SITE_EXTRA);

        onConfigureWebView();

        updateUI(url);

        ConfigureToolbar();

        onPageFinished();

    }

    // --------------------
    // TOOLBAR
    // --------------------

    //Set the toolbar
    private void ConfigureToolbar() {
        // Set the Toolbar
        setSupportActionBar(toolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
    }

    //To configure WebView
    @SuppressLint("SetJavaScriptEnabled")
    private void onConfigureWebView() {
        //Configure the webView for the use of javascript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        // Allows opening of windows
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //Allow storage DOM (Document Object Model)
        webSettings.setDomStorageEnabled(true);
    }

    // Function that allows the display of the page when everything is loaded
    public void onPageFinished() {
        webView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    // -------------------
    // UPDATE UI
    // -------------------

    //Show WebView
    protected void updateUI(String url) {
        webView.loadUrl(url);
    }
}
