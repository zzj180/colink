package com.unisound.unicar.gui.application;

import com.baidu.mapapi.SDKInitializer;

import android.app.Application;

public class CrashApplication extends Application { 
    @Override 
    public void onCreate() { 
        super.onCreate(); 
        SDKInitializer.initialize(this);
 //       CrashHandler crashHandler=CrashHandler.getInstance(); 
        //指定Crash时的处理程序  
 //       crashHandler.setCrashHanler(getApplicationContext()); 
    } 
} 