package com.zzj.colink.fileupload;

import com.zzj.colink.fileupload.FileUpload.IuploadState;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class UploadService extends Service{
	
	
	FileUpload fileUpload;
	String imei;
	boolean isover = true;
	
	private IuploadState iuploadState = new IuploadState() {
		@Override
		public void uploadSuccess() {
			isover = true;
			Log.v("uploadState", "上传成功");
			Toast.makeText(UploadService.this, "上传成功", Toast.LENGTH_LONG).show();
		}
		
		@Override
		public void uploadFail() {
			isover = true;
			Log.v("uploadState", "上传失败");
			Toast.makeText(UploadService.this, "上传失败", Toast.LENGTH_LONG).show();
		}
		
		@Override
		public void connOver() {
			if(!isover)
				Toast.makeText(UploadService.this, "上传失败", Toast.LENGTH_LONG).show();
			isover = true;
			
		}
	};
	@Override
	public void onCreate() {
		super.onCreate();
		fileUpload = new FileUpload(iuploadState );
		imei = ((TelephonyManager)getSystemService("phone")).getDeviceId();
		isover = true;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent!=null){
			if(isover){
				isover = false;
				if(TextUtils.isEmpty(imei)){
					imei = ((TelephonyManager)getSystemService("phone")).getDeviceId();
				}
				if(!TextUtils.isEmpty(imei)){
					String text = intent.getStringExtra("content");
					fileUpload.formUpload("http://www.colinktek-server.com/LogSystem/UploadLogFile", "/mnt/sdcard/log",imei,text);
				}else{
					isover = true;
					Toast.makeText(this, "获取IMEI失败", Toast.LENGTH_SHORT).show();
				}
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
