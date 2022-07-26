package com.ganbook.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.project.ganim.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AttachmentWebView extends BaseAppCompatActivity {

    private static String loadUrl;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attachment_webview_layout);
        setActionBar("Attachment", true);

        WebView attachmentWebView = findViewById(R.id.attachmentWebView);
        if (Build.VERSION.SDK_INT >= 19) {
            attachmentWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else {
            attachmentWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        attachmentWebView.setWebViewClient(new AttachmentWebViewClient());
        attachmentWebView.getSettings().setJavaScriptEnabled(true);
        attachmentWebView.getSettings().setLoadWithOverviewMode(true);
        attachmentWebView.getSettings().setUseWideViewPort(true);
        attachmentWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        attachmentWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        attachmentWebView.getSettings().setBuiltInZoomControls(true);

        String urlSplit [] = loadUrl.split("/");
        String fileExtension = urlSplit[urlSplit.length - 1];

        if(fileExtension.contains(".pdf") || fileExtension.contains(".docx") || fileExtension.contains(".pptx")) {
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append("https://docs.google.com/gview?embedded=true&url=");
            try {
                urlBuilder.append(URLEncoder.encode(loadUrl, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            attachmentWebView.loadUrl(urlBuilder.toString());
        } else {
            attachmentWebView.loadUrl(loadUrl);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public static void start(Activity activity, String url) {
        Log.d("LOAD URL", url);
        loadUrl = url;
        Intent intent = new Intent(activity, AttachmentWebView.class);
        activity.startActivity(intent);
    }

    private class AttachmentWebViewClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
