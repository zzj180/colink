/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : WaitingSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-2
 */
package com.unisound.unicar.gui.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import cn.yunzhisheng.vui.assistant.WindowService;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.unisound.unicar.gui.data.operation.PoiDataModel;
import com.unisound.unicar.gui.domain.localsearch.AmapLocalSearchClient;
import com.unisound.unicar.gui.domain.localsearch.AmapLocalSearchUtils;
import com.unisound.unicar.gui.domain.localsearch.AsyncFetchDianPingPoiTask;
import com.unisound.unicar.gui.domain.localsearch.DianPing;
import com.unisound.unicar.gui.domain.localsearch.DianPingParameterNames;
import com.unisound.unicar.gui.domain.localsearch.DianPingURLBuilder;
import com.unisound.unicar.gui.location.interfaces.ILocationListener;
import com.unisound.unicar.gui.location.operation.LocationModelProxy;
import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.model.PoiInfo;
import com.unisound.unicar.gui.preference.PrivatePreference;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.preference.UserPerferenceUtil;
import com.unisound.unicar.gui.search.interfaces.IPoiListener;
import com.unisound.unicar.gui.search.operation.POISearchModelProxy;
import com.unisound.unicar.gui.utils.DataModelErrorUtil;
import com.unisound.unicar.gui.utils.ErrorUtil;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.LocalSearchWaitingContentView;
import com.unisound.unicar.gui.view.LocalSearchWaitingContentView.IRouteWaitingContentViewListener;

public class LocalsearchWaitingSession extends BaseSession {

    public static final String TAG = "LocalsearchWaitingSession";

    private LocalSearchWaitingContentView mWaitingContentView = null;
    private LocationInfo mlLocationInfo = null;
    private LocationModelProxy locationModel = null;
    private Context mContext;
    private String category;
    private String keyWord;
    private double mLat;
    private double mLng;
    private String cityCode;
    private IPoiListener poilistener;
    private boolean isSessionCanceled = false;
    private boolean isRespknow = false;
    private AmapLocalSearchClient amapLocalSearchClient;
    
    private String poi;
	private String city;
	private POISearchModelProxy poiSearchModelProxy = null;
	
	private final int RADIUS_DEFALUT = 1000;
	
	private String type = null;
	
	public static String SEARCH_TO_POI = "SEARCH_TO_POI";

    private IRouteWaitingContentViewListener mListener = new IRouteWaitingContentViewListener() {

        @Override
        public void onCancel() {
            Logger.d(TAG, "tyz----LocalsearchWaitingSession---onCancel");
            isSessionCanceled = true;
            sendPoiLocationFailedResp();
            mSessionManagerHandler
                    .sendEmptyMessage(SessionPreference.MESSAGE_WAITING_SESSION_CANCEL);

            if (locationModel != null) {
                locationModel.stopLocate();
            }
            if (poilistener != null) {
                poilistener = null;
            }
        }
    };

    /**
	 * 
	 */
    private void sendPoiLocationFailedResp() {
        Logger.d(TAG, "!--->sendPoiLocationFailedResp----isRespknow = " + isRespknow);
        if (!isRespknow) {
            // if poi data do no send to vui when cancel btn clicked
            // or Poi Location Failed
            // send fail response to vui
            onRespParams("", "FAIL");
            isRespknow = true;
        }
    }

    public LocalsearchWaitingSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
        mContext = context;
    }

    @SuppressWarnings("deprecation")
	public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);

        try {
            JSONObject objectData = jsonProtocol.getJSONObject("data");
            Logger.d(TAG, "putProtocol :" + objectData.toString());
            if(objectData.has("category")){ 
				category = objectData.getString("category");
			} else {
				category = "";
			}
			
			if(objectData.has("currentPoi")){ 
				poi = objectData.getString("currentPoi");
			} else {
				poi = "CURRENT_LOC";
			}
			
			if(objectData.has("city")){
				city = objectData.getString("city");
			} else {
				city ="CURRENT_CITY";
			}
            keyWord = category;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.d(TAG, "putProtocol :" + city +" " +poi +" " + category);
        mAnswer = JsonTool.getJsonValue(jsonProtocol, "ttsAnswer");
        addQuestionViewText(mQuestion);

        if (mWaitingContentView == null) {
            mWaitingContentView = new LocalSearchWaitingContentView(mContext);
        }
        mWaitingContentView.setLisener(mListener);

        mWaitingContentView.setPoiText("正在帮你查找" + category);

        addAnswerView(mWaitingContentView);
        addAnswerViewText(mAnswer);
        poilistener = new IPoiListener() {
            @Override
            public void onPoiSearchResult(List<PoiInfo> infos, ErrorUtil errorUtil) {
                if (infos != null && infos.size() > 0) {
                	
                	if(type == null) {
                		for (int i = 0; i < infos.size(); i++) {
                            Logger.d(TAG, "infos = " + infos.get(i).toString());
                        }
                        if (isSessionCanceled) {
                            if (!isRespknow) {
                                onRespParams(JsonTool.toJson(infos), "FAIL");
                                isRespknow = true;
                            }

                        } else {
                            if (!isRespknow) {
                                onRespParams(JsonTool.toJson(infos));
                                isRespknow = true;
                            }
                        }
                	} else if(SEARCH_TO_POI.equals(type)) {
                		type = null;
                		PoiInfo poi = infos.get(0);
                		searchDianPingPoiAsyn(poi.getLatitude(),poi.getLongitude() , null, null, category, 7 + "", 15, 5000,
                                poilistener);
                		
                	}
                } else {// 得到大众的数据为null时
                    if (isSessionCanceled) {
                        if (!isRespknow) {
                            onRespParams(getJsonRespFromErrorUtil(errorUtil), "FAIL");
                            isRespknow = true;
                        }

                    } else {
                        if (!isRespknow) {
                            onRespParams(getJsonRespFromErrorUtil(errorUtil));
                            isRespknow = true;
                        }

                    }
                }

            }
        };

        if("CURRENT_LOC".equals(poi)) {
			serachNearbyInCurrentPoi();
		} else if(!"CURRENT_LOC".equals(poi) 
				&& ("".equals(city) || city == null || "CURRENT_CITY".equals(city))) {
			
			if(WindowService.mLocationInfo != null) {
				city = WindowService.mLocationInfo.getCity();
				
		
				type = SEARCH_TO_POI;
				poiSearchModelProxy = POISearchModelProxy.getInstance(mContext);
				//开始poi搜索
				PoiDataModel poiDataModel = convert2PoiDataModel(mlLocationInfo, poi, city, category);
				poiSearchModelProxy.startSearch(poiDataModel, poilistener);
			} else {
				locationModel = LocationModelProxy.getInstance(mContext);

				locationModel.setLocationListener(new ILocationListener() {
					@Override
					public void onLocationResult(List<LocationInfo> infos,
							ErrorUtil code) {
					}

					@Override
					public void onLocationChanged(LocationInfo info, ErrorUtil errorUtil) {
						Logger.d(TAG, "onLocationChanged :　info : " + info);
						mlLocationInfo = info;
						if (errorUtil != null) {
							if (isSessionCanceled) {
								if (!isRespknow) {
									onRespParams(getJsonRespFromErrorUtil(errorUtil),"FAIL");
									isRespknow = true;
								}
							}else{
								if (!isRespknow) {
									onRespParams(getJsonRespFromErrorUtil(errorUtil));
									isRespknow = true;
								}
							}
						} else {
							if (mlLocationInfo != null) {
								
								type = SEARCH_TO_POI;
								
								city = mlLocationInfo.getCity();
								poiSearchModelProxy = POISearchModelProxy.getInstance(mContext);
								//开始poi搜索
								PoiDataModel poiDataModel = convert2PoiDataModel(mlLocationInfo, poi, city, category);
								poiSearchModelProxy.startSearch(poiDataModel, poilistener);

							}
						}
					}
				});
			}
		} else {
			type = SEARCH_TO_POI;
			poiSearchModelProxy = POISearchModelProxy.getInstance(mContext);
			//开始poi搜索
			PoiDataModel poiDataModel = convert2PoiDataModel(mlLocationInfo, poi, city, category);
			poiSearchModelProxy.startSearch(poiDataModel, poilistener);
		}
        
    }
    
    private void serachNearbyInCurrentPoi() {
    	if (WindowService.mLocationInfo != null) {
            this.mlLocationInfo = WindowService.mLocationInfo;
            mLat = mlLocationInfo.getLatitude();
            mLng = mlLocationInfo.getLongitude();
            cityCode = mlLocationInfo.getCityCode();
            Logger.d(TAG, "windowService.mLocation != null --- [lat-" + mLat + " lng-" + mLng
                    + " cityCode-" + cityCode);
            AmapLocalSearchUtils amapUtils = new AmapLocalSearchUtils(mContext);
            if (amapUtils.isNeedUseAmap(mContext, keyWord)) {
                amapLocalSearchClient = AmapLocalSearchClient.getInstance(mContext);
                amapLocalSearchClient.searchAMAPPoiAsyn(mLat, mLng, keyWord,
                        amapUtils.getCategory(keyWord), cityCode, mOnPoiSearchListener);
            } else {
                searchDianPingPoiAsyn(mLat, mLng, null, null, category, 7 + "", 15, 5000,
                        poilistener);
            }
        } else {
            locationModel = LocationModelProxy.getInstance(mContext);

            locationModel.setLocationListener(new ILocationListener() {

                @Override
                public void onLocationResult(List<LocationInfo> infos, ErrorUtil code) {}

                @Override
                public void onLocationChanged(LocationInfo info, ErrorUtil errorUtil) {
                    Logger.d(TAG, "onLocationChanged :　info : " + info);
                    mlLocationInfo = info;
                    if (errorUtil != null) {
                        if (isSessionCanceled) {
                            if (!isRespknow) {
                                onRespParams(getJsonRespFromErrorUtil(errorUtil), "FAIL");
                                isRespknow = true;
                            }

                        } else {
                            if (!isRespknow) {
                                onRespParams(getJsonRespFromErrorUtil(errorUtil));
                                isRespknow = true;
                            }
                        }
                    } else {
                        if (mlLocationInfo != null) {
                            mLat = mlLocationInfo.getLatitude();
                            mLng = mlLocationInfo.getLongitude();
                            cityCode = mlLocationInfo.getCityCode();
                            Logger.d(TAG, "location --- [lat-" + mLat + " lng-" + mLng
                                    + " cityCode-" + cityCode);

                            AmapLocalSearchUtils amapUtils = new AmapLocalSearchUtils(mContext);
                            if (amapUtils.isNeedUseAmap(mContext, keyWord)) {
                                amapLocalSearchClient = AmapLocalSearchClient.getInstance(mContext);
                                amapLocalSearchClient.searchAMAPPoiAsyn(mLat, mLng, keyWord,
                                        amapUtils.getCategory(keyWord), cityCode,
                                        mOnPoiSearchListener);
                            } else {
                                searchDianPingPoiAsyn(mLat, mLng, null, null, category, 7 + "", 15,
                                        5000, poilistener);
                            }
                        }
                    }
                }
            });

            locationModel.startLocate();
        }
    	
    }
    
    private PoiDataModel convert2PoiDataModel(LocationInfo mlLocationInfo, String toPoi, String toCity, String category) {
		PoiDataModel poiDataModelTemp = new PoiDataModel();
		
		/*poiDataModelTemp.setLatitude(mlLocationInfo.getLatitude());
		poiDataModelTemp.setLontitude(mlLocationInfo.getLongitude());*/
		poiDataModelTemp.setCity(toCity);
		poiDataModelTemp.setPoi(toPoi);
		poiDataModelTemp.setCategory("");
		poiDataModelTemp.setRadius(RADIUS_DEFALUT);
		
		return poiDataModelTemp;
	}

    private String getJsonRespFromErrorUtil(ErrorUtil errorUtil) {
        JSONObject jsonObject = new JSONObject();
        if(errorUtil == null) return jsonObject.toString();
        try {
            jsonObject.put("locationErrcode", errorUtil.code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    /**
     * 
     * @param respJson
     */
   /* private void sendRespParamsDelay(String respJson) {
        Logger.d(TAG, "!--->sendRespParamsDelay----isSessionCanceled = " + isSessionCanceled
                + "; respJson = " + respJson);
        if (!isSessionCanceled) {
            onRespParamsDelay(respJson);
        } else {
            sendPoiLocationFailedResp();
        }

    }*/

    @Override
    public void release() {
        mWaitingContentView = null;
        poilistener = null;
        super.release();
    }

    private void searchDianPingPoiAsyn(double lat, double lng, String city, String region,
            String category, String sort, int limit, int radius, IPoiListener l) {
        int sortType = DianPing.SORT_DEFAULT;
        // if (IPoiOperate.SORT_DEFAULT.equals(sort)) {
        // sortType = DianPing.SORT_DEFAULT;
        // } else if (IPoiOperate.SORT_RATING.equals(sort)) {
        // sortType = DianPing.SORT_RATING;
        // } else if (IPoiOperate.SORT_DISTANCE.equals(sort)) {
        // sortType = DianPing.SORT_DISTANCE;
        // }

        int ASYNC_TASK_TIMEOUT = 10000;
        int[] DIANPING_APP_KEY = {-50, -59, -62, -63, -65, -65, -65, -69, -70, -72};
        int[] DIANPING_APP_SECRET =
                {-101, -59, -60, -60, -109, -63, -110, -72, -65, -67, -69, -120, -77, -128, -78,
                        -84, -90, -135, -94, -95, -90, -140, -144, -96, -148, -108, -150, -107,
                        -156, -159, -113, -161};

        DianPingURLBuilder builder =
                new DianPingURLBuilder(DianPing.BASE_URL + DianPing.FIND_BUSINESSES,
                        PrivatePreference.decrypt(DIANPING_APP_KEY),
                        PrivatePreference.decrypt(DIANPING_APP_SECRET));
        Map<String, String> paramMap = new HashMap<String, String>();
        if (lat != 0 && lng != 0) {
            paramMap.put(DianPingParameterNames.LATITUDE, String.valueOf(lat));
            paramMap.put(DianPingParameterNames.LONGITUDE, String.valueOf(lng));
        } else {
            if (!TextUtils.isEmpty(city) && city.endsWith("市")) {
                city = city.substring(0, city.lastIndexOf("市"));
            }
            paramMap.put(DianPingParameterNames.CITY, city);
            if (!TextUtils.isEmpty(region)) {
                paramMap.put(DianPingParameterNames.REGION, region);
            }
        }
        if (limit > 0) {
            paramMap.put(DianPingParameterNames.LIMIT, String.valueOf(limit));
        }
        if (radius > 0) {
            paramMap.put(DianPingParameterNames.RADIUS, String.valueOf(radius));
        }
        paramMap.put(DianPingParameterNames.KEYWORD, category);
        paramMap.put(DianPingParameterNames.SORT, String.valueOf(sortType));


        int mapchoose = UserPerferenceUtil.getMapChoose(mContext);

        Logger.d(TAG, "---mapchoose---" + mapchoose);
        if (mapchoose != UserPerferenceUtil.VALUE_MAP_BAIDU) {
            paramMap.put(DianPingParameterNames.OFFSET_TYPE, String.valueOf(1));
        }

        String url = builder.getUrl(paramMap);

        AsyncFetchDianPingPoiTask mFetchDianPingPoiTask = new AsyncFetchDianPingPoiTask();
        try {
            mFetchDianPingPoiTask.execute(url).get(ASYNC_TASK_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            if (l != null) {
                l.onPoiSearchResult(null,
                        DataModelErrorUtil.getErrorUtil(DataModelErrorUtil.SEARCH_POI_TIMEOUT));
            }
        }
        mFetchDianPingPoiTask.setListener(l);
    }

    private OnPoiSearchListener mOnPoiSearchListener = new OnPoiSearchListener() {

        @Override
        public void onPoiSearched(PoiResult arg0, int arg1) {
            if (arg0 != null) {
                int poiSize = arg0.getPois().size();
                Logger.d(TAG, "onPoiSearched : " + poiSize);
                amapLocalSearchClient.onSearchDone();

                if (poiSize == 0) {
                    Logger.d(TAG, "amap poi search no result");
                    ErrorUtil errorUtil =
                            DataModelErrorUtil.getErrorUtil(DataModelErrorUtil.RETURN_NORESULT);
                    amapRespError(errorUtil);
                } else {
                    ArrayList<PoiItem> poiItems = arg0.getPois();
                    for (PoiItem item : poiItems) {
                        Logger.d(TAG, "item : " + item.toString());
                    }
                    List<PoiInfo> infos = amapLocalSearchClient.getPoiInfo(poiItems);
                    amapRespNotNull(infos);
                }
            } else {
                if (arg1 == AmapLocalSearchClient.WAHT_TIMEOUT) {
                    Logger.d(TAG, "amap poi search timeout !");
                    ErrorUtil errorUtil =
                            DataModelErrorUtil
                                    .getErrorUtil(DataModelErrorUtil.RETURN_SEARCH_TIMEOUT);
                    amapRespError(errorUtil);
                } else {
                    Logger.e(TAG, "未知错误");
                    ErrorUtil errorUtil =
                            DataModelErrorUtil.getErrorUtil(DataModelErrorUtil.RETURN_NORESULT);
                    amapRespError(errorUtil);
                }
            }
        }

        @Override
        public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {}
    };

    private void amapRespNotNull(List<PoiInfo> infos) {
        if (isSessionCanceled) {
            if (!isRespknow) {
                onRespParams(JsonTool.toJson(infos), null);
                isRespknow = true;
            }
        } else {
            if (!isRespknow) {
                onRespParams(JsonTool.toJson(infos));
                isRespknow = true;
            }
        }
    }

    private void amapRespError(ErrorUtil errorUtil) {
        if (isSessionCanceled) {
            if (!isRespknow) {
                onRespParams(getJsonRespFromErrorUtil(errorUtil), null);
                isRespknow = true;
            }
        } else {
            if (!isRespknow) {
                onRespParams(getJsonRespFromErrorUtil(errorUtil), null);
                isRespknow = true;
            }
        }
    }

    @Override
    public void onTTSEnd() {
        super.onTTSEnd();
    }
}
