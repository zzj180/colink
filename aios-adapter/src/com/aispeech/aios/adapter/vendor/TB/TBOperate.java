package com.aispeech.aios.adapter.vendor.TB;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.bean.PoiBean;
import com.aispeech.aios.adapter.config.AiosApi;
import com.aispeech.aios.adapter.config.Configs;
import com.aispeech.aios.adapter.node.HomeNode;
import com.aispeech.aios.adapter.util.APPUtil;
import com.aispeech.aios.adapter.util.SendBroadCastUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hehr on 2015/10/14.
 * 图吧接口
 */
public class TBOperate {

    private Context context;
    private static TBOperate mInstance;
    private boolean isOperate = false;//操作是否成功
    private static String TAG = "AIOS-Adapter-TBOperate";

    public TBOperate(Context context) {
        this.context = context;
    }

    public static synchronized TBOperate getInstance(Context context) {

        if (null == mInstance) {
            mInstance = new TBOperate(context);
        }
        return mInstance;
    }

    /**
     * 退出图吧导航
     */
    public boolean closeMap() {
        if (APPUtil.getInstance().isInstalled(Configs.MapConfig.PACKAGE_TBMAP)) {//先判断图吧地图是否存在
            isOperate = true;
            Intent i = new Intent();
            i.setAction("android.intent.aciton.mapbar.exit.navi");
            i.putExtra("mabar_exit_navi", true);
            context.sendBroadcast(i);
        } else {
            isOperate = false;
        }
        return isOperate;
    }

    /**
     * 开始导航
     */
    public void startNavigation(PoiBean bean) {

        if (APPUtil.getInstance().isInstalled(Configs.MapConfig.PACKAGE_TBMAP)) {//先判断图吧地图是否存在
            try {
                JSONObject passToTuBa = new JSONObject();
                passToTuBa.put("toPoi", bean.getName().toString());
                //                PoiBean g= TransformPositionUtil.bd09_To_Gcj02(bean.getLatitude(), bean.getLongitude());//百度坐标转GCJ02火星坐标
                passToTuBa.put("toLongtitude", String.valueOf(bean.getLongitude()));
                passToTuBa.put("toLatitude", String.valueOf(bean.getLatitude()));
                String info = passToTuBa.toString();
                SendBroadCastUtil.getInstance().sendBroadCast("android.intent.action.SEND_POIINFO", "poi_info", info);
                AILog.i(TAG, " to tuba info:" + info);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "请先安装图吧地图", Toast.LENGTH_LONG).show();
            HomeNode.getInstance().getBusClient().publish(AiosApi.Other.UI_CLICK);//停掉交互
        }
    }

    /**
     * 打开图吧地图
     */
    public void openMap() {
        if (APPUtil.getInstance().isInstalled(Configs.MapConfig.PACKAGE_TBMAP)) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cn = new ComponentName("com.mapbar.android.carnavi", "com.mapbar.android.carnavi.activity.MainActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }

    /**
     * 当前位置，跳转到图吧定位
     */
    public boolean locate() {

        if (APPUtil.getInstance().isInstalled(Configs.MapConfig.PACKAGE_TBMAP)) {
            if (APPUtil.getInstance().isRunning(Configs.MapConfig.PACKAGE_TBMAP)) {
                SendBroadCastUtil.getInstance().sendBroadCast("com.intent.action.req.mypoi", "", "");
            } else {
                openMap();//打开图吧
                isOperate = true;
            }

        } else {
            Toast.makeText(context, "请先安装图吧地图", Toast.LENGTH_LONG).show();
            isOperate = false;
        }
        return isOperate;
    }
}
