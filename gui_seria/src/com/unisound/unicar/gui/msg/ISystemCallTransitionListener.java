package com.unisound.unicar.gui.msg;

public interface ISystemCallTransitionListener {
    public void setState(int type);

    public void onControlWakeupSuccess(String wakeupWord);

    public void onUpdateWakeupWordsStatus(String status);

    public void onTalkRecordingException();

    public void onTalkRecordingPrepared();

    public void onTalkRecordingStart();

    public void onTalkRecordingStop();

    public void onTalkResult(String result);

    public void onSessionProtocal(String protocol);

    public void onPlayEnd();

    public void onSendMsg(String msg);

    public void onUpdateVolume(int volume);

    public void onRecognizerTimeout();

    public void onCTTCancel();

    public void onOneShotRecognizerTimeout();

    public void onStartFakeAnimation();

    public void onGetWakeupWords(String wakeupWords);

    public void onClickMainActionButton(int style);

    public void onTalkRecodingIdle();

}
