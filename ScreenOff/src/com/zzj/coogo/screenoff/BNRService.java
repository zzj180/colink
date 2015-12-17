package com.zzj.coogo.screenoff;

import java.text.DecimalFormat;

import com.baidu.navisdk.remote.BNRemoteVistor;
import com.baidu.navisdk.remote.aidl.BNEventListener;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;

public class BNRService extends Service {
	private Handler handler;
	public static boolean isnavi;
	public static boolean acc_state;
	private final static String PATH = "ar_";
	private int mSpeed;
	BNRBroadCast mScreenOffReceiver;
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
			mSpeed=speed;
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
				handler.post(new Runnable() {
				@Override
				public void run() {
					if (ScrrenoffActivity.screen != null) {
						if(mSpeed<30){
							if(distance < 120){
						/*		Intent navi = new Intent();
								ComponentName component = new ComponentName("com.baidu.navi", "com.baidu.navi.NaviActivity");
								navi.setComponent(component );
								navi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(navi); 
								return;*/
								int resID = getResources().getIdentifier(PATH + maneuverName, "drawable",getApplicationInfo().packageName);
								ScrrenoffActivity.screen.maneuverImage.setBackgroundResource(resID);
							}else{
								startActivity(ScrrenoffActivity.screen.getIntent());
								ScrrenoffActivity.screen.maneuverImage.setBackgroundResource(R.drawable.ar_turn_front);
							}
						}else if(mSpeed<60){
							if(distance < 500){
						/*		Intent navi = new Intent();
								ComponentName component = new ComponentName("com.baidu.navi", "com.baidu.navi.NaviActivity");
								navi.setComponent(component );
								navi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(navi); 
								return;*/
								int resID = getResources().getIdentifier(PATH + maneuverName, "drawable",getApplicationInfo().packageName);
								ScrrenoffActivity.screen.maneuverImage.setBackgroundResource(resID);
							}else{
								startActivity(ScrrenoffActivity.screen.getIntent());
								ScrrenoffActivity.screen.maneuverImage.setBackgroundResource(R.drawable.ar_turn_front);
							}
						}else{
							if(distance < 3000){
								/*Intent navi = new Intent();
								ComponentName component = new ComponentName("com.baidu.navi", "com.baidu.navi.NaviActivity");
								navi.setComponent(component );
								navi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(navi); 
								return;*/
								int resID = getResources().getIdentifier(PATH + maneuverName, "drawable",getApplicationInfo().packageName);
								ScrrenoffActivity.screen.maneuverImage.setBackgroundResource(resID);
							}else{
								startActivity(ScrrenoffActivity.screen.getIntent());
								ScrrenoffActivity.screen.maneuverImage.setBackgroundResource(R.drawable.ar_turn_front);
							}
						}
						if (distance < 1000) {
							ScrrenoffActivity.screen.distanceText.setText(distance + "米");
						} else {
							DecimalFormat df = new DecimalFormat("#.00");
							ScrrenoffActivity.screen.distanceText.setText(df.format(distance * 1.0 / 1000) + "公里");

						}
					}

				}
			});

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
		public void onRemainInfoChanged(final int remainDistance, int remainTime) {
			isnavi = true;
			handler.post(new Runnable() {

				@Override
				public void run() {
					if (ScrrenoffActivity.screen != null) {
						if (remainDistance < 1000) {
							ScrrenoffActivity.screen.remainDistanceText
									.setText(remainDistance + "米");
						} else {
							DecimalFormat df = new DecimalFormat("#.00");
							ScrrenoffActivity.screen.remainDistanceText.setText(df
									.format(remainDistance * 1.0 / 1000) + "公里");
						}
					}

				}
			});
		}

		/**
		 * 当前道路名更新
		 * 
		 * @param currentRoadName
		 *            当前路名
		 */
		@Override
		public void onCurrentRoadNameChanged(final String currentRoadName) {
			handler.post(new Runnable() {

				@Override
				public void run() {
					
					if (ScrrenoffActivity.screen != null)
						
						ScrrenoffActivity.screen.currentRoad.setText(currentRoadName);

				}
				
			});
			
		}

		/**
		 * 下一道路名更新
		 * 
		 * @param nextRoadName
		 *            下一个道路名
		 */
		@Override
		public void onNextRoadNameChanged(final String nextRoadName) {
			handler.post(new Runnable() {

				@Override
				public void run() {
					
					if (ScrrenoffActivity.screen != null)
						
						ScrrenoffActivity.screen.nextRoad.setText(nextRoadName);
					
				}
				
			});
			
		}

		@Override
		public void onNaviEnd() throws RemoteException {
			isnavi = false;
			getSharedPreferences("navishare", MODE_PRIVATE).edit()
					.putBoolean("isnavi", false).commit();
			stopService(new Intent(BNRService.this, SwitchServeice.class));
			handler.post(new Runnable() {

				@Override
				public void run() {
					if (ScrrenoffActivity.screen != null) {
						ScrrenoffActivity.screen.ledView
								.setVisibility(View.VISIBLE);
						ScrrenoffActivity.screen.layout
								.setVisibility(View.GONE);
/*						ScrrenoffActivity.screen.naviButton
								.setVisibility(View.GONE);
*/					}
				}
			});
			BNRemoteVistor.getInstance().setOnConnectListener(mOncConnectListener);
			BNRemoteVistor.getInstance().connectToBNService(getApplicationContext());
			sendBroadcast(new Intent("com.inet.broadcast.stoptnavi"));
		}

		@Override
		public void onNaviStart() throws RemoteException {
			isnavi = true;
			getSharedPreferences("navishare", MODE_PRIVATE).edit().putBoolean("isnavi", true).commit();
			startService(new Intent(BNRService.this, SwitchServeice.class));
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
			isnavi = false;
			getSharedPreferences("navishare", Context.MODE_PRIVATE).edit()
					.putBoolean("isnavi", false).commit();
			stopService(new Intent(BNRService.this, SwitchServeice.class));
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (ScrrenoffActivity.screen != null) {
						ScrrenoffActivity.screen.ledView.setVisibility(View.VISIBLE);
						ScrrenoffActivity.screen.layout.setVisibility(View.GONE);
/*						ScrrenoffActivity.screen.naviButton
								.setVisibility(View.GONE);
*/					}
				}
			});
			sendBroadcast(new Intent("com.inet.broadcast.stoptnavi"));
			BNRemoteVistor.getInstance().setOnConnectListener(mOncConnectListener);
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
			try {
				
				BNRemoteVistor.getInstance().setBNEventListener(mBNEventListener);
				
			} catch (RemoteException e) {
				
				e.printStackTrace();
				
			}
			
		}
		
	};

	@Override
	public IBinder onBind(Intent intent) {
		
		return mBNEventListener;
		
	}

	@Override
	public void onCreate() {
		getSharedPreferences("navishare", MODE_PRIVATE).edit().putBoolean("isnavi", false).commit();
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		
		unregisterReceiver(mScreenOffReceiver);
		
		startService(new Intent(this, BNRService.class));
		
		super.onDestroy();
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		BNRemoteVistor.getInstance().setOnConnectListener(mOncConnectListener);

		BNRemoteVistor.getInstance().connectToBNService(getApplicationContext());
		
		handler = new Handler(getMainLooper());

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		mScreenOffReceiver = new BNRBroadCast();
		registerReceiver(mScreenOffReceiver, filter);
		return START_STICKY;
	}
}
