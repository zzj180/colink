package com.aispeech.aios.adapter.bean;

import java.util.List;

/**
 * @desc 自定义aios通用反馈语，将会覆盖默认配置
 * @auth AISPEECH
 * @date 2016-01-14
 * @copyright aispeech.com
 */
public class CommonTip {

    private List<String> hi;
    private List<String> bye;
    private List<String> silence;
    private List<String> wakeup_again;
    private List<String> repeat_again;
    private List<String> network_abnomal;
    private List<String> network_abnomal_navi;
    private List<String> bluetooth_disconnect;
    private List<String> contacts_unsync;

    /**
     * 设置启动AIOS时语音播报词
     * @param hi
     */
    public void setHi(List<String> hi) {
        this.hi = hi;
    }

    /**
     * 设置退出AIOS时语音播报词
     * @param bye
     */
    public void setBye(List<String> bye) {
        this.bye = bye;
    }

    /**
     * 设置没有识别到语音时语音播报词
     * @param silence
     */
    public void setSilence(List<String> silence) {
        this.silence = silence;
    }

    /**
     * 设置提醒再次唤醒的语音播报词
     * @param wakeup_again
     */
    public void setWakeupAgain(List<String> wakeup_again) {
        this.wakeup_again = wakeup_again;
    }

    /**
     * 设置提醒再说一次时的语音播报词
     * @param repeat_again
     */
    public void setRepeatAgain(List<String> repeat_again) {
        this.repeat_again = repeat_again;
    }

    /**
     * 设置网络连接异常时的语音播报词
     * @param network_abnomal
     */
    public void setNetworkAbnomal(List<String> network_abnomal) {
        this.network_abnomal = network_abnomal;
    }

    /**
     * 设置导航是网络连接异常时的语音播报词
     * @param network_abnomal_navi
     */
    public void setNetworkAbnomalNavi(List<String> network_abnomal_navi) {
        this.network_abnomal_navi = network_abnomal_navi;
    }

    /**
     * 设置蓝牙断开时的语音播报词
     * @param bluetooth_disconnect
     */
    public void setBluetoothDisconnect(List<String> bluetooth_disconnect) {
        this.bluetooth_disconnect = bluetooth_disconnect;
    }

    /**
     * 设置联系人同步失败时的语音播报词
     * @param contacts_unsync
     */
    public void setContactsUnsync(List<String> contacts_unsync) {
        this.contacts_unsync = contacts_unsync;
    }

}
