/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : AMapLocationView.java
 * @ProjectName : uniCarSolution_dev_xd_20151010
 * @PakageName : com.unisound.unicar.gui.view
 * @version : 1.0
 * @Author : Xiaodong.He
 * @CreateDate : 2015-10-20
 */
package com.unisound.unicar.gui.view;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.yunzhisheng.vui.assistant.WindowService;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.utils.DeviceTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.utils.StringUtil;

/**
 * @author xiaodong
 * @date 20151020
 */
@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
public class AMapLocationView extends BaseMapLocationView {

	private static final String TAG = AMapLocationView.class.getSimpleName();

	private Context mContext;

	private LocationInfo mLocationInfo = null;

	private WebView webView;

	private EditText mEtSearchLocation, mEtFocus;
	private LinearLayout mButtonsLayout;
	private Button mBtnOk;
	private Button mBtnCancel;
	private ImageView mIvSearchLocation;
	private MapLocationViewListener mMapLocationViewListener;
	private boolean isEditLocationFocusByUser = false;

	/**
	 * @param context
	 */
	public AMapLocationView(Context context) {
		this(context, null);
		mContext = context;

		getCurrentLocation();
		initView();
	}

	public AMapLocationView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setMapLocationViewListener(
			MapLocationViewListener mapLocationViewListener) {
		mMapLocationViewListener = mapLocationViewListener;
	}

	private void getCurrentLocation() {
		mLocationInfo = WindowService.mLocationInfo;
	}

	private void initView() {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.layout_location_amap_js, this, true);

		webView = (WebView) view.findViewById(R.id.js_webview);
		webView.getSettings().setJavaScriptEnabled(true);
		// WebSettings webSettings = webView.getSettings();
		// webSettings.setJavaScriptEnabled(true);
		// webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		// webSettings.setUseWideViewPort(true);
		// webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		int height = (int) (DeviceTool.getScreenHight(mContext));
		webView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				height));

		webView.addJavascriptInterface(new JSConnection(), "jsConnection");

		webView.setWebChromeClient(new WebChromeClient() {
		});
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				Logger.d(TAG, "onPageFinished----------");

				if (mLocationInfo != null) {
					String data = "[{\"lng\":" + mLocationInfo.getLongitude()
							+ ", \"lat\":" + mLocationInfo.getLatitude() + "}]";
					webView.loadUrl("javascript:show('" + data + "') ");
				} else {
					webView.loadUrl("javascript:show(null) ");
				}
			}
		});

		webView.loadUrl("file:///android_asset/amaproutehtml.html");

		mEtSearchLocation = (EditText) findViewById(R.id.et_around_search_keyword);
		mButtonsLayout = (LinearLayout) findViewById(R.id.ll_edit_buttons);
		mBtnOk = (Button) findViewById(R.id.btn_edit_ok);
		mBtnCancel = (Button) findViewById(R.id.btn_edit_cancel);
		mIvSearchLocation = (ImageView) findViewById(R.id.iv_search_location);

		mEtFocus = (EditText) findViewById(R.id.et_focus);

		mEtSearchLocation.setOnFocusChangeListener(mOnFocusChangeListener);
		mEtSearchLocation.addTextChangedListener(mEditTextWatcher);
		mBtnOk.setOnClickListener(mOnClickListener);
		mBtnCancel.setOnClickListener(mOnClickListener);
		mIvSearchLocation.setOnClickListener(mOnClickListener);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// Logger.d(TAG, "onInterceptTouchEvent---action-" + ev.getAction());
		if (MotionEvent.ACTION_MOVE == ev.getAction()
				&& mMapLocationViewListener != null) {
			mMapLocationViewListener.onMapViewMove();
		}
		return super.onInterceptTouchEvent(ev);
	}

	private final class JSConnection {

		@SuppressWarnings("unused")
		public void sendParamsToJS() {

			Logger.d(TAG, "JSConnection------sendParamsToJS-----");

			JSONObject obj = new JSONObject();
			if (mLocationInfo != null) {
				try {
					obj.put("lat", String.valueOf(mLocationInfo.getLatitude()));
					obj.put("lon", String.valueOf(mLocationInfo.getLongitude()));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			webView.loadUrl("javascript:show() ");
		}
	}

	private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			switch (v.getId()) {
			case R.id.et_around_search_keyword:
				Logger.d(TAG, "onFocusChange---hasFocus = " + hasFocus
						+ "; isEditLocationFocusByUser = "
						+ isEditLocationFocusByUser);
				if (hasFocus) {
					onEditFucus();
				}
				isEditLocationFocusByUser = true;
				break;
			default:
				break;
			}
		}
	};

	private void onEditFucus() {
		if (null != mMapLocationViewListener) {
			mMapLocationViewListener.onEditLocationFocus();
		}
		mButtonsLayout.setVisibility(View.VISIBLE);
	}

	private TextWatcher mEditTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			Logger.d(TAG, "mEditTextWatcher--onTextChanged--s=" + s
					+ "; count = " + count + "; start = " + start
					+ "; before = " + before);
			if (null == mBtnOk) {
				return;
			}
			if (TextUtils.isEmpty(s)) {
				mBtnOk.setEnabled(false);
			} else {
				mBtnOk.setEnabled(true);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			Logger.d(TAG, "mEditTextWatcher--beforeTextChanged--s=" + s
					+ "; count = " + count + "; start = " + start
					+ "; after = " + after);
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.et_around_search_keyword:
				Logger.d(TAG, "et_search_location click");
				onEditFucus();
				break;
			case R.id.btn_edit_ok:
				onSearchClick();
				break;
			case R.id.btn_edit_cancel:
				if (null != mMapLocationViewListener) {
					mMapLocationViewListener.onConfirmLocationCancel();
				}
				resetEditFocus();
				mButtonsLayout.setVisibility(View.GONE);
				DeviceTool.showEditTextKeyboard(mEtSearchLocation, false);
				break;
			case R.id.iv_search_location:
				onSearchClick();
				break;
			default:
				break;
			}
		}

	};

	/**
	 * onSearchClick
	 */
	private void onSearchClick() {
		String input = mEtSearchLocation.getText().toString();
		Logger.d(TAG, "onSearchClick---input = " + input);
		if (TextUtils.isEmpty(input)) {
			Toast.makeText(mContext, R.string.toast_input_location,
					Toast.LENGTH_SHORT).show();
			return;
		}
		String location = StringUtil.clearSpecialCharacter(input);
		Logger.d(TAG, "onSearchClick---location = " + location);
		if (TextUtils.isEmpty(location)) {
			mEtSearchLocation.setText("");
			Toast.makeText(mContext, R.string.toast_input_location_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (null != mMapLocationViewListener) {
			mMapLocationViewListener.onConfirmLocationOk(location);
		}
		mButtonsLayout.setVisibility(View.GONE);
		DeviceTool.showEditTextKeyboard(mEtSearchLocation, false);
	}

	private void resetEditFocus() {
		mEtSearchLocation.clearFocus();
		mEtSearchLocation.setText("");
		mEtFocus.requestFocus();
		mEtFocus.requestFocusFromTouch();
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		super.release();
		Logger.d(TAG, "release---");
		if (null != webView) {
			webView = null;
		}
	}
}
