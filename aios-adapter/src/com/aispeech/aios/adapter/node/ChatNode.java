package com.aispeech.aios.adapter.node;

import android.text.TextUtils;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.BaseNode;
import com.aispeech.aios.BusClient;
import com.aispeech.aios.adapter.config.AiosApi;
import com.aispeech.aios.adapter.config.Configs;
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
public class ChatNode extends BaseNode {

    private static ChatNode mInstance;

    private static final String TAG = "AIOS-Adapter-ChatNode";

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
        if (topic.equals(AiosApi.Other.UI_MIC_CLICK) || topic.equals("wakeup.result") || topic.equals(AiosApi.Other.UI_CLICK)) {
            AudioPlayManager.getInstance().stop();
        } else if (topic.equals("data.chat.question.query.result")) {
            UiEventDispatcher.notifyUpdateUI(UIType.CancelLoadingUI);
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
        bc.subscribe("data.chat.question.query.result");
    }

    @Override
    public BusClient.RPCResult onCall(String s, byte[]... bytes) throws Exception {
        UiEventDispatcher.notifyUpdateUI(UIType.Awake);
        AILog.i(TAG, s, bytes);

        if (s.equals("/chat/question/query")) {
            bc.call("/data/chat/question/query", bytes);
            UiEventDispatcher.notifyUpdateUI(UIType.LoadingUI);
        } else if (s.equals("/chat/play/voice")) {
            String url = StringUtil.getEncodedString(bytes[0]);
            if (!TextUtils.isEmpty(url)) {
                UiEventDispatcher.notifyUpdateUI(UIType.LoadingUI);
                AudioPlayManager.getInstance().registerListener(new AudioPlayManager.PlayListener() {
                    @Override
                    public void onAudioStart() {
                        UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow);
                    }

                    @Override
                    public void onComplete() {
                        AILog.d("onComplete");
                        AudioPlayManager.getInstance().unRegisterListener(this);
                        TTSNode.getInstance().playRpc("chat.play.voice.state", "completed");
                    }
                });
                try {
                    AILog.i(TAG, "download and play audio");
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
        TTSNode.getInstance().playRpc("chat.question.query.result", null, null, Configs.MarkedWords.NET_EXCEPTION);
    }

}
