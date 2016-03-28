package com.colink.zzj.txzassistant.node;

import com.colink.zzj.txzassistant.AdapterApplication;
import com.txznet.sdk.TXZCameraManager;
import com.txznet.sdk.TXZCameraManager.CameraTool;
import com.txznet.sdk.TXZCameraManager.CapturePictureListener;

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
		public boolean capturePicure(long time,
				final CapturePictureListener listener) {
			// TODO 抓拍实现
			AdapterApplication.runOnUiGround(new Runnable() {
				@Override
				public void run() {
					// TODO 保存
					// listener.onSave("/camera/123.jpg");
					// TODO 出错
					listener.onError(TXZCameraManager.CAPTURE_ERROR_NO_CAMERA,
							"没有摄像头");
				}
			}, 2000);
			
			return true;
		}
	};
}
