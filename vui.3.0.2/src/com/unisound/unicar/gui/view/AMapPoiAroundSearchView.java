/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : AMapPoiAroundSearchView.java
 * @ProjectName : uniCarSolution_dev_xd_20151010
 * @PakageName : com.unisound.unicar.gui.view
 * @version : 1.1
 * @Author : Xiaodong.He
 * @CreateDate : 2015-10-21
 */
package com.unisound.unicar.gui.view;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;
import cn.yunzhisheng.vui.assistant.WindowService;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.adapter.AroundSearchKeywordAdapter;
import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.utils.DeviceTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.utils.StringUtil;
import com.unisound.unicar.gui.view.ClickEnableSpinner.OnClickSpinnerListener;

/**
 * @author xiaodong
 * @date 20151021
 */
@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled", "HandlerLeak",
		"InflateParams" })
public class AMapPoiAroundSearchView extends FrameLayout implements
		ISessionView {

	private static final String TAG = AMapPoiAroundSearchView.class
			.getSimpleName();
	private Context mContext;
	private ClickEnableSpinner selectDeep;// 选择类别列表
	private String[] itemDeep = { "餐饮", "酒店", "景区", "影院" };
	private Spinner selectType;// 选择返回是否有团购，优惠
	private String[] itemTypes = { "所有poi", "有团购", "有优惠", "有团购或者优惠" };
	private String deepType = "餐饮";// poi搜索类型

	/* < XD 20151027 added Begin */
	private EditText mEtSearchKeyword, mEtFocus;
	private ImageView mIvMoreSearchKey;
	private LinearLayout parent;
	private PopupWindow mSelectKeywordPopupWindow = null;
	private ListView mKeywordListView = null;
	private AroundSearchKeywordAdapter mKeywordAdapter = null;
	private ArrayList<String> datas = new ArrayList<String>();
	private int pwidth;

	private ImageView mSearchButton;
	private LinearLayout mButtonsLayout;
	private Button mBtnOk;
	private Button mBtnCancel;
	// private boolean isEditLocationFocusByUser = false;
	/* XD 20151027 added End > */

	private LocationInfo mLocationInfo = null;
	private double mFromLat = 0.0;
	private double mFromLng = 0.0;
	private String mFromAddress = "";

	private static final int MSG_ON_SELECT_TYPE_PINNER_CLICK = 1001;
	public static final int MSG_ON_HELP_KEYWORD_SELECT = 1002;
	public static final int MSG_ON_HELP_KEYWORD_DELETE = 1003;

	private boolean isPoiSearchTypeSelectByUser = false;

	private WebView webView;

	public void onCreate(Bundle b) {

	}

	public void onResume() {

	};

	public void onSaveInstanceState(Bundle outState) {

	}

	public void onPause() {

	}

	public void onDestroy() {

	}

	/**
	 * 
	 * must call onCreate() after new AMapPoiAroundSearchView()
	 * 
	 * @param context
	 */
	public AMapPoiAroundSearchView(Context context) {
		this(context, null);
		mContext = context;
		mLocationInfo = WindowService.mLocationInfo;
		initView();
	}

	/**
	 * 
	 * @param context
	 * @param attrs
	 */
	public AMapPoiAroundSearchView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private void initView() {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.layout_localsearch_amap_js, this, true);

		webView = (WebView) view.findViewById(R.id.js_webview);
		webView.getSettings().setJavaScriptEnabled(true);

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

		webView.loadUrl("file:///android_asset/amapnearbyhtml.html");

		mEtSearchKeyword = (EditText) findViewById(R.id.et_around_search_keyword);
		mEtFocus = (EditText) findViewById(R.id.et_focus);
		mIvMoreSearchKey = (ImageView) findViewById(R.id.iv_more_search_keyword);
		mButtonsLayout = (LinearLayout) findViewById(R.id.ll_edit_buttons);
		mBtnOk = (Button) findViewById(R.id.btn_edit_ok);
		mBtnCancel = (Button) findViewById(R.id.btn_edit_cancel);

		mIvMoreSearchKey.setOnClickListener(mOnClickListener);
		mEtFocus = (EditText) findViewById(R.id.et_focus);

		mEtSearchKeyword.setOnFocusChangeListener(mOnFocusChangeListener);
		mEtSearchKeyword.addTextChangedListener(mEditTextWatcher);
		mBtnOk.setOnClickListener(mOnClickListener);
		mBtnCancel.setOnClickListener(mOnClickListener);

		parent = (LinearLayout) findViewById(R.id.ll_edit_around_search);
		pwidth = parent.getWidth();
		Logger.d(TAG, "initView--pwidth = " + pwidth);
		initPopuWindow();

		mSearchButton = (ImageView) findViewById(R.id.searchButton);
		mSearchButton.setOnClickListener(mOnClickListener);
		getCurrentLocation();
		init();
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		Logger.d(TAG, "init------Begin");
		setUpMap();
		setSelectType();
		Logger.d(TAG, "init------End");
	}

	private final class JSConnection {

		@SuppressWarnings("unused")
		public void sendParamsToJS() {

			Logger.e(TAG, "JSConnection------sendParamsToJS-----");

			JSONObject obj = new JSONObject();
			if (mLocationInfo != null) {
				try {
					obj.put("lat", String.valueOf(mLocationInfo.getLatitude()));
					obj.put("lon", String.valueOf(mLocationInfo.getLongitude()));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// webView.loadUrl("javascript:show('" + obj.toString() + "') ");
			webView.loadUrl("javascript:show() ");
		}

	}

	/**
     * 
     */
	private void getCurrentLocation() {
		mLocationInfo = WindowService.mLocationInfo;
		Logger.d(TAG, "getCurrentLocation--mLocationInfo = " + mLocationInfo);
		if (mLocationInfo != null) {
			mFromLat = mLocationInfo.getLatitude();
			mFromLng = mLocationInfo.getLongitude();
			mFromAddress = mLocationInfo.getAddress();
			Logger.d(TAG, "getCurrentLocation--mFromAddress = " + mFromAddress
					+ "; mFromLat = " + mFromLat + "; mFromLng = " + mFromLng);
		} else {
			// 默认西单广场
			mFromAddress = "西单";
		}
	}

	/**
	 * 设置城市选择
	 */
	private void setUpMap() {
		selectDeep = (ClickEnableSpinner) findViewById(R.id.spinnerdeep);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
				android.R.layout.simple_spinner_item, itemDeep);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		selectDeep.setAdapter(adapter);
		selectDeep.setOnClickMyListener(mOnClickSpinnerListener);
		selectDeep.setOnItemSelectedListener(mOnItemSelectedListener);// 添加spinner选择框监听事件
	}

	private Handler mHanlder = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Bundle data = msg.getData();
			switch (msg.what) {
			case MSG_ON_SELECT_TYPE_PINNER_CLICK:
				if (null != selectDeep) {
					selectDeep.performClick();
				}
				if (null != mPoiAroundSearchViewListener) {
					mPoiAroundSearchViewListener.onStartSelectPoiSearchType();
				}
				break;
			case MSG_ON_HELP_KEYWORD_SELECT:
				int selIndex = data.getInt("index");
				Logger.d(TAG, "MSG_ON_HELP_KEYWORD_SELECT--selIndex = "
						+ selIndex);
				deepType = datas.get(selIndex);
				mEtSearchKeyword.setText(deepType);
				dismissPopupWindw();
				if (null != mPoiAroundSearchViewListener) {
					mPoiAroundSearchViewListener
							.onPoiSearchTypeSelect(deepType);
				}
				break;
			case MSG_ON_HELP_KEYWORD_DELETE:
				int delIndex = data.getInt("index");
				datas.remove(delIndex);
				mKeywordAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};

	private OnClickSpinnerListener mOnClickSpinnerListener = new OnClickSpinnerListener() {
		@Override
		public void onClick() {
			Logger.d(TAG, "mOnClickMyListener--spinnerdeep click");
			mHanlder.sendEmptyMessage(MSG_ON_SELECT_TYPE_PINNER_CLICK);
		}
	};

	/**
	 * 设置选择类型
	 */
	private void setSelectType() {
		selectType = (Spinner) findViewById(R.id.searchType);// 搜索类型
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
				android.R.layout.simple_spinner_item, itemTypes);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		selectType.setAdapter(adapter);
		selectType.setOnItemSelectedListener(mOnItemSelectedListener);// 添加spinner选择框监听事件
	}

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.et_around_search_keyword:
				Logger.d(TAG, "et_search_location click");
				onEditFucus();
				break;
			case R.id.btn_edit_ok:
				// String searchKeyword = mEtSearchKeyword.getText().toString();
				Logger.d(TAG, "btn_edit_ok click-----");
				if (onSearchBtnClick()) {
					mButtonsLayout.setVisibility(View.GONE);
					DeviceTool.showEditTextKeyboard(mEtSearchKeyword, false);
				}
				break;
			case R.id.btn_edit_cancel:
				if (null != mPoiAroundSearchViewListener) {
					mPoiAroundSearchViewListener.onEditCancel();
				}
				mButtonsLayout.setVisibility(View.GONE);
				DeviceTool.showEditTextKeyboard(mEtSearchKeyword, false);
				resetEditFocus();
				break;
			case R.id.iv_more_search_keyword:
				Logger.d(TAG, "iv_more_search_key click");
				showPopupWindw();
				if (null != mPoiAroundSearchViewListener) {
					mPoiAroundSearchViewListener.onStartSelectPoiSearchType();
				}

				if (null != mButtonsLayout
						&& mButtonsLayout.getVisibility() == View.VISIBLE) {
					mButtonsLayout.setVisibility(View.GONE);
					DeviceTool.showEditTextKeyboard(mEtSearchKeyword, false);
					resetEditFocus();
				}
				break;
			case R.id.searchButton:
				Logger.d(TAG, "searchButton click-----");
				onSearchBtnClick();
				break;
			default:
				break;
			}
		}

	};

	/**
	 * 
	 * @return false: input error
	 */
	private boolean onSearchBtnClick() {
		deepType = mEtSearchKeyword.getText().toString();
		Logger.d(TAG, "onSearchBtnClick---input = " + deepType);
		if (TextUtils.isEmpty(deepType)) {
			Toast.makeText(mContext, R.string.toast_input_localsearch_type,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		deepType = StringUtil.clearSpecialCharacter(deepType);
		if (TextUtils.isEmpty(deepType)) {
			mEtSearchKeyword.setText("");
			Toast.makeText(mContext,
					R.string.toast_input_localsearch_type_error,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		Logger.d(TAG, "onSearchBtnClick---deepType = " + deepType);
		if (null != mPoiAroundSearchViewListener) {
			mPoiAroundSearchViewListener.onPoiSearchTypeSelect(deepType);
		}
		return true;
	}

	private OnItemSelectedListener mOnItemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			if (parent == selectDeep) {
				deepType = itemDeep[position];
			}

			/* < XD added 20151023 Begin */
			Logger.d(TAG, "onItemSelected---deepType = " + deepType
					+ "; isPoiSearchTypeSelectByUser = "
					+ isPoiSearchTypeSelectByUser);
			if (isPoiSearchTypeSelectByUser
					&& null != mPoiAroundSearchViewListener) {
				mPoiAroundSearchViewListener.onPoiSearchTypeSelect(deepType);
			}
			isPoiSearchTypeSelectByUser = true;
			/* XD added 20151023 End > */
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			Logger.d(TAG, "onNothingSelected---");
			if (parent == selectDeep) {
				deepType = "餐饮";
			}
		}

	};

	/* < XD 20151027 added Begin */
	private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			switch (v.getId()) {
			case R.id.et_around_search_keyword:
				Logger.d(TAG, "onFocusChange---hasFocus = " + hasFocus);
				if (hasFocus) {
					onEditFucus();
				}
				break;
			default:
				break;
			}
		}
	};

	private void onEditFucus() {
		if (null != mPoiAroundSearchViewListener) {
			mPoiAroundSearchViewListener.onEditFucus();
		}
		mButtonsLayout.setVisibility(View.VISIBLE);
	}

	private void resetEditFocus() {
		mEtSearchKeyword.clearFocus();
		mEtSearchKeyword.setText("");
		mEtFocus.requestFocus();
		mEtFocus.requestFocusFromTouch();
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

	private void initDatas() {
		datas.clear();
		for (int i = 0; i < itemDeep.length; i++) {
			datas.add(itemDeep[i]);
		}
	}

	/**
	 * init PopupWindow
	 */
	@SuppressWarnings("deprecation")
	private void initPopuWindow() {
		initDatas();

		View searchKeywordWindow = (View) LayoutInflater.from(mContext)
				.inflate(R.layout.localsearch_pop_keyword_list, null);
		mKeywordListView = (ListView) searchKeywordWindow
				.findViewById(R.id.lv_search_help_list);

		mKeywordAdapter = new AroundSearchKeywordAdapter(mContext, mHanlder,
				datas);
		mKeywordListView.setAdapter(mKeywordAdapter);

		mSelectKeywordPopupWindow = new PopupWindow(searchKeywordWindow,
				pwidth, LayoutParams.WRAP_CONTENT, true);
		mSelectKeywordPopupWindow.setOutsideTouchable(true);
		mSelectKeywordPopupWindow.setBackgroundDrawable(new BitmapDrawable());
	}

	public void showPopupWindw() {
		pwidth = parent.getWidth();
		if (pwidth == 0) {
			pwidth = 450;
		}
		Logger.d(TAG, "showPopupWindw---pwidth = " + pwidth);
		mSelectKeywordPopupWindow.setWidth(pwidth);
		mSelectKeywordPopupWindow.setFocusable(true);
		mSelectKeywordPopupWindow.showAsDropDown(parent, 0, -8);
	}

	public void dismissPopupWindw() {
		mSelectKeywordPopupWindow.dismiss();
	}

	/* XD 20151027 added End > */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		Logger.d(TAG, "onInterceptTouchEvent---ev--" + ev.getAction());
		if (MotionEvent.ACTION_MOVE == ev.getAction()
				&& mPoiAroundSearchViewListener != null) {
			mPoiAroundSearchViewListener.onMapViewMove();
		}
		return super.onInterceptTouchEvent(ev);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.unisound.unicar.gui.view.ISessionView#isTemporary()
	 */
	@Override
	public boolean isTemporary() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.unisound.unicar.gui.view.ISessionView#release()
	 */
	@Override
	public void release() {
		Logger.d(TAG, "release---");
		if (null != webView) {
			webView = null;
		}
	}

	private PoiAroundSearchViewListener mPoiAroundSearchViewListener;

	public void setPoiAroundSearchViewListener(
			PoiAroundSearchViewListener poiAroundSearchViewListener) {
		mPoiAroundSearchViewListener = poiAroundSearchViewListener;
	}

	/**
	 * 
	 * @author xiaodong.he
	 * @date 2015-10-23
	 */
	public interface PoiAroundSearchViewListener {

		public void onEditFucus();

		public void onEditCancel();

		public void onStartSelectPoiSearchType();

		public void onPoiSearchTypeSelect(String poiSearchType);

		public void onMapViewMove();
	}

}
