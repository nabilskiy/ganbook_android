package com.ganbook.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ganbook.utils.StrUtils;
import com.project.ganim.R;

public class WebViewActivity extends BaseAppCompatActivity {

	// webview_layout
		
	public static final String TERMS = "http://www.ganbook.co.il/TermsofService.html";
	public static final String FAQ = "http://www.ganbook.co.il/faq-new.html";
	
	private static String urlToOpen; 

	@SuppressLint("SetJavaScriptEnabled") 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (StrUtils.isEmpty(urlToOpen)) {
			this.finish();
			return; 
		}


		String _urlToOpen = urlToOpen;
		urlToOpen = null;
		setContentView(R.layout.webview_layout);
		setActionBar("FAQ", true);

		final WebView webView = (WebView) findViewById(R.id.web_view);
		webView.getSettings().setJavaScriptEnabled(true);
		// open links in-app & no addressbar:
		webView.loadUrl(_urlToOpen);
		webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("mailto:")) {
                    //Handle mail Urls
                    startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(url)));
                } else {
                    view.loadUrl(url);
                }
                return true;
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                final Uri uri = request.getUrl();
                if (uri.toString().startsWith("mailto:")) {
                    //Handle mail Urls
                    startActivity(new Intent(Intent.ACTION_SENDTO, uri));
                } else {
                    //Handle Web Urls
                    view.loadUrl(uri.toString());
                }
                return true;
            }
        });

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if(item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}
	
	public static void start(Activity activity, String url) {
		urlToOpen = url;
		activity.startActivity(new Intent(activity, WebViewActivity.class));
	}

}
