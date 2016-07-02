package com.aispeech.aios.adapter.node;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.BaseNode;
import com.aispeech.aios.BusClient;
import com.aispeech.aios.adapter.AdapterApplication;
import com.aispeech.aios.adapter.R;
import com.aispeech.aios.adapter.bean.PoiBean;
import com.aispeech.aios.adapter.config.AiosApi;
import com.aispeech.aios.adapter.config.Configs;
import com.aispeech.aios.adapter.config.Configs.MapConfig;
import com.aispeech.aios.adapter.control.UIType;
import com.aispeech.aios.adapter.control.UiEventDispatcher;
import com.aispeech.aios.adapter.util.LocationUtil;
import com.aispeech.aios.adapter.util.MathUtil;
import com.aispeech.aios.adapter.util.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * @desc 附近领域对应节点
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class NearbyNode extends BaseNode {

    private static final String TAG = "AIOS-Adapter-NearbyNode";

    private Context mContext;
    private static NearbyNode mInstance;
    private String searchKey = "";

    /**
     * 查询到的数据填充列表
     * String fillData 待填充数据
     * String title   页面填充标题
     */
    private void fillList(String fillData, String title) throws JSONException {
        if (!TextUtils.isEmpty(fillData)) {
            ArrayList<Object> list = new ArrayList<Object>();
            JSONArray jrr = new JSONArray(fillData);
            for (int i = 0; i < jrr.length(); i++) {
                JSONObject object = jrr.getJSONObject(i);
                PoiBean bean = new PoiBean();
                if (object.has("tel")) {
                    bean.setTelephone(object.getString("tel"));
                }
                if (object.has("latitude")) {
                    bean.setLatitude(object.getDouble("latitude"));
                }
                if (object.has("longitude")) {
                    bean.setLongitude(object.getDouble("longitude"));
                }
                if (object.has("address") && !TextUtils.isEmpty(object.getString("address"))) {
                    bean.setAddress(object.getString("address"));
                } else {//没有address，使用name填充
                    if (object.has("dst_name")) {
                        bean.setAddress(object.getString("dst_name"));
                    }
                }
                if (object.has("distance")) {
                    bean.setDisplayDistance(MathUtil.getInstance().convertToKm(object.getInt("distance")));
                }
                if (object.has("dst_name")) {
                    bean.setName(object.getString("dst_name"));
                }
                if (null != bean) {
                    list.add(bean);
                }
            }
            UiEventDispatcher.notifyUpdateUI(UIType.NearbyUI, list, title);
        }
    }

    private NearbyNode() {
        this.mContext = AdapterApplication.getContext();
    }

    public static synchronized NearbyNode getInstance() {
        if (mInstance == null) {
            mInstance = new NearbyNode();
        }

        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public String getName() {
        return "nearby";
    }

    @Override
    public void onMessage(String topic, byte[]... parts) throws Exception {
        super.onMessage(topic, parts);
        AILog.i(TAG, topic, parts);

        if (topic.equals("data.nearby.poi.search.result")) {
            queryNearbyResult(parts);
        }
    }

    @Override
    public void onJoin() {
        super.onJoin();
        bc.subscribe("data.nearby.poi.search.result");
    }

    @Override
    public BusClient.RPCResult onCall(final String url, final byte[]... args) throws Exception {
        AILog.i(TAG, url, args);

        if (url.equals("nearby")) {
            UiEventDispatcher.notifyUpdateUI(UIType.Awake);
        } else if (url.equals(AiosApi.Nearby.POI_SEARCH)) {//附近搜索
            UiEventDispatcher.notifyUpdateUI(UIType.LoadingUI, 0);
            searchKey = new String(args[0], "utf-8");
            if(LocationUtil.getLocation() == null){  //没有定位数据
                TTSNode.getInstance().play(mContext.getString(R.string.error_loc));
                bc.publish(AiosApi.Other.UI_PAUSE);
                UiEventDispatcher.notifyUpdateUI(mContext.getString(R.string.error_loc));
                UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow, 2000);
            }else {
                String city = LocationUtil.getLocation().getCityName();//如果语音没有解析出来城市，使用定位城市
                if (args.length > 1) { //如果内核解析有城市信息
                    city = new String(args[1], "utf-8");
                }
                AILog.d(TAG, "Nearby searchKey : " + searchKey);
                AILog.d(TAG, "Nearby city : " + city);
                String currentMap = Configs.getMapName(Settings.System.getInt(AdapterApplication.getContext().getContentResolver(),"MAP_INDEX", MapConfig.GDMAP));//读取当前配置地图
                AILog.d(TAG, "当前设置地图： " + currentMap);
                if ("高德地图".equals(currentMap.trim())) {//高德地图
                    bc.call("/data/nearby/poi/search", args[0], "autonav".getBytes(), city.getBytes());
                } else if ("百度地图".equals(currentMap.trim())) {//百度地图
                    bc.call("/data/nearby/poi/search", args[0], "baidu".getBytes(), city.getBytes());
                } else if ("凯立德地图".equals(currentMap.trim())) {//凯立德地图
                    bc.call("/data/nearby/poi/search", args[0], "careland".getBytes(), city.getBytes());
                } else if ("图吧地图".equals(currentMap.trim())) {//图吧地图
                    bc.call("/data/nearby/poi/search", args[0], "mapbar".getBytes(), city.getBytes());
                } else if ("百度导航".equals(currentMap.trim())) {//百度导航
                    bc.call("/data/nearby/poi/search", args[0], "baidu".getBytes(), city.getBytes());
                } else if ("高德地图车机版".equals(currentMap.trim())) {//高德地图车机版
                    bc.call("/data/nearby/poi/search", args[0], "autonav".getBytes(), city.getBytes());
                } else {
                    bc.call("/data/nearby/poi/search", args[0], "autonav".getBytes(), city.getBytes());
                }
            }

        } else if (url.equals(AiosApi.Nearby.POI_LIST)) {//附近搜索结果显示
            try {
                fillList(new String(args[0], "utf-8"), searchKey);//填充列表显示
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (url.equals(AiosApi.Nearby.POI_SELECT_PREV_PAGE)) {//上一页

            if (UiEventDispatcher.isListViewFirstPage()) {//当前已经是第一页
                return new BusClient.RPCResult(null, "current is the first page");
            } else {
                UiEventDispatcher.notifyUpdateUI(UIType.ListViewPrePage, 0);
            }

        } else if (url.equals(AiosApi.Nearby.POI_SELECT_NEXT_PAGE)) {//下一页

            if (UiEventDispatcher.isListViewLastPage()) {//当前已经是最后一页
                return new BusClient.RPCResult(null, "current is the last page");
            } else {
                UiEventDispatcher.notifyUpdateUI(UIType.ListViewNextPage, 0);
            }

        }
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 查询附近数据
     *
     * @param args
     */
    private void queryNearbyResult(byte[]... args) {

        UiEventDispatcher.notifyUpdateUI(UIType.CancelLoadingUI);

        try {
            int errorCode = Integer.parseInt(new String(args[1], "utf-8"));
            switch (errorCode) {
                case 0://有结果
                    bc.publish("nearby.search.result", new String(args[0], "utf-8"));
                    break;
                case 1://没结果
                    bc.publish("nearby.search.result", null, "not found");
                    break;
                case 3://网络条件不佳
                    bc.publish("nearby.search.result", null, "timeout");
                    break;
                case 2://定位失败
                    TTSNode.getInstance().play(mContext.getString(R.string.error_loc));
                    bc.publish(AiosApi.Other.UI_PAUSE);
                    UiEventDispatcher.notifyUpdateUI(mContext.getString(R.string.error_loc));
                    UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow, 2000);
                    break;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
