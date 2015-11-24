package com.unisound.unicar.gui.session;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.unisound.unicar.gui.domain.localsearch.DianPing;
import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.model.PoiInfo;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.LocalSearchView;
import com.unisound.unicar.gui.view.PickBaseView.IPickListener;

/**
 * 附近的“XXX”
 * 
 * @author
 * 
 */
public class LocalSearchShowSession extends BaseSession {

    private static final String TAG = "LacalSearchShowSession";
    private LocalSearchView mLocalView;
    private String category;
    private Context mContext;
    private IPickListener mPickListener;

    public static final int CALL_BACK_CALL = 0;

    public static final int CALL_BACK_NAVI = 1;

    private boolean canBeClick = true;

    private List<String> mDataItemProtocalList = new ArrayList<String>();

    public LocalSearchShowSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
        mContext = context;
    }

    @Override
    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);
        Logger.d(TAG, "tyz--putProtocol");

        try {
            JSONObject objectData = jsonProtocol.getJSONObject("data");
            Logger.d(TAG, "tyz--putProtocol ; data == " + objectData.toString());

            JSONArray datas = objectData.getJSONArray("locationSearch");
            // category = objectData.getString("category");//Error

            Logger.d(TAG, "tyz--putProtocol----category = " + category + ";--datas == " + datas);
            CallbakeLocalSearch listCallback = new CallbakeLocalSearch() {
                @Override
                public void dissMissLocalSessionView(int type, String protocal) {
                    Message localSearchMsg = new Message();
                    if (CALL_BACK_NAVI == type) {
                        // localSearchMsg.what = SessionPreference.MESSAGE_START_LOCALSEARCH_NAVI;
                        Logger.d(TAG, "!--->onClick()----protocal = " + protocal);
                        onUiProtocal(SessionPreference.EVENT_NAME_SELECT_ITEM, protocal);
                    } else {
                        // localSearchMsg.what = SessionPreference.MESSAGE_START_LOCALSEARCH_CALL;
                        // mSessionManagerHandler.sendMessage(localSearchMsg);//消失sessionView的界面，导航页面在主界面
                        Logger.d(TAG, "!--->onClick()----protocal = " + protocal);
                        onUiProtocal(SessionPreference.EVENT_NAME_SELECT_ITEM, protocal);
                    }

                }
            };
            mPickListener = new IPickListener() {

                @Override
                public void onPre() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onPickCancel() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onNext() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onItemPicked(int position) {
                    if (canBeClick) {
                        if (mDataItemProtocalList != null && mDataItemProtocalList.size() > 0) {
                            /* < BUG-2792 XD 20150722 modify Begin */
                            Logger.d(TAG, "!--->onItemPicked()----position = " + position
                                    + "; mDataItemProtocalList" + mDataItemProtocalList.size());
                            if (mDataItemProtocalList.size() > position) {
                                String selectedItem = mDataItemProtocalList.get(position);

                                Logger.d(TAG, "!--->onItemPicked()----selectedItem = "
                                        + selectedItem);
                                onUiProtocal(SessionPreference.EVENT_NAME_SELECT_LOCALSEARCH_ITEM,
                                        selectedItem);
                            } else {
                                Logger.e(TAG, "!--->Error: position >= mDataItemProtocalList size");
                            }
                            /* BUG-2792 XD 20150722 modify End > */
                        }
                        canBeClick = false;
                    }

                }
            };

            mLocalView = new LocalSearchView(mContext);
            mLocalView.setPickListener(mPickListener);
            mLocalView.setListener(listCallback);
            mLocalView.initView(getDatas(datas), category);
            addAnswerView(mLocalView);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadMoreItem() {
        mLocalView.addMoreView();
    }

    private List<PoiInfo> getDatas(JSONArray datas) throws JSONException {
        List<PoiInfo> resultList = new ArrayList<PoiInfo>();
        for (int i = 0; i < datas.length(); i++) {
            PoiInfo poiSearchResult = new PoiInfo();

            JSONObject poiObj = JsonTool.getJSONObject(datas, i);
            // poiSearchResult = (PoiInfo)JsonTool.fromJson(poiObj.toString(), PoiInfo.class);
            JSONArray regionsArr = JsonTool.getJsonArray(poiObj, "region");
            if (regionsArr != null) {
                String[] regions = new String[regionsArr.length()];
                for (int j = 0; j < regionsArr.length(); j++) {
                    regions[j] = regionsArr.getString(j);
                }
                poiSearchResult.setRegions(regions);
            }
            JSONObject local = poiObj.getJSONObject("location");
            double lat = local.getDouble("lat");
            double lng = local.getDouble("lng");
            LocationInfo info = new LocationInfo();
            info.setLatitude(lat);
            info.setLongitude(lng);
            info.setCity(JsonTool.getJsonValue(local, DianPing.CITY));
            info.setType(LocationInfo.GPS_ORIGIN);
            poiSearchResult.setLocationInfo(info);

            poiSearchResult.setTel(JsonTool.getJsonValue(poiObj, "tel"));
            poiSearchResult.setHas_online_reservation(JsonTool.getJsonValue(poiObj,
                    "has_online_reservation", false));
            poiSearchResult.setUrl(JsonTool.getJsonValue(poiObj, "url"));
            poiSearchResult.setId(JsonTool.getJsonValue(poiObj, "id"));
            poiSearchResult.setName(JsonTool.getJsonValue(poiObj, "name"));

            poiSearchResult.setHas_coupon(JsonTool.getJsonValue(poiObj, "has_coupon", false));
            poiSearchResult.setDistance(JsonTool.getJsonValue(poiObj, "distance", 0));
            poiSearchResult.setRating((float) JsonTool.getJsonValue(poiObj, "rate", 0.0));

            JSONArray categoriesArr = JsonTool.getJsonArray(poiObj, "category");
            String[] category = {categoriesArr.get(0).toString()};
            poiSearchResult.setCategories(category);

            poiSearchResult.setSeletItem(JsonTool.getJsonValue(poiObj,
                    SessionPreference.KEY_TO_SELECT, ""));
            poiSearchResult.setCallSelectItem(JsonTool.getJsonValue(poiObj,
                    SessionPreference.KEY_CALL_TO_SELECT, ""));

            mDataItemProtocalList.add(JsonTool.getJsonValue(poiObj,
                    SessionPreference.KEY_TO_SELECT, ""));

            if (regionsArr != null && regionsArr.length() > 1) {
                String[] categoriesArrs = new String[categoriesArr.length()];
                for (int j = 0; j < categoriesArr.length(); j++) {
                    categoriesArrs[j] = categoriesArr.getString(j);
                }
                poiSearchResult.setCategories(categoriesArrs);
            }

            poiSearchResult.setHas_deal(JsonTool.getJsonValue(poiObj, "has_deal", false));
            poiSearchResult.setAvg_price(JsonTool.getJsonValue(poiObj, "avg_price", 0));
            poiSearchResult.setBranchName(JsonTool.getJsonValue(poiObj, "branchName"));
            resultList.add(poiSearchResult);
        }
        if (resultList.size() > 0) {
            return resultList;
        }
        return null;

    }



    @Override
    public void release() {
        super.release();
        Logger.d(TAG, "!--->release-----");
        if (null != mLocalView) {
            mLocalView.release();
        }
    }



    public interface CallbakeLocalSearch {
        void dissMissLocalSessionView(int type, String protocal);
    }

}
