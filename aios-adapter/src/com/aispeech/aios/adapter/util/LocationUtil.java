package com.aispeech.aios.adapter.util;

import android.provider.Settings;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.BusClient;
import com.aispeech.aios.adapter.AdapterApplication;
import com.aispeech.aios.adapter.bean.PoiBean;
import com.aispeech.aios.adapter.config.Configs;
import com.aispeech.aios.adapter.config.Configs.MapConfig;
import com.aispeech.aios.adapter.node.TTSNode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by hehr on 2016/3/10.
 * 获取定位信息
 */
public class LocationUtil {

    private final static String TAG = "LocationUtil";

    /**
     * 获取定位信息
     *
     * @return
     */
    public static PoiBean getLocation() {

        BusClient.RPCResult rpc = TTSNode.getInstance().call("/keys/mylocation", "get");
        if (rpc.retval == null || rpc.retval.length == 0 || new String(rpc.retval).equals("nil")) {
            return null;
        }
        JSONObject location = null;
        PoiBean p = new PoiBean();
        try {
            location = new JSONObject(new String(rpc.retval, "utf-8"));
            AILog.d(TAG, "获取定位信息：rpc.retval " + new String(rpc.retval, "utf-8"));
            p.setName(location.optString("name"));
            p.setCityName(location.optString("city"));
            p.setLatitude(location.optDouble("latitude"));
            p.setLongitude(location.optDouble("longitude"));
            p.setAddress(location.optString("address"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int mapType = Settings.System.getInt(AdapterApplication.getContext().getContentResolver(),"MAP_INDEX", MapConfig.BDDH);
        if (mapType == Configs.MapConfig.BDDH || mapType ==  Configs.MapConfig.BDMAP) {//如果是百度地图或者百度导航
            PoiBean pi = PoiBean.gcj02_To_Bd09(p.getLatitude(), p.getLongitude());
            p.setLongitude(pi.getLongitude());
            p.setLatitude(pi.getLatitude());
        }
        return p;
    }

    /**
     * 写入定位信息  一分钟写入一次
     *
     * @param p
     */
    public static void setLocation(PoiBean p) {
        if (p != null) {
            AILog.i(TAG, "更新定位数据  p.getLongitude():" + p.getLongitude());
            AILog.i(TAG, "更新定位数据  p.getLatitude():" + p.getLatitude());
            AILog.i(TAG, "更新定位数据  p.getAddress():" + p.getAddress());
            AILog.i(TAG, "更新定位数据  p.getCityName():" + p.getCityName());
            AILog.i(TAG, "更新定位数据  p.getName():" + p.getName());
            try {
                JSONObject job = new JSONObject();
                job.put("name", p.getName());
                job.put("city", p.getCityName());
                job.put("latitude", p.getLatitude());
                job.put("longitude", p.getLongitude());
                job.put("address", p.getAddress());
                TTSNode.getInstance().call("/keys/mylocation", "set", job.toString());
                AILog.d(TAG, "/keys/mylocation :" + job.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
