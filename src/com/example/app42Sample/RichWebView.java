/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.example.app42Sample;

import android.R;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

/**
 * @author Vishnu Garg
 * 
 */
public class RichWebView {
	private View mCustomView = null;
	private MyWebChromeClient mWebChromeClient = null;
	private WebView webView;
	private FrameLayout frameLayout;
	private ProgressBar progressBar;

	/**
	 * Used to build WebView and load Html and image content over it
	 * @param webFrame
	 * @param webView
	 * @param progress
	 * @param isUrlContent
	 * @param content
	 */
	public RichWebView(FrameLayout webFrame, WebView webView,
			ProgressBar progress,boolean isUrlContent,String content) {
		this.webView = webView;
		this.frameLayout = webFrame;
		this.progressBar = progress;
		webView.setVisibility(View.VISIBLE);
		WebSettings webSettings = webView.getSettings();
		webSettings.setAllowFileAccess(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(true);
	
		webView.setWebViewClient(new MyWebClient());
		mWebChromeClient = new MyWebChromeClient();
		webView.setWebChromeClient(mWebChromeClient);
		webView.setBackgroundColor(R.color.transparent);
		if(isUrlContent)
			webView.loadUrl(content);
		else
			webView.loadData(content, "text/html", "UTF-8");
	}

	/**
	 * Set WebView Client on Android WebView
	 *
	 */
	public class MyWebClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			progressBar.setVisibility(View.GONE);
		}
	}

	/**
	 * WebView CromeClient
	 *
	 */
	private class MyWebChromeClient extends WebChromeClient {
		private WebChromeClient.CustomViewCallback mCustomViewCallback;

		@Override
		public void onShowCustomView(View view,
				WebChromeClient.CustomViewCallback callback) {
			webView.setVisibility(View.GONE);
			if (mCustomView != null) {
				callback.onCustomViewHidden();
				return;
			}
			frameLayout.addView(view);
			mCustomView = view;
			mCustomViewCallback = callback;
			frameLayout.setVisibility(View.VISIBLE);
		}

		@Override
		public void onHideCustomView() {
			if (mCustomView == null)
				return;
			mCustomView.setVisibility(View.GONE);
			frameLayout.removeView(mCustomView);
			mCustomView = null;
			mCustomViewCallback.onCustomViewHidden();
			webView.setVisibility(View.VISIBLE);
		}

		@Override
		public void onCloseWindow(WebView window) {
			mWebChromeClient.onHideCustomView();
		}
	}
	/**
	 * hide customWebView
	 */
	public void hideCustomView() {
		if(mCustomView!=null)
		mWebChromeClient.onHideCustomView();
	}
	
}
