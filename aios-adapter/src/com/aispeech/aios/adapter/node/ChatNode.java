package com.aispeech.aios.adapter.node;

import android.text.TextUtils;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.BaseNode;
import com.aispeech.aios.BusClient;
import com.aispeech.aios.adapter.config.AiosApi;
import com.aispeech.aios.adapter.config.Configs;
import com.aispeech.aios.adapter.control.UITimer;
import com.aispeech.aios.adapter.control.UITimerTask;
import com.aispeech.aios.adapter.control.UIType;
import com.aispeech.aios.adapter.control.UiEventDispatcher;
import com.aispeech.aios.adapter.util.AudioPlayManager;
import com.aispeech.aios.adapter.util.StringUtil;

import org.json.JSONObject;

/**
 * @desc 闲聊领域节点
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class ChatNode extends BaseNode implements AudioPlayManager.PlayListener{

    private static ChatNode mInstance;

    private static final String TAG = "AIOS-Adapter-ChatNode";
    private String queryType = "";

    public static synchronized ChatNode getInstance() {
        if (mInstance == null) {
            mInstance = new ChatNode();
        }
        return mInstance;
    }

    @Override
    public String getName() {
        return "chat";
    }

    @Override
    public void onMessage(String topic, byte[]... parts) throws Exception {
        super.onMessage(topic, parts);
        AILog.i(TAG, topic, parts);

        for (int i = 0; i < parts.length; i++) {
            AILog.d(TAG, "args[" + i + "]: " + new String(parts[i]));
        }
        if (topic.equals("wakeup.result") || topic.equals(AiosApi.Other.UI_CLICK) || topic.equals(AiosApi.Other.UI_PAUSE)||topic.equals(AiosApi.Other.UI_MIC_CLICK)) {
            cancelTask();
        } else if (topic.equals(AiosApi.Other.UI_MIC_CLICK)) {//停止播放，恢复主悬浮窗并显示
            UITimer.getInstance().cancelTask();
            AudioPlayManager.getInstance().stop();
            AudioPlayManager.getInstance().unRegisterListener(this);
            UiEventDispatcher.notifyUpdateUI(UIType.RestoreMainWindow);
            bc.publish("chat.question.query.result","text","");
        }else if (AiosApi.Player.STATE.equals(topic) && "wait".equals(StringUtil.getEncodedString(parts[0]))) {
            bc.unsubscribe(AiosApi.Player.STATE);
        } else if (topic.equals("data.chat.question.query.result")) {
            AILog.e(TAG , "time : " );
            UiEventDispatcher.notifyUpdateUI(UIType.CancelLoadingUI);
            UiEventDispatcher.notifyUpdateUI("正在为您讲" + queryType);
            int errId = Integer.valueOf(new String(parts[1]));
            if (errId == 0) {
                onResponse(new String(parts[0], "utf-8"));
            } else {
                onErrorResponse();
            }
        }
    }

    @Override
    public void onJoin() {
        super.onJoin();
        bc.subscribe(AiosApi.Other.UI_MIC_CLICK);
        bc.subscribe("wakeup.result");
        bc.subscribe(AiosApi.Other.UI_CLICK);
        bc.subscribe(AiosApi.Other.UI_PAUSE);
        bc.subscribe("data.chat.question.query.result");
    }

    @Override
    public BusClient.RPCResult onCall(String s, byte[]... bytes) throws Exception {
        UiEventDispatcher.notifyUpdateUI(UIType.Awake);
        AILog.i(TAG, s, bytes);

        if (s.equals("/chat/question/query")) {
            AILog.e(TAG , "time : " );
            bc.call("/data/chat/question/query", bytes);
            UiEventDispatcher.notifyUpdateUI(UIType.LoadingUI);
            queryType = new String(bytes[1]);
        } else if (s.equals("/chat/play/voice")) {
            String url = StringUtil.getEncodedString(bytes[0]);
            if (!TextUtils.isEmpty(url)) {
                UiEventDispatcher.notifyUpdateUI(" 正在加载，请稍候");
                AudioPlayManager.getInstance().registerListener(this);
                try {
                    AILog.d(TAG, "download and play audio");
                    AudioPlayManager.getInstance().openAndPlay(StringUtil.getEncodedString(bytes[0]));
                } catch (Exception e) {
                    e.printStackTrace();
                    TTSNode.getInstance().playRpc("chat.play.voice.state", "failed");
                }
            } else {
                UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow);
                TTSNode.getInstance().playRpc("chat.play.voice.state", "failed");
            }
        } else if (s.equals("/chat/stop/voice")) {
            AudioPlayManager.getInstance().stop();
        }
        return null;
    }

    public void onResponse(String response) {
        AILog.json(response);
        try {
            JSONObject resultJson = new JSONObject(response);
            JSONObject object = resultJson.optJSONObject("result");
            if (object != null) {
                String type = object.optString("type");
                String text = object.optString("text");
                if (type.equals("text")) {
                    TTSNode.getInstance().playRpc("chat.question.query.result", type, text);
                } else if (type.equals("audio")) {
                    JSONObject audio = object.optJSONObject("audio");
                    if (audio != null) {
                        if (audio.optString("url") != null) {
                            TTSNode.getInstance().playRpc("chat.question.query.result", "voice", audio.optString("url"));
                        }
                    }
                }
            } else {
                TTSNode.getInstance().playRpc("chat.question.query.result", null, null, Configs.MarkedWords.CANT_NOT_UNDERSTAND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            TTSNode.getInstance().playRpc("chat.question.query.result", null, null, Configs.MarkedWords.CANT_NOT_UNDERSTAND);
            AILog.i(TAG, "parse json failed");
        }
    }

    public void onErrorResponse() {
        AILog.e(TAG, "onErrorResponse() excute");
        TTSNode.getInstance().playRpc("chat.question.query.result", null, null, Configs.MarkedWords.NET_EXCEPTION);
    }

    public void cancelTask() {
        UITimer.getInstance().cancelTask();
        AudioPlayManager.getInstance().stop();
        AudioPlayManager.getInstance().unRegisterListener(this);
        UiEventDispatcher.notifyUpdateUI(UIType.ExitVoiceWindow);
        bc.publish("chat.question.query.result","text","");
    }

    @Override
    public void onAudioStart() {
        UiEventDispatcher.notifyUpdateUI(UIType.CancelLoadingUI);
        UiEventDispatcher.notifyUpdateUI(" 正在为您讲" + queryType);
        UITimer.getInstance().executeStoryTask(new UITimerTask() , UITimer.DELAY_MIDDLE );
    }

    @Override
    public void onComplete() {
        AILog.d("onComplete");
        UiEventDispatcher.notifyUpdateUI(UIType.ExitVoiceWindow);
        AudioPlayManager.getInstance().unRegisterListener(this);
        TTSNode.getInstance().playRpc("chat.play.voice.state", "completed");
    }

    @Override
    public void onError() {
        AILog.e(TAG, "chatNode onError() excute!");
        UiEventDispatcher.notifyUpdateUI(UIType.CancelLoadingUI);
        TTSNode.getInstance().playRpc("chat.play.voice.state", "failed",Configs.MarkedWords.NET_EXCEPTION);
    }
}
