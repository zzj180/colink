package com.colink.zzj.txzassistant.node;

import android.content.Context;
import android.widget.Toast;

import com.colink.zzj.txzassistant.AdapterApplication;
import com.colink.zzj.txzassistant.util.APPUtil;
import com.colink.zzj.txzassistant.util.Gps;
import com.colink.zzj.txzassistant.util.Logger;
import com.colink.zzj.txzassistant.util.PositionUtil;
import com.colink.zzj.txzassistant.vendor.BD.BDOperate;
import com.colink.zzj.txzassistant.vendor.BDDH.BDDHOperate;
import com.colink.zzj.txzassistant.vendor.GD.GDOperate;
import com.colink.zzj.txzassistant.vendor.KLD.KLDOperate;
import com.txznet.sdk.TXZNavManager;
import com.txznet.sdk.TXZNavManager.NavTool;
import com.txznet.sdk.TXZNavManager.NavToolStatusListener;
import com.txznet.sdk.TXZNavManager.NavToolType;
import com.txznet.sdk.bean.Poi;

/**
 * @desc 导航节点
 * @auth zzj
 * @date 2016-03-17
 */
public class NavigationNode {
	private Context mContext;
	private static NavigationNode mInstance;

	public NavigationNode() {
		this.mContext = AdapterApplication.getContext();
	}

	public void init() {
		TXZNavManager.getInstance().setNavTool(mNavTool);
	}

	public static synchronized NavigationNode getInstance() {
		if (mInstance == null) {
			mInstance = new NavigationNode();
		}
		return mInstance;
	}

	public void setNaviTool(int mapType) {
		switch (mapType) {
		case 0:
			TXZNavManager.getInstance().setNavTool(
					NavToolType.NAV_TOOL_BAIDU_NAV);
			break;
		case 1:
			TXZNavManager.getInstance().setNavTool(
					NavToolType.NAV_TOOL_GAODE_MAP);
			break;
		case 2:
			TXZNavManager.getInstance().setNavTool(
					NavToolType.NAV_TOOL_KAILIDE_NAV);
			break;
		default:
			break;
		}
	}

	NavTool mNavTool = new NavTool() {

		@Override
		public void setStatusListener(NavToolStatusListener listener) {
			// TODO 请记录下导航状态监听器，通过监听器来通知状态给同行者
		}

		@Override
		public void navToLoc(Poi poi) {
			// TXZNavManager.getInstance().navToLoc(poi);
			startNavigation(poi.getLat(), poi.getLng(), poi.getName());

		}

		@Override
		public void navHome() {
			Logger.i("navHome");
			// 已废弃
			TXZNavManager.getInstance().navHome();
		}

		@Override
		public void navCompany() {
			Logger.i("navCompany");
			// 已废弃
			TXZNavManager.getInstance().navCompany();
		}

		@Override
		public boolean isInNav() {
			// TODO 返回真实的导航状态
			return false;
		}

		@Override
		public void exitNav() {
			Logger.i("exitNav");
			GDOperate.getInstance(mContext).closeMap();
			if(APPUtil.getInstance().isInstalled(APPUtil.BD_NAVI_PKG)){
				BDDHOperate.getInstance(mContext).closeMap();
			}
			if(APPUtil.getInstance().isInstalled( APPUtil.BD_MAP_PKG)){
				BDOperate.getInstance(mContext).closeMap();
			}
			if(APPUtil.getInstance().isInstalled(APPUtil.KLD_MAP_PKG)){
				KLDOperate.getInstance(mContext).closeMap();
			}
			if(APPUtil.getInstance().isInstalled(APPUtil.MX_MAP_PKG)){
				APPUtil.forceStopPackage(APPUtil.MX_MAP_PKG, mContext);
			}
			if(APPUtil.getInstance().isInstalled(APPUtil.GG_MAP_PKG)){
				APPUtil.forceStopPackage(APPUtil.GG_MAP_PKG, mContext);
			}
			
		}

		@Override
		public void enterNav() {
			Logger.i("enterNav");
			openMap();
			// TODO 返回导航，在isNav返回true时回到原来的导航界面
			
		}
	};

	/**
	 * 开始导航
	 */
	private void startNavigation(double lat, double lon, String name) {
		if (AdapterApplication.mapType == 1) {// 高德
			GDOperate.getInstance(mContext).startNavigation(lat, lon);
		} else if (AdapterApplication.mapType == 2) {
			KLDOperate.getInstance(mContext).startNavigation(lat, lon, name);
		} else if (AdapterApplication.mapType == 0) {// 百度导航开始导航
			Gps bd09 = PositionUtil.gcj02_To_Bd09(lat,lon);
			BDDHOperate.getInstance(mContext).startNavigation(bd09.getWgLat(), bd09.getWgLon());
		}
	}
	
	private void openMap(){
		if (AdapterApplication.mapType == 1) {// 高德
			try {
				if (APPUtil.getInstance().isInstalled(APPUtil.GD_CARJ_PKG)) {
					APPUtil.lanchApp(mContext, APPUtil.GD_CARJ_PKG);
				}else if (APPUtil.getInstance().isInstalled(APPUtil.GD_CAR_PKG)) {
					APPUtil.lanchApp(mContext, APPUtil.GD_CAR_PKG);
				}else if(APPUtil.getInstance().isInstalled(APPUtil.GD_MAP_PKG)){
					APPUtil.lanchApp(mContext, APPUtil.GD_MAP_PKG);
				}else{
					Toast.makeText(mContext, "未找到高德地图",Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}  else if (AdapterApplication.mapType == 0) {// 百度导航开始导航
			try {
				APPUtil.lanchApp(mContext, APPUtil.BD_NAVI_PKG);
			} catch (Exception e) {
				Toast.makeText(mContext, "未找到百度导航",Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}else if (AdapterApplication.mapType == 2) {
			try {
				APPUtil.lanchApp(mContext, APPUtil.KLD_MAP_PKG);
			} catch (Exception e) {
				Toast.makeText(mContext, "未找到凯立德地图",Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}else if (AdapterApplication.mapType == 3) {
			try {
				APPUtil.lanchApp(mContext, APPUtil.MX_MAP_PKG);
			} catch (Exception e) {
				Toast.makeText(mContext, "未找到美行地图",Toast.LENGTH_SHORT).show();
			}
		}else if (AdapterApplication.mapType == 4) {
			try {
				APPUtil.lanchApp(mContext, APPUtil.GG_MAP_PKG);
			} catch (Exception e) {
				Toast.makeText(mContext, "未找到谷歌地图",Toast.LENGTH_SHORT).show();
			}
		}
	}
}
