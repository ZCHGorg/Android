package io.charg.chargstation.ui.activities.webBasedActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import io.charg.chargstation.R;
import io.charg.chargstation.root.CommonData;
import io.charg.chargstation.ui.activities.BaseActivity;

public class WebBasedActivity extends BaseActivity {

    @BindView(R.id.webview)
    WebView mWebView;

    private ExtWebViewClient mExtWebClient;

    @Override
    public int getResourceId() {
        return R.layout.activity_web_based;
    }

    @Override
    public void onActivate() {
        initWebClient();
        initView();
        loadPage();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        mWebView.setWebViewClient(mExtWebClient);
        mWebView.getSettings().setJavaScriptEnabled(true);
    }

    private void initWebClient() {
        mExtWebClient = new ExtWebViewClient();
    }

    private void loadPage() {
        String url = CommonData.SITE_URL;

        if (!TextUtils.isEmpty(url)) {
            mWebView.loadUrl(url);
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    public class ExtWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Log.i(CommonData.TAG, "Current url: " + request.getUrl().toString());
            }
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.i(CommonData.TAG, "Page started: " + url);
        }

        @Override
        public void onPageFinished(WebView view, final String url) {
            super.onPageFinished(view, url);
            CookieSyncManager.getInstance().sync();
            Log.i(CommonData.TAG, "Page finished: " + url);
        }
    }
}
