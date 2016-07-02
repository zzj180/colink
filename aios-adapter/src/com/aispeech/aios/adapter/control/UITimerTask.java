package com.aispeech.aios.adapter.control;

import android.util.Log;

import java.util.TimerTask;

/**
 * Created by Spring on 2016/3/22.
 * to do:
 */
public class UITimerTask extends TimerTask {
    private UITimerTask.TaskListener mListener;

    @Override
    public void run() {
        Log.e("","done Task run");
        mListener.doneTask();
    }

    public UITimerTask() {
        Log.e("Mytimer", "MyTimerTask");
    }

    public void setTaskListener(UITimerTask.TaskListener mListener) {
        this.mListener = mListener;
    }

    public interface TaskListener {
        void doneTask();
    }
}
