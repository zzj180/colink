package com.unisound.unicar.gui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.utils.DeviceTool;
import com.unisound.unicar.gui.utils.Logger;

/**
 * Chat WebView
 * 
 * @author zzj
 * @date 20150909
 */
@SuppressLint("HandlerLeak")
public class ChatWebView extends FrameLayout implements ISessionView {

    private static final String TAG = ChatWebView.class.getSimpleName();
    private Context mContext;

    private LinearLayout mLoadingLinearLayout;
    private ProgressBar mProgressbar;
    private int mProgressMax = 100;
    private int mShowWebViewDelay = 0;
    /**
     * UI Configuration: is Show Loading ProgressBar when loading
     */
    private static final boolean isShowLoadingProgressBar = false;

    private static final int MSG_HIDE_PROGRESSBAR = 0;
    private static final int MSG_ON_PAGE_FINISHED = 1;

    private WebView mWebViewChat;

    private static final String PARAM_H5_CHAT_CLEAR_HISTORY = "&historyLog=empty";

    public ChatWebView(Context context) {
        super(context);
    }

    public ChatWebView(Context context, Handler mSessionManagerHandler) {
        super(context);
        mContext = context;
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_chat_web, this, true);
        findViews();
    }

    @SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
    private void findViews() {
        Logger.d(TAG, "!--->findViews---");

        mProgressbar = (ProgressBar) findViewById(R.id.pb_h5chat);
        mProgressbar.setMax(mProgressMax);
        mLoadingLinearLayout = (LinearLayout) findViewById(R.id.ll_h5chat_loading);

        mWebViewChat = (WebView) findViewById(R.id.webview_chat);

        int height = DeviceTool.getScreenHight(mContext);
        mWebViewChat.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height));
        mLoadingLinearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height));

        WebSettings webSettings = mWebViewChat.getSettings();
        // webView设置
        webSettings.setJavaScriptEnabled(true);
        // webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        // webSettings.setUseWideViewPort(true);
        // webSettings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);

        webSettings.setAllowFileAccess(true);
        webSettings.setDatabaseEnabled(true);
        String dir = mContext.getDir("database", Context.MODE_PRIVATE).getPath();
        webSettings.setDatabasePath(dir);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);// 启用地理定位

        webSettings.setDefaultTextEncodingName("UTF-8");

        mWebViewChat.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Logger.d(TAG, "WebViewClient---onPageStarted webView : " + mWebViewChat);
                showWebView(false);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Logger.d(TAG, "WebViewClient---onPageFinished----");
                if (null != mHandler) {
                    mHandler.sendEmptyMessageDelayed(MSG_ON_PAGE_FINISHED, mShowWebViewDelay);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // return true open with WebView / false open with browser
                Logger.d(TAG, "WebViewClient---shouldOverrideUrlLoading----url = " + url);
                view.loadUrl(url);
                return true;
            }
        });

        // mWebViewChat.setWebChromeClient(new WebChromeClient());
        mWebViewChat.setWebChromeClient(mWebChromeClient);// XD added 20151109

        // mWebViewChat.loadUrl("file:///android_asset/no_content.html");
    }

    /**
     * @date 2015-11-9
     */
    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            Logger.d(TAG, "onProgressChanged newProgress : " + newProgress
                    + "; isShowLoadingProgressBar = " + isShowLoadingProgressBar);
            if (!isShowLoadingProgressBar) {
                return;
            }
            if (null != mWebViewChat) {
                if (newProgress == mProgressMax) {
                    if (mHandler != null && !mHandler.hasMessages(MSG_HIDE_PROGRESSBAR)) {
                        mHandler.sendEmptyMessageDelayed(MSG_HIDE_PROGRESSBAR, mShowWebViewDelay);
                    }
                } else {
                    showProgressBar(true);
                }
                mProgressbar.setProgress(newProgress);
            }
        };
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

        Logger.d(TAG, "!--->updateView---url = " + url + "; answer=" + answer
                + "; isClearHistory = " + isClearHistory);

        mWebViewChat.loadUrl(url);
    }



    /**
     * reload
     * 
     * @param url
     * @param answer
     * @param isClearHistory
     */
    public void reload(String url, String answer, boolean isClearHistory) {
        if (mWebViewChat == null) {
            return;
        }
        Logger.d(TAG, "reload-----");
        if (isClearHistory) {
            url = url + PARAM_H5_CHAT_CLEAR_HISTORY;
        }

        Logger.d(TAG, "!--->updateView---url = " + url + "; answer=" + answer
                + "; isClearHistory = " + isClearHistory);
        mWebViewChat.reload();
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int msgType = msg.what;
            Logger.d(TAG, "handleMessage delay msg what : " + msg.what);
            switch (msgType) {
                case MSG_HIDE_PROGRESSBAR:
                    showProgressBar(false);
                    break;
                case MSG_ON_PAGE_FINISHED:
                    showWebView(true);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 
     * @param isShow
     */
    private void showWebView(boolean isShow) {
        Logger.d(TAG, "showWebView---isShow = " + isShow);
        if (isShow) {
            if (null != mWebViewChat) {
                mWebViewChat.setVisibility(VISIBLE);
            }
            if (null != mLoadingLinearLayout) {
                mLoadingLinearLayout.setVisibility(GONE);
            }
        } else {
            if (null != mWebViewChat) {
                mWebViewChat.setVisibility(GONE);
            }
            if (null != mLoadingLinearLayout) {
                mLoadingLinearLayout.setVisibility(VISIBLE);
            }
        }
    }

    /**
     * 
     * @param isShow
     */
    private void showProgressBar(boolean isShow) {
        Logger.d(TAG, "showProgressBar---isShow = " + isShow);
        if (!isShowLoadingProgressBar) {
            return;
        }
        if (isShow) {
            if (null != mProgressbar && mProgressbar.getVisibility() != View.VISIBLE) {
                mProgressbar.setVisibility(VISIBLE);
            }
        } else {
            if (null != mProgressbar) {
                mProgressbar.setVisibility(GONE);
            }
        }
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
