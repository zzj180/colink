package com.unisound.unicar.gui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.utils.DeviceTool;
import com.unisound.unicar.gui.utils.Logger;

/**
 * Chat WebView
 * 
 * @author xiaodong
 * @date 20150909
 */
public class ChatWebView extends FrameLayout implements ISessionView {

	private static final String TAG = ChatWebView.class.getSimpleName();
	private Context mContext;
	private Handler mSessionManagerHandler;

	private WebView mWebViewChat;
	private String ttsAnswer;

	private static final double SCALE_NUM = 0.8;
	private boolean isFirstLoad = true;

	private static final String PARAM_H5_CHAT_CLEAR_HISTORY = "&historyLog=empty";

	public ChatWebView(Context context) {
		super(context);
	}

	public ChatWebView(Context context, Handler mSessionManagerHandler) {
		super(context);
		mContext = context;
		this.mSessionManagerHandler = mSessionManagerHandler;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_chat_web, this, true);
		// mAllChatObjList = allChatObjList;
		findViews();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void findViews() {
		Logger.d(TAG, "!--->findViews---");
		mWebViewChat = (WebView) findViewById(R.id.webview_chat);
		WebSettings webSettings = mWebViewChat.getSettings();
		// webView设置
		webSettings.setJavaScriptEnabled(true);
		// webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);

		webSettings.setAllowFileAccess(true);
		webSettings.setDatabaseEnabled(true);
		String dir = mContext.getDir("database", Context.MODE_PRIVATE)
				.getPath();
		webSettings.setDatabasePath(dir);
		webSettings.setDomStorageEnabled(true);
		webSettings.setGeolocationEnabled(true);

		webSettings.setDefaultTextEncodingName("UTF-8");

	}

	private WebViewClient mWebViewClient = new WebViewClient() {
		@SuppressWarnings("deprecation")
		@Override
		public void onPageFinished(WebView view, String url) {
			if (isFirstLoad) {
				// playTTS(ttsAnswer);

				int height = (int) (DeviceTool.getScreenHight(mContext) * SCALE_NUM);
				mWebViewChat.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, height));
				mWebViewChat.setVisibility(VISIBLE);

				isFirstLoad = false;
			}
			super.onPageFinished(view, url);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// return true open with WebView / false open with browser
			view.loadUrl(url);
			return true;
		}
	};

	/**
	 * 
	 * @param url
	 * @param answer
	 * @param isClearHistory
	 */
	public void updateView(String url, String answer, boolean isClearHistory) {
		if (mWebViewChat == null) {
			return;
		}
		if (isClearHistory) {
			url = url + PARAM_H5_CHAT_CLEAR_HISTORY;
		}
		mWebViewChat.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				if (isFirstLoad) {
					// playTTS(ttsAnswer);
					Logger.d(TAG, "onPageFinished----url = " + url);
					int height = (int) (DeviceTool.getScreenHight(mContext) * SCALE_NUM);
					mWebViewChat.setLayoutParams(new LayoutParams(
							LayoutParams.MATCH_PARENT, height));
					mWebViewChat.setVisibility(VISIBLE);

					isFirstLoad = false;
				}
				super.onPageFinished(view, url);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// return true open with WebView / false open with browser
				Logger.d(TAG, "shouldOverrideUrlLoading----url = " + url);
				view.loadUrl(url);
				return true;
			}
		});
		// mWebViewChat.setWebChromeClient(new WebChromeClient());

		Logger.d(TAG, "!--->updateView---url = " + url + "; answer=" + answer
				+ "; isClearHistory = " + isClearHistory);
		ttsAnswer = answer;

		mWebViewChat.loadUrl(url);
	}

	/**
	 * 
	 */
	public void notifyDataChanged() {
		Logger.d(TAG, "!--->notifyDataChanged---");
	}

	@Override
	public boolean isTemporary() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		Logger.d(TAG, "!--->release---");
	}

}
