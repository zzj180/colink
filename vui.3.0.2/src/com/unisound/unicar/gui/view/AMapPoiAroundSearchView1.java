/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : AMapPoiAroundSearchView.java
 * @ProjectName : uniCarSolution_dev_xd_20151010
 * @PakageName : com.unisound.unicar.gui.view
 * @version : 1.0
 * @Author : Xiaodong.He
 * @CreateDate : 2015-10-21
 */
package com.unisound.unicar.gui.view;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.utils.ToastUtil;
import com.unisound.unicar.gui.view.ClickEnableSpinner.OnClickSpinnerListener;

/**
 * @author xiaodong
 * @date 20151021
 */
public class AMapPoiAroundSearchView1 extends LinearLayout implements
		ISessionView {

	private static final String TAG = AMapPoiAroundSearchView1.class
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

	private LocationInfo mLocationInfo = null;
	private double mFromLat = 0.0;
	private double mFromLng = 0.0;
	private String mFromCity = "";
	private String mFromAddress = "";

	private static final int MSG_ON_LELECT_TYPE_PINNER_CLICK = 1001;

	private boolean isPoiSearchTypeSelectByUser = false;

	/**
	 * 
	 * must call onCreate() after new AMapPoiAroundSearchView()
	 * 
	 * @param context
	 */
	public AMapPoiAroundSearchView1(Context context) {
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
	public AMapPoiAroundSearchView1(Context context, AttributeSet attrs) {
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
			aMap = mapView.getMap();
			setUpMap();
			setSelectType();
			Button locationButton = (Button) findViewById(R.id.locationButton);
			locationButton.setOnClickListener(mOnClickListener);
			ImageView searchButton = (ImageView) findViewById(R.id.searchButton);
			searchButton.setOnClickListener(mOnClickListener);
			nextButton = (Button) findViewById(R.id.nextButton);
			nextButton.setOnClickListener(mOnClickListener);
			nextButton.setClickable(false);// 默认下一页按钮不可点

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
		selectDeep.setOnFocusChangeListener(mOnFocusChangeListener);
		aMap.setOnMarkerClickListener(mOnMarkerClickListener);// 添加点击marker监听事件
		aMap.setInfoWindowAdapter(mInfoWindowAdapter);// 添加显示infowindow监听事件

	}

	private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.spinnerdeep:
				Logger.d(TAG,
						"mOnFocusChangeListener---spinnerdeep---hasFocus = "
								+ hasFocus);

				break;
			default:
				break;
			}

		}

	};

	private Handler mHanlder = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_ON_LELECT_TYPE_PINNER_CLICK:
				if (null != selectDeep) {
					selectDeep.performClick();
				}
				if (null != mPoiAroundSearchViewListener) {
					mPoiAroundSearchViewListener.onStartSelectPoiSearchType();
				}
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
			mHanlder.sendEmptyMessage(MSG_ON_LELECT_TYPE_PINNER_CLICK);
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
			case R.id.locationButton:
				// 点击标记按钮
				aMap.clear();// 清理所有marker
				registerListener();
				break;
			case R.id.searchButton:
				// 点击搜索按钮
				doSearchQuery();
				if (null != mPoiAroundSearchViewListener) {
					mPoiAroundSearchViewListener
							.onPoiSearchTypeSelect(deepType);
				}
				break;
			case R.id.iv_more_search_keyword:
				Logger.d(TAG, "iv_more_search_key click");

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
						}
					}

				} else {
					ToastUtil.show(mContext, R.string.amap_no_result);
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
							ToastUtil.show(mContext, R.string.amap_no_result);
						}
					}
				} else {
					ToastUtil.show(mContext, R.string.amap_no_result);
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

		switch (searchType) {
		case 0: {// 所有poi
			query.setLimitDiscount(false);
			query.setLimitGroupbuy(false);
		}
			break;
		case 1: {// 有团购
			query.setLimitGroupbuy(true);
			query.setLimitDiscount(false);
		}
			break;
		case 2: {// 有优惠
			query.setLimitGroupbuy(false);
			query.setLimitDiscount(true);
		}
			break;
		case 3: {// 有团购或者优惠
			query.setLimitGroupbuy(true);
			query.setLimitDiscount(true);
		}
			break;
		}

		if (mLatLonPoint != null) {
			poiSearch = new PoiSearch(mContext, query);
			poiSearch.setOnPoiSearchListener(mOnPoiSearchListener);
			poiSearch.setBound(new SearchBound(mLatLonPoint, 10000, true));// 10km
			// 设置搜索区域为以lp点为圆心，其周围2000米范围
			/*
			 * List<LatLonPoint> list = new ArrayList<LatLonPoint>();
			 * list.add(lp);
			 * list.add(AMapUtil.convertToLatLonPoint(Constants.BEIJING));
			 * poiSearch.setBound(new SearchBound(list));// 设置多边形poi搜索范围
			 */
			poiSearch.searchPOIAsyn();// 异步搜索
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		// Logger.d(TAG, "onInterceptTouchEvent---ev--" + ev.getAction());
		if (MotionEvent.ACTION_MOVE == ev.getAction()
				&& mPoiAroundSearchViewListener != null) {
			mPoiAroundSearchViewListener.onMapViewMove();
		}
		return super.onInterceptTouchEvent(ev);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		Logger.d(TAG, "onTouchEvent---action-" + event.getAction());
		return super.onTouchEvent(event);
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

		public void onStartSelectPoiSearchType();

		public void onPoiSearchTypeSelect(String poiSearchType);

		public void onMapViewMove();
	}

}
