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
import java.util.List;

import android.app.ProgressDialog;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;
import cn.yunzhisheng.vui.assistant.WindowService;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.adapter.AroundSearchKeywordAdapter;
import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.utils.DeviceTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.utils.StringUtil;
import com.unisound.unicar.gui.utils.ToastUtil;
import com.unisound.unicar.gui.view.ClickEnableSpinner.OnClickSpinnerListener;

/**
 * @author xiaodong
 * @date 20151021
 */
public class AMapPoiAroundSearchViewBake extends LinearLayout implements
		ISessionView {

	private static final String TAG = AMapPoiAroundSearchViewBake.class
			.getSimpleName();
	private Context mContext;

	private MapView mapView;
	private AMap aMap;
	private ProgressDialog progDialog = null;// 搜索时进度条
	private ClickEnableSpinner selectDeep;// 选择类别列表
	private String[] itemDeep = { "餐饮", "酒店", "景区", "影院" };
	private Spinner selectType;// 选择返回是否有团购，优惠
	private String[] itemTypes = { "所有poi", "有团购", "有优惠", "有团购或者优惠" };
	private String deepType = "餐饮";// poi搜索类型
	private int searchType = 0;// 搜索类型
	private int tsearchType = 0;// 当前选择搜索类型
	private PoiResult poiResult; // poi返回的结果
	private int currentPage = 0;// 当前页面，从0开始计数
	private PoiSearch.Query query;// Poi查询条件类
	private LatLonPoint mLatLonPoint = null;
	private Marker locationMarker; // 选择的点
	private PoiSearch poiSearch;
	private PoiOverlay poiOverlay;// poi图层
	private List<PoiItem> poiItems;// poi数据
	private Marker detailMarker;// 显示Marker的详情
	private Button nextButton;// 下一页

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
	private String mFromCity = "";
	private String mFromAddress = "";

	private static final int MSG_ON_SELECT_TYPE_PINNER_CLICK = 1001;
	public static final int MSG_ON_HELP_KEYWORD_SELECT = 1002;
	public static final int MSG_ON_HELP_KEYWORD_DELETE = 1003;

	private boolean isPoiSearchTypeSelectByUser = false;

	/**
	 * 
	 * must call onCreate() after new AMapPoiAroundSearchView()
	 * 
	 * @param context
	 */
	public AMapPoiAroundSearchViewBake(Context context) {
		this(context, null);
		mContext = context;
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.layout_poi_around_search_amap, this, true);
		isPoiSearchTypeSelectByUser = false;
		// initView();
	}

	/**
	 * 
	 * @param context
	 * @param attrs
	 */
	public AMapPoiAroundSearchViewBake(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void onCreate(Bundle savedInstanceState) {
		Logger.d(TAG, "onCreate-----");
		initView(savedInstanceState);
	}

	private void initView(Bundle savedInstanceState) {
		mapView = (MapView) findViewById(R.id.map);
		mapView.setOnClickListener(mOnClickListener);
		mapView.onCreate(savedInstanceState);// 此方法必须重写

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

		Button locationButton = (Button) findViewById(R.id.locationButton);
		locationButton.setOnClickListener(mOnClickListener);
		mSearchButton = (ImageView) findViewById(R.id.searchButton);
		mSearchButton.setOnClickListener(mOnClickListener);
		nextButton = (Button) findViewById(R.id.nextButton);
		nextButton.setOnClickListener(mOnClickListener);
		nextButton.setClickable(false);// 默认下一页按钮不可点

		getCurrentLocation();
		init();
	}

	public void onResume() {
		Logger.d(TAG, "onResume-----");
		if (null != mapView) {
			mapView.onResume();
		}
	};

	public void onSaveInstanceState(Bundle outState) {
		Logger.d(TAG, "onSaveInstanceState-----");
		if (null != mapView) {
			mapView.onSaveInstanceState(outState);
		}

	}

	public void onPause() {
		Logger.d(TAG, "onPause-----");
		if (null != mapView) {
			mapView.onPause();
			// deactivate();
		}
	}

	public void onDestroy() {
		Logger.d(TAG, "onDestroy-----");
		if (null != mapView) {
			mapView.onDestroy();
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
			mFromCity = mLocationInfo.getCity();
			mFromAddress = mLocationInfo.getAddress();
			Logger.d(TAG, "getCurrentLocation--mFromAddress = " + mFromAddress
					+ "; mFromLat = " + mFromLat + "; mFromLng = " + mFromLng);

			mLatLonPoint = new LatLonPoint(mLocationInfo.getLatitude(),
					mLocationInfo.getLongitude());
		} else {
			// 默认西单广场
			mLatLonPoint = new LatLonPoint(39.908127, 116.375257);
			mFromCity = "北京市";
			mFromAddress = "西单";
		}
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			Logger.d(TAG, "init------Begin");
			aMap = mapView.getMap();
			setUpMap();
			setSelectType();
			// Button locationButton = (Button)
			// findViewById(R.id.locationButton);
			// locationButton.setOnClickListener(mOnClickListener);
			// ImageView searchButton = (ImageView)
			// findViewById(R.id.searchButton);
			// searchButton.setOnClickListener(mOnClickListener);
			// nextButton = (Button) findViewById(R.id.nextButton);
			// nextButton.setOnClickListener(mOnClickListener);
			// nextButton.setClickable(false);// 默认下一页按钮不可点

			MarkerOptions mOptions = new MarkerOptions();
			mOptions.anchor(0.5f, 1).icon(
					BitmapDescriptorFactory
							.fromResource(R.drawable.ic_amap_point));
			mOptions.position(new LatLng(mLatLonPoint.getLatitude(),
					mLatLonPoint.getLongitude()));
			mOptions.title(mFromAddress + "为中心点，查其周边");

			locationMarker = aMap.addMarker(mOptions);
			locationMarker.showInfoWindow();
			doSearchQuery();
			Logger.d(TAG, "init------End");
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
		aMap.setOnMarkerClickListener(mOnMarkerClickListener);// 添加点击marker监听事件
		aMap.setInfoWindowAdapter(mInfoWindowAdapter);// 添加显示infowindow监听事件

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
					doSearchQuery();
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
		aMap.setOnMarkerClickListener(mOnMarkerClickListener);// 添加点击marker监听事件
		aMap.setInfoWindowAdapter(mInfoWindowAdapter);// 添加显示infowindow监听事件
	}

	/**
	 * 注册监听
	 */
	private void registerListener() {
		aMap.setOnMapClickListener(mOnMapClickListener);
		aMap.setOnMarkerClickListener(mOnMarkerClickListener);
		aMap.setOnInfoWindowClickListener(mOnInfoWindowClickListener);
		aMap.setInfoWindowAdapter(mInfoWindowAdapter);
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
					mPoiAroundSearchViewListener.onRecordingControl(true);
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
			case R.id.locationButton:
				// 点击标记按钮
				aMap.clear();// 清理所有marker
				registerListener();
				break;
			case R.id.searchButton:
				Logger.d(TAG, "searchButton click-----");
				onSearchBtnClick();
				break;
			case R.id.nextButton:
				// 点击下一页按钮
				nextSearch();
				break;
			case R.id.map:
				Logger.d(TAG, "!--->map view click");
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
		doSearchQuery();
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

			} else if (parent == selectType) {
				tsearchType = position;
			}
			nextButton.setClickable(false);// 改变搜索条件，需重新搜索

			/* < XD added 20151023 Begin */
			Logger.d(TAG, "onItemSelected---deepType = " + deepType
					+ "; isPoiSearchTypeSelectByUser = "
					+ isPoiSearchTypeSelectByUser);
			if (isPoiSearchTypeSelectByUser
					&& null != mPoiAroundSearchViewListener) {
				doSearchQuery();
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
			} else if (parent == selectType) {
				tsearchType = 0;
			}
			nextButton.setClickable(false);// 改变搜索条件，需重新搜索
		}

	};

	private OnMarkerClickListener mOnMarkerClickListener = new OnMarkerClickListener() {

		@Override
		public boolean onMarkerClick(Marker marker) {
			if (poiOverlay != null && poiItems != null && poiItems.size() > 0) {
				detailMarker = marker;
				doSearchPoiDetail(poiItems.get(poiOverlay.getPoiIndex(marker))
						.getPoiId());
			}
			return false;
		}

	};

	private OnMapClickListener mOnMapClickListener = new OnMapClickListener() {

		@Override
		public void onMapClick(LatLng latng) {
			locationMarker = aMap.addMarker(new MarkerOptions()
					.anchor(0.5f, 1)
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.ic_amap_point))
					.position(latng).title("点击选择为中心点"));
			locationMarker.showInfoWindow();
		}

	};

	private InfoWindowAdapter mInfoWindowAdapter = new InfoWindowAdapter() {

		@Override
		public View getInfoContents(Marker marker) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public View getInfoWindow(Marker marker) {
			// TODO Auto-generated method stub
			return null;
		}

	};

	private OnInfoWindowClickListener mOnInfoWindowClickListener = new OnInfoWindowClickListener() {

		@Override
		public void onInfoWindowClick(Marker marker) {
			locationMarker.hideInfoWindow();
			mLatLonPoint = new LatLonPoint(
					locationMarker.getPosition().latitude,
					locationMarker.getPosition().longitude);
			locationMarker.destroy();
		}

	};

	private OnPoiSearchListener mOnPoiSearchListener = new OnPoiSearchListener() {

		/**
		 * POI详情回调
		 */
		@Override
		public void onPoiItemDetailSearched(PoiItemDetail result, int rCode) {
			Logger.d(TAG, "onPoiItemDetailSearched---rCode = " + rCode);
			dissmissProgressDialog();// 隐藏对话框
			if (rCode == 0) {
				if (result != null) {// 搜索poi的结果
					if (detailMarker != null) {
						StringBuffer sb = new StringBuffer(result.getSnippet());
						if ((result.getGroupbuys() != null && result
								.getGroupbuys().size() > 0)
								|| (result.getDiscounts() != null && result
										.getDiscounts().size() > 0)) {

							if (result.getGroupbuys() != null
									&& result.getGroupbuys().size() > 0) {// 取第一条团购信息
								sb.append("\n团购："
										+ result.getGroupbuys().get(0)
												.getDetail());
							}
							if (result.getDiscounts() != null
									&& result.getDiscounts().size() > 0) {// 取第一条优惠信息
								sb.append("\n优惠："
										+ result.getDiscounts().get(0)
												.getDetail());
							}
						} else {
							sb = new StringBuffer("地址：" + result.getSnippet()
									+ "\n电话：" + result.getTel() + "\n类型："
									+ result.getTypeDes());
						}
						// 判断poi搜索是否有深度信息
						if (result.getDeepType() != null) {
							// 如果大家需要深度信息，可以查看下面的代码，现在界面上未显示相关代码
							// sb = getDeepInfo(result, sb);
							detailMarker.setSnippet(sb.toString());
						} else {
							ToastUtil.show(mContext, "此Poi点没有深度信息");
							Logger.d(TAG,
									"onPoiItemDetailSearched--this poi has no deep info.");
						}
					}

				} else {
					Logger.d(TAG,
							"onPoiItemDetailSearched--AMap no search result");
					// ToastUtil.show(mContext, R.string.amap_no_result);
				}
			} else if (rCode == 27) {
				ToastUtil.show(mContext, R.string.amap_error_network);
			} else if (rCode == 32) {
				ToastUtil.show(mContext, R.string.amap_error_key);
			} else {
				ToastUtil.show(mContext,
						mContext.getString(R.string.amap_error_other) + rCode);
			}
		}

		/**
		 * POI搜索回调方法
		 */
		@Override
		public void onPoiSearched(PoiResult result, int rCode) {
			Logger.d(TAG, "onPoiSearched---rCode = " + rCode);
			dissmissProgressDialog();// 隐藏对话框
			if (rCode == 0) {
				if (result != null && result.getQuery() != null) {// 搜索poi的结果
					if (result.getQuery().equals(query)) {// 是否是同一条
						poiResult = result;
						poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
						List<SuggestionCity> suggestionCities = poiResult
								.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
						if (poiItems != null && poiItems.size() > 0) {
							aMap.clear();// 清理之前的图标
							poiOverlay = new PoiOverlay(aMap, poiItems);
							poiOverlay.removeFromMap();
							poiOverlay.addToMap();
							poiOverlay.zoomToSpan();

							nextButton.setClickable(true);// 设置下一页可点
						} else if (suggestionCities != null
								&& suggestionCities.size() > 0) {
							showSuggestCity(suggestionCities);
						} else {
							// ToastUtil.show(mContext,
							// R.string.amap_no_result);
							Logger.d(TAG,
									"onPoiSearched--AMap no search result--");
						}
					}
				} else {
					// ToastUtil.show(mContext, R.string.amap_no_result);
					Logger.d(TAG, "onPoiSearched--AMap no search result.");
				}
			} else if (rCode == 27) {
				ToastUtil.show(mContext, R.string.amap_error_network);
			} else if (rCode == 32) {
				ToastUtil.show(mContext, R.string.amap_error_key);
			} else {
				ToastUtil.show(mContext,
						mContext.getString(R.string.amap_error_other) + rCode);
			}
		}

	};

	/**
	 * poi没有搜索到数据，返回一些推荐城市的信息
	 */
	private void showSuggestCity(List<SuggestionCity> cities) {
		String infomation = "推荐城市\n";
		for (int i = 0; i < cities.size(); i++) {
			infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
					+ cities.get(i).getCityCode() + "城市编码:"
					+ cities.get(i).getAdCode() + "\n";
		}
		ToastUtil.show(mContext, infomation);

	}

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(mContext);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(false);
		progDialog.setMessage("正在搜索中");
		progDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	/**
	 * 开始进行poi搜索
	 */
	protected void doSearchQuery() {
		// showProgressDialog();// 显示进度框
		aMap.setOnMapClickListener(null);// 进行poi搜索时清除掉地图点击事件
		currentPage = 0;
		// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
		Logger.d(TAG, "doSearchQuery : " + deepType);
		query = new PoiSearch.Query("", deepType, mFromCity);
		query.setPageSize(10);// 设置每页最多返回多少条poiitem
		query.setPageNum(currentPage);// 设置查第一页

		searchType = tsearchType;
		checkSearchType(searchType);

		if (mLatLonPoint != null) {
			Logger.d(TAG,
					"doSearchQuery---mLatLonPoint is not null, new PoiSearch");
			poiSearch = new PoiSearch(mContext, query);
			poiSearch.setOnPoiSearchListener(mOnPoiSearchListener);
			poiSearch.setBound(new SearchBound(mLatLonPoint, 10000, true));// 10km
			Logger.d(TAG,
					"doSearchQuery---mLatLonPoint is not null, searchPOIAsyn--begin");
			// 设置搜索区域为以lp点为圆心，其周围2000米范围
			/*
			 * List<LatLonPoint> list = new ArrayList<LatLonPoint>();
			 * list.add(lp);
			 * list.add(AMapUtil.convertToLatLonPoint(Constants.BEIJING));
			 * poiSearch.setBound(new SearchBound(list));// 设置多边形poi搜索范围
			 */
			poiSearch.searchPOIAsyn();// 异步搜索
			Logger.d(TAG,
					"doSearchQuery---mLatLonPoint is not null, searchPOIAsyn---end");
		}
		Logger.d(TAG, "doSearchQuery-----End");
	}

	private void checkSearchType(int searchType) {
		Logger.d(TAG, "checkSearchType---searchType = " + searchType);
		switch (searchType) {
		case 0:
			// 所有poi
			query.setLimitDiscount(false);
			query.setLimitGroupbuy(false);
			break;
		case 1:
			// 有团购
			query.setLimitGroupbuy(true);
			query.setLimitDiscount(false);
			break;
		case 2:
			// 有优惠
			query.setLimitGroupbuy(false);
			query.setLimitDiscount(true);
			break;
		case 3:
			// 有团购或者优惠
			query.setLimitGroupbuy(true);
			query.setLimitDiscount(true);
			break;
		default:
			break;
		}
	}

	/**
	 * 点击下一页poi搜索
	 */
	public void nextSearch() {
		if (query != null && poiSearch != null && poiResult != null) {
			if (poiResult.getPageCount() - 1 > currentPage) {
				currentPage++;

				query.setPageNum(currentPage);// 设置查后一页
				poiSearch.searchPOIAsyn();
			} else {
				Logger.d(TAG, "nextSearch-----no_result");
				// ToastUtil.show(mContext, R.string.no_result);
			}
		}
	}

	/**
	 * 查单个poi详情
	 * 
	 * @param poiId
	 */
	public void doSearchPoiDetail(String poiId) {
		if (poiSearch != null && poiId != null) {
			poiSearch.searchPOIDetailAsyn(poiId);
		}
	}

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
			mPoiAroundSearchViewListener.onRecordingControl(false);
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.unisound.unicar.gui.view.ISessionView#release()
	 */
	@Override
	public void release() {
		// TODO Auto-generated method stub
		Logger.d(TAG, "release-----");
		if (null != mapView) {
			// mapView.onDestroy();
			mapView = null;
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

		public void onRecordingControl(boolean isStart);

		public void onStartSelectPoiSearchType();

		public void onPoiSearchTypeSelect(String poiSearchType);

		public void onMapViewMove();
	}

}
