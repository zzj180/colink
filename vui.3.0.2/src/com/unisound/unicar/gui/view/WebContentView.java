/**
 * Copyright (c) 2012-2013 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : WebContentView.java
 * @ProjectName : iShuoShuo2
 * @PakageName : cn.yunzhisheng.vui.assistant.view
 * @Author : Brant
 * @CreateDate : 2013-3-25
 */
package com.unisound.unicar.gui.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import com.coogo.inet.vui.assistant.car.R;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2013-3-25
 * @ModifiedBy : Brant
 * @ModifiedDate: 2013-3-25
 * @Modified: 2013-3-25: 实现基本功能
 */
public class WebContentView extends FrameLayout implements ISessionView {
	public static final String TAG = "WebContentView";

	private String mUrl;

	private WebView mWebView;
	private View mViewLoading;
	private View mBtnViewWebSource;

	public WebContentView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.web_content_view, this, true);
		findViews();
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings()
				.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		mWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				if (newProgress == 100) {
					mViewLoading.setVisibility(View.GONE);
				} else {
					mViewLoading.setVisibility(View.VISIBLE);
				}
			}

		});
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return false;
			}

		});
		mBtnViewWebSource.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent view = new Intent(Intent.ACTION_VIEW);
				view.setData(Uri.parse(mUrl));
				view.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				v.getContext().startActivity(view);
			}
		});
	}

	public WebContentView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WebContentView(Context context) {
		this(context, null);
	}

	private void findViews() {
		mWebView = (WebView) findViewById(R.id.webView);
		mViewLoading = findViewById(R.id.progressBarLoading);
		mBtnViewWebSource = findViewById(R.id.textViewViewWeb);
	}

	public void setUrl(String url) {
		mUrl = url;
		mWebView.loadUrl(url);
	};

	@Override
	public boolean isTemporary() {
		return false;
	}

	@Override
	public void release() {

	}

}
