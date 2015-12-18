package com.unisound.unicar.gui.smsdefault;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Empty HeadlessSmsSendService for default SMS APP
 * 
 * @author xiaodong
 * @date 20150825
 */
public class HeadlessSmsSendService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }


}
