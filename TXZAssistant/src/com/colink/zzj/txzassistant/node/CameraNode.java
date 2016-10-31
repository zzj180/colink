package com.colink.zzj.txzassistant.node;

import android.content.Intent;

import com.colink.zzj.txzassistant.AdapterApplication;
import com.colink.zzj.txzassistant.util.Logger;
import com.txznet.sdk.TXZCameraManager;
import com.txznet.sdk.TXZCameraManager.CameraTool;
import com.txznet.sdk.TXZCameraManager.CapturePictureListener;
import com.txznet.sdk.TXZCameraManager.CaptureVideoListener;

/**
 * @desc  拍照场景节点
 * @auth zzj
 * @date 2016-03-19
 */
public class CameraNode {
	private static CameraNode mInstance;

	private CameraNode() {
	}

	public static synchronized CameraNode getInstance() {
		if (mInstance == null) {
			mInstance = new CameraNode();
		}
		return mInstance;
	}
	public void init(){
		TXZCameraManager.getInstance().setCameraTool(mCameraTool);
		TXZCameraManager.getInstance().useWakeupCapturePhoto(true);
	}
	
	public static CameraTool mCameraTool = new CameraTool() {

		@Override
		public boolean capturePicure(long time,final CapturePictureListener listener) {
			// TODO 抓拍实现
			Logger.d("long="+time);
				AdapterApplication.runOnUiGround(new Runnable() {
					@Override
					public void run() {
							AdapterApplication.getContext().sendBroadcast(new Intent("KEY_CAMARA_GET_PICTURE"));
							// TODO 保存
							
							//  TODO 出错
							listener.onError(TXZCameraManager.CAPTURE_ERROR_UNKNOW,null);
					}
				}, time);
			
			return true;
		}

		@Override
		public boolean captureVideo(CaptureVideoListener arg0,
				CaptureVideoListener arg1) {
			// TODO Auto-generated method stub
			return false;
		}
	};
}
