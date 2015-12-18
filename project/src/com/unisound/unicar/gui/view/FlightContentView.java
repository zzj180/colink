package com.unisound.unicar.gui.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.GuiProtocolUtil;

public class FlightContentView extends FrameLayout implements ISessionView {

	private WebView mFlightWebView;
	private LinearLayout ll_flight_wait_content;
	private Handler mSessionManagerHandler;
	private String ttsAnswer;
	private TextView startPOIText;
	private TextView endPOIText;
	private LinearLayout mLayCancel;
	private ImageView mImgBuffering;
	private Context mContext;
	private static final double SCALE_NUM = 0.8;
	private boolean isFirstLoad = true;
	private boolean isCancel = false;

	public FlightContentView(Context context, Handler mSessionManagerHandler, String origin, String destination) {
		super(context);
		this.mContext = context;
		this.mSessionManagerHandler = mSessionManagerHandler;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.flight_content_view, this, true);
		
		initView(origin, destination);
	}

	private void initView(String origin, String destination) {
		mFlightWebView = (WebView) findViewById(R.id.webview_flight);
		WebSettings webSettings = mFlightWebView.getSettings();
		//webView设置
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		
		ll_flight_wait_content = (LinearLayout) findViewById(R.id.ll_flight_wait_content);
		
		//设置waittingView
		startPOIText = (TextView) findViewById(R.id.startPOIText);
		startPOIText.setText(origin);
		endPOIText = (TextView) findViewById(R.id.endPOIText);
		endPOIText.setText(destination);
		
		//设置载入动画
		mImgBuffering = (ImageView) findViewById(R.id.imageViewBuffering);
		mImgBuffering.post(new Runnable() {
			@Override
			public void run() {
				Drawable drawable = mImgBuffering.getDrawable();
				if (drawable != null && drawable instanceof AnimationDrawable) {
					((AnimationDrawable) drawable).start();
				}
			}
		});
		
		//设置按钮点击事件
		mLayCancel = (LinearLayout) findViewById(R.id.layCancel);
		mLayCancel.setOnClickListener(mOnClickListener);
	}

	public void updateUI(String url, String answer){
		if(mFlightWebView != null){
			ttsAnswer = answer;
			
			mFlightWebView.setWebViewClient(new WebViewClient(){
				@SuppressWarnings("deprecation")
				@Override
				public void onPageFinished(WebView view, String url) {
					//由于可能载入很多页面,所以只有第一次载入才播放tts
					if(isFirstLoad && !isCancel){
						//1.当载入页面完成后,播放tts
						playTTS(ttsAnswer);
						//3.非第一次
						isFirstLoad = false;
					}	
						//2.ui切换,隐藏等待界面,显示webView
						ll_flight_wait_content.setVisibility(INVISIBLE);
						WindowManager mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
						int height = (int) (mWindowManager.getDefaultDisplay().getHeight() * SCALE_NUM);
						mFlightWebView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height));
						mFlightWebView.setVisibility(VISIBLE);
						
					
					super.onPageFinished(view, url);
				}
			});
			
			mFlightWebView.loadUrl(url);
		}
	}
	
	private void playTTS(String ttsContent){
		Message msg = new Message();
		msg.what = SessionPreference.MESSAGE_UI_OPERATE_PROTOCAL;
		
		Bundle bundle = new Bundle();
		bundle.putString(SessionPreference.KEY_BUNDLE_EVENT_NAME, SessionPreference.EVENT_NAME_PLAY_TTS);
		bundle.putString(SessionPreference.KEY_BUNDLE_PROTOCAL, GuiProtocolUtil.getPlayTTSEventParam(ttsContent,
				GuiProtocolUtil.VALUE_RECOGNIZER_TYPE_WAKEPU));
		msg.obj = bundle;
		
		mSessionManagerHandler.sendMessage(msg);
	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			isCancel = true;
			if(mFlightWebView != null){
				mFlightWebView.stopLoading();
			}
			mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_WAITING_SESSION_CANCEL);
		}
	};
	
	@Override
	public boolean isTemporary() {
		return false;
	}

	@Override
	public void release() {
	}

}
