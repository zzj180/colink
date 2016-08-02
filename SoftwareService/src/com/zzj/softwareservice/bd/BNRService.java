package com.zzj.softwareservice.bd;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;

import com.baidu.navisdk.remote.BNRemoteVistor;
import com.baidu.navisdk.remote.aidl.BNEventListener;
import com.zzj.softwareservice.L;
import com.zzj.softwareservice.database.DBConstant;
import com.zzj.softwareservice.weather.WeatherController;

public class BNRService extends Service {
	private final static String PATH = "ar_";
	private static final String ACC_STATE = "acc_state";
	private WeatherController mController;
	BNEventListener.Stub mBNEventListener = new BNEventListener.Stub() {

		/**
		 * 辅助诱导图标更新回调
		 * 
		 * @param assitantType
		 *            辅助诱导类型
		 *            {@link com.baidu.navisdk.remote.BNRemoteConstants.AssitantType
		 *            <code>AssitantType</code>}
		 * @param limitedSpeed
		 *            当类型是SpeedCamera和IntervalCamera的时候，会带有限速的值
		 * @param distance
		 *            诱导距离(以米位单位),当距离为0时，表明这个诱导丢失消失
		 */
		@Override
		public void onAssistantChanged(int assistantType, int limitedSpeed,
				int distance) throws RemoteException {
		}

		/**
		 * 服务区更新回调
		 * 
		 * @param serviceArea
		 *            服务区的名字
		 * @param distance
		 *            服务区的距离，当distance为0或者serviceArea为空时，表明服务区消失
		 */
		@Override
		public void onServiceAreaChanged(String serviceArea, int distance) {
		}

		/**
		 * GPS速度变化，在实际应用中，在某些情况下，手机GPS速度与实际车速在相差4km/h左右
		 * 
		 * @param speed
		 *            gps速度，单位km/h
		 * @param latitude
		 *            纬度，GCJ-02坐标
		 * @param longitude
		 *            经度，GCJ-02坐标
		 */
		@Override
		public void onGpsChanged(int speed, double latitude, double longitude) {
		}

		/**
		 * 导航机动点更新
		 * 
		 * @param maneuverName
		 *            下一个机动点名称，具体可以参考官网上，每一个机动点名称对应的图标
		 * @param distance
		 *            距离下一个机动点距离（以米为单位）
		 */
		@Override
		public void onManeuverChanged(final String maneuverName,
				final int distance) {
			ContentValues con = new ContentValues();
			con.put(DBConstant.NaviTable.MANEUVER_IMAGE, PATH + maneuverName);
			con.put(DBConstant.NaviTable.NEXT_ROAD_DISTANCE, distance);
			con.put(DBConstant.NaviTable.ISNAVING, 1);
			getContentResolver().update(DBConstant.NaviTable.CONTENT_URI, con,
					null, null);
		}

		/**
		 * 到达目的地的距离和时间更新
		 * 
		 * @param remainDistance
		 *            到达目的地的剩余距离（以米为单位）
		 * @param remainTime
		 *            到达目的地的剩余时间(以秒为单位)
		 */
		@Override
		public void onRemainInfoChanged(final int remainDistance,
				final int remainTime) {

			ContentValues con = new ContentValues();
			con.put(DBConstant.NaviTable.TOTAL_REMAIN_DISTANCE, remainDistance);
			con.put(DBConstant.NaviTable.TOTAL_REMAIN_TIME, remainTime);
			con.put(DBConstant.NaviTable.ISNAVING, 1);
			getContentResolver().update(DBConstant.NaviTable.CONTENT_URI, con,
					null, null);

		}

		/**
		 * 当前道路名更新
		 * 
		 * @param currentRoadName
		 *            当前路名
		 */
		@Override
		public void onCurrentRoadNameChanged(final String currentRoadName) {
			ContentValues con = new ContentValues();
			con.put(DBConstant.NaviTable.CURRENT_ROADNAME, currentRoadName);
			con.put(DBConstant.NaviTable.ISNAVING, 1);
			getContentResolver().update(DBConstant.NaviTable.CONTENT_URI, con,
					null, null);

		}

		/**
		 * 下一道路名更新
		 * 
		 * @param nextRoadName
		 *            下一个道路名
		 */
		@Override
		public void onNextRoadNameChanged(final String nextRoadName) {
			ContentValues con = new ContentValues();
			con.put(DBConstant.NaviTable.NEXT_ROAD, nextRoadName);
			con.put(DBConstant.NaviTable.ISNAVING, 1);
			getContentResolver().update(DBConstant.NaviTable.CONTENT_URI, con,
					null, null);

		}

		@Override
		public void onNaviEnd() throws RemoteException {

			ContentValues con = new ContentValues();
			con.put(DBConstant.NaviTable.ISNAVING, 0);
			getContentResolver().update(DBConstant.NaviTable.CONTENT_URI, con,
					null, null);

			BNRemoteVistor.getInstance().setOnConnectListener(
					mOncConnectListener);
			BNRemoteVistor.getInstance().connectToBNService(
					getApplicationContext());
			sendBroadcast(new Intent("com.inet.broadcast.stoptnavi"));
		}

		@Override
		public void onNaviStart() throws RemoteException {
			ContentValues con = new ContentValues();
			con.put(DBConstant.NaviTable.ISNAVING, 1);
			getContentResolver().update(DBConstant.NaviTable.CONTENT_URI, con,
					null, null);
			sendBroadcast(new Intent("com.inet.broadcast.startnavi"));
		}

		@Override
		public void onReRoutePlanComplete() throws RemoteException {
		}

		@Override
		public void onRoutePlanYawing() throws RemoteException {

		}

		@Override
		public void onCruiseEnd() throws RemoteException {

		}

		@Override
		public void onCruiseStart() throws RemoteException {
		}

		@Override
		public void onExtendEvent(int arg0, Bundle arg1) throws RemoteException {
		}

		@Override
		public void onGPSLost() throws RemoteException {
		}

		@Override
		public void onGPSNormal() throws RemoteException {
		}

	};

	private BNRemoteVistor.OnConnectListener mOncConnectListener = new BNRemoteVistor.OnConnectListener() {

		@Override
		public void onDisconnect() {

			ContentValues con = new ContentValues();
			con.put(DBConstant.NaviTable.ISNAVING, 0);
			getContentResolver().update(DBConstant.NaviTable.CONTENT_URI, con,
					null, null);

			sendBroadcast(new Intent("com.inet.broadcast.stoptnavi"));

			BNRemoteVistor.getInstance().setOnConnectListener(
					mOncConnectListener);
			BNRemoteVistor.getInstance().connectToBNService(getApplicationContext());

		}

		@Override
		public void onConnectSuccess() {
			try {

				BNRemoteVistor.getInstance().setBNEventListener(mBNEventListener);

			} catch (RemoteException e) {

				e.printStackTrace();

			}

		}

		@Override
		public void onConnectFail(final String reason) {
			L.v(reason);
			try {

				BNRemoteVistor.getInstance().setBNEventListener(
						mBNEventListener);

			} catch (RemoteException e) {

				e.printStackTrace();

			}

		}

	};

	@Override
	public IBinder onBind(Intent intent) {

		return mBNEventListener;

	}
	
	ContentObserver contentObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			boolean acc_state = Settings.System.getInt(getContentResolver(),ACC_STATE, 1)==1;
			if(acc_state){
				mController.startLoc();
			}else{
				mController.stopLoc();
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		mController = new WeatherController(this);
		boolean acc_state = Settings.System.getInt(getContentResolver(),ACC_STATE, 1)==1;
		L.v("acc="+acc_state);
		if(true){
			mController.startLoc();
		}
		
		getContentResolver().registerContentObserver(Settings.System.getUriFor(ACC_STATE), true, contentObserver);
		ContentValues con = new ContentValues();
		con.put(DBConstant.NaviTable.ISNAVING, 0);
		getContentResolver().update(DBConstant.NaviTable.CONTENT_URI, con, null, null);

		BNRemoteVistor.getInstance().setOnConnectListener(mOncConnectListener);

		BNRemoteVistor.getInstance().connectToBNService(getApplicationContext());
	}

	@Override
	public void onDestroy() {
		startService(new Intent(this, BNRService.class));
		getContentResolver().unregisterContentObserver(contentObserver);
		super.onDestroy();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		return START_STICKY;
	}
}
