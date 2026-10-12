package in.freedailyjobalert.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class MainActivity extends Activity {

    private WebView webView;
    private ProgressBar progressBar;
    private LinearLayout offlineLayout;
    private FrameLayout webLayout;

    private static final String URL_HOME    = "https://freedailyjobalert.in";
    private static final String URL_JOBS    = "https://freedailyjobalert.in/govt-jobs/";
    private static final String URL_RESULTS = "https://freedailyjobalert.in/results/";
    private static final String URL_ALERTS  = "https://t.me/freedailyjobalert";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView       = findViewById(R.id.webview);
        progressBar   = findViewById(R.id.progress_bar);
        offlineLayout = findViewById(R.id.offline_layout);
        webLayout     = findViewById(R.id.web_layout);

        setupWebView();
        setupBottomNav();

        // Determine launch URL
        String launchUrl = URL_HOME;

        // From FCM notification (push notification tap)
        if (getIntent() != null && getIntent().getStringExtra("launch_url") != null) {
            launchUrl = getIntent().getStringExtra("launch_url");
        }
        // From deep link (click on freedailyjobalert.in link)
        else if (getIntent() != null && getIntent().getData() != null) {
            launchUrl = getIntent().getData().toString();
        }

        if (isConnected()) {
            webView.loadUrl(launchUrl);
        } else {
            showOffline();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setSupportZoom(true);
        // Custom UA so your analytics can filter app traffic
        settings.setUserAgentString(settings.getUserAgentString() + " FDJA-AndroidApp/1.0");

        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                // Keep site URLs in WebView
                if (url.startsWith("https://freedailyjobalert.in") ||
                    url.startsWith("http://freedailyjobalert.in")) {
                    return false;
                }
                // Open mailto/tel links with system apps
                if (url.startsWith("mailto:") || url.startsWith("tel:") || url.startsWith("whatsapp:")) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } catch (Exception e) { /* ignore */ }
                    return true;
                }
                // All other external URLs → open in browser
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } catch (Exception e) { /* ignore */ }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
                webLayout.setVisibility(View.VISIBLE);
                offlineLayout.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (!isConnected()) {
                    showOffline();
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                if (newProgress < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        // Retry button
        findViewById(R.id.btn_retry).setOnClickListener(v -> {
            if (isConnected()) {
                offlineLayout.setVisibility(View.GONE);
                webLayout.setVisibility(View.VISIBLE);
                webView.reload();
            }
        });
    }

    private void setupBottomNav() {
        findViewById(R.id.nav_home).setOnClickListener(v -> loadUrl(URL_HOME));
        findViewById(R.id.nav_jobs).setOnClickListener(v -> loadUrl(URL_JOBS));
        findViewById(R.id.nav_results).setOnClickListener(v -> loadUrl(URL_RESULTS));
        findViewById(R.id.nav_alerts).setOnClickListener(v -> loadUrl(URL_ALERTS));
    }

    private void loadUrl(String url) {
        if (isConnected()) {
            offlineLayout.setVisibility(View.GONE);
            webLayout.setVisibility(View.VISIBLE);
            webView.loadUrl(url);
        } else {
            showOffline();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Handle new notification tap while app is already open
        if (intent.getStringExtra("launch_url") != null) {
            loadUrl(intent.getStringExtra("launch_url"));
        }
    }

    private void showOffline() {
        progressBar.setVisibility(View.GONE);
        webLayout.setVisibility(View.GONE);
        offlineLayout.setVisibility(View.VISIBLE);
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}
