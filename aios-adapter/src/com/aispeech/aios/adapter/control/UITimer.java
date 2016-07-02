package com.aispeech.aios.adapter.control;

import android.util.Log;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.ui.MyWindowManager;
import com.aispeech.aios.adapter.util.APPUtil;

import java.util.Timer;

/**
 * UI优先级定义:
 * a、导航、行车记录同属于最高优先级
 * b、音乐、喜马拉雅、等这类多媒体app同属于第三优先级
 * c、讲故事、讲笑话等这类多媒体播报以及天气、股票、限行等这类即时资讯信息展示同属于第四优先级
 * <p/>
 * 优先级的规则：
 * a、同优先级的UI“互斥”，即最后调用的UI高于之前被调起的。举例：导航先打开，行车记录再打开，那么不会主动调出（回到）导航的UI；
 * 天气播报过程中吊起股票查询，则股票查询的UI干掉天气查询的接口
 * b、低优先级的UI交互完成后，自动调出（回到）最后打开的最高优先级的UI，举例：导航先打开，再打开音乐app，一段时间后；或者查股
 * 票，股票信息播放完成一段时间后，自动回到导航的UI
 * <p/>
 * 等待时间约定（可以根据路测的反馈再修改）：
 * a、股票、天气、限行TTS播报完成后再等待3秒，如3秒内用户不手动点股票、限行的大图，则适配层退出，调出（回到）高优先级的UI；如
 * TTS播报完成后的3秒内用户点开大图，则重新倒计时6秒后退出；如用户6秒内点大图再回到小图界面，则重新及时6秒后退出。
 * b、笑话、故事、音乐，喜马拉雅等，在节目内容开始播放6秒后UI退出，调出（回到）高优先级的UI
 */
public class UITimer extends Timer implements UITimerTask.TaskListener {
    private static final String TAG = "UITimer";

    //延迟时间
    public static int DELAY_SHORT = 3000;
    public static int DELAY_MIDDLE = 6000;
    public static int DELAY_LONG = 15000;

    //计时类型
    private static int TIMER_TYPE_APP = 0;
    private static int TIMER_TYPE_UI = 1;
    private static int TIMER_TYPE_STORY = 2;

    private static UITimer mInstance;

    private UITimerTask mTimerTask;
    private long mTimerDelay = DELAY_SHORT;
    /**计时类型**/
    private int mType = TIMER_TYPE_APP;
    /** 是否点开了大图**/
    private boolean mIsDetails = false;

    public synchronized static UITimer getInstance() {
        if (mInstance == null) {
            mInstance = new UITimer();
        }
        return mInstance;
    }

    public void setTask(UITimerTask mTimerTask) {
        this.mTimerTask = mTimerTask;
    }

    public void setDelay(long delay) {
        this.mTimerDelay = delay;
    }

    private void scheduleTask() {
        if (mTimerTask != null) {
            AILog.d(TAG, "Schedule Task...");
            mTimerTask.setTaskListener(this);
            schedule(mTimerTask, mTimerDelay);
        }
    }

    /**
     * AIOS 自带UI开始计时
     * @param mTimerTask 计时任务
     * @param delay 延迟时间
     * @param isDetails 是否是显示大图
     */
    public void executeUITask(UITimerTask mTimerTask, int delay, boolean isDetails) {
        //如果UI播报完成之前，已经点开大图，则播报完成重新大图计时
        if (mIsDetails) {
            delay = UITimer.DELAY_MIDDLE;
            AILog.d(TAG , "Displaying details , delay DELAY_MIDDLE...");
        }

        mIsDetails = isDetails;
        cancelTask();
        this.mTimerDelay = delay;
        this.mTimerTask = mTimerTask;
        this.mType = TIMER_TYPE_UI;
        scheduleTask();
    }

    /**
     * 开始讲笑话、故事计时，计时完成后可能需要切换到语音伴随悬浮窗
     * @param mTimerTask 计时任务
     * @param delay 延迟时间
     */
    public void executeStoryTask(UITimerTask mTimerTask, int delay) {
        cancelTask();
        this.mTimerDelay = delay;
        this.mTimerTask = mTimerTask;
        this.mType = TIMER_TYPE_STORY;
        scheduleTask();
    }

    /**
     * 开始App计时
     * @param mTimerTask 计时任务
     * @param delay 延迟时间
     */
    public void executeAppTask(UITimerTask mTimerTask, int delay) {
        cancelTask();
        this.mTimerDelay = delay;
        this.mTimerTask = mTimerTask;
        this.mType = TIMER_TYPE_APP;
        scheduleTask();
    }

    public void cancelTask() {
        if (mTimerTask != null && mType != TIMER_TYPE_APP) {
            mTimerTask.cancel();
        }
    }

    @Override
    public void doneTask() {
        AILog.d(TAG, "UITimer  type = " + mType + " , Delay = " + mTimerDelay);
        if (MyWindowManager.getInstance().isHelpOrSettingPage()) {
            AILog.d(TAG, "Help or setting page is showing , do not dismiss...");
            return;
        } else if (mType == TIMER_TYPE_UI && isPriorityActivityExist()) {//自带UI
            AILog.d(TAG, "AIOS UI will dismiss later...");
            mIsDetails = false;
            UiEventDispatcher.notifyUpdateUI(UIType.Awake);
            UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow);
            UiEventDispatcher.notifyUpdateUI(UIType.VehiclerestrictionLargeImage);
        } else if (mType == TIMER_TYPE_STORY && isPriorityActivityExist()) {//闲聊
            AILog.d(TAG, "AIOS story will dismiss later...");
            UiEventDispatcher.notifyUpdateUI(UIType.SwitchVoiceWindow);
        } else {
            AILog.d(TAG, "Switching App...");
            showPriorityActivity();
        }
        mType = TIMER_TYPE_UI;
    }

    private void showPriorityActivity() {
        APPUtil.getInstance().ShowPriorityActivity();
    }

    /**
     * 判断当前的任务栈是否含有比  讲故事/笑话等这类多媒体播报以及天气、股票、限行等这类即时资讯信息展示UI 优先级更高的应用
     */
    private boolean isPriorityActivityExist() {
        return APPUtil.getInstance().isPriorityActivityExist();
    }

}
