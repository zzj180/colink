package com.aispeech.aios.adapter.node;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.aispeech.ailog.AILog;
import com.aispeech.aimusic.AIMusic;
import com.aispeech.aimusic.config.MusicConfig;
import com.aispeech.aimusic.interfa.OnMusicAppListener;
import com.aispeech.aimusic.interfa.OnMusicSearchedListener;
import com.aispeech.aimusic.model.MusicSearchParam;
import com.aispeech.aios.BaseNode;
import com.aispeech.aios.BusClient;
import com.aispeech.aios.adapter.AdapterApplication;
import com.aispeech.aios.adapter.bean.RpcRecall;
import com.aispeech.aios.adapter.config.AiosApi;
import com.aispeech.aios.adapter.config.Configs;
import com.aispeech.aios.adapter.control.UITimer;
import com.aispeech.aios.adapter.control.UITimerTask;
import com.aispeech.aios.adapter.control.UIType;
import com.aispeech.aios.adapter.control.UiEventDispatcher;
import com.aispeech.aios.adapter.ui.MyWindowManager;
import com.aispeech.aios.adapter.util.APPUtil;
import com.aispeech.aios.adapter.util.SendBroadCastUtil;
import com.aispeech.aios.adapter.util.StringUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc 音乐领域对应节点
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class MusicNode extends BaseNode implements OnMusicSearchedListener, OnMusicAppListener {

    private static final String TAG = "AIOS-Adapter-MusicNode";

    private Context mContext;
    private static MusicNode mInstance;

    private RpcRecall mRpcRecall;

    private MusicSearchParam mParam;
    private long timeId = -1;

    private MusicNode() {
        this.mContext = AdapterApplication.getContext();
    }

    public static synchronized MusicNode getInstance() {
        if (mInstance == null) {
            mInstance = new MusicNode();
        }
        return mInstance;
    }

    @Override
    public String getName() {
        return "music";
    }

    @Override
    public void onMessage(String topic, byte[]... parts) throws Exception {
        super.onMessage(topic, parts);
        AILog.i(TAG, topic, parts);
        if (topic.equals(AiosApi.Wakeup.WAKEUP_RESULT)) {//唤醒成功
        } else if (AiosApi.TTS.STATE.equals(topic) && "wait".equals(StringUtil.getEncodedString(parts[0]))) {
            bc.unsubscribe(AiosApi.TTS.STATE);

            bc.publish(AiosApi.Other.UI_CLICK);
        }
    }

    @Override
    public void onJoin() {
        super.onJoin();
        bc.subscribe(AiosApi.Wakeup.WAKEUP_RESULT);
    }

    @Override
    public BusClient.RPCResult onCall(final String url, final byte[]... args) throws Exception {
        AILog.i(TAG, url, args);
        mRpcRecall = new RpcRecall(url, args);

        if (!APPUtil.getInstance().isInstalled(AIMusic.getCurrentMusicPkg())) {
            if (timeId != -1) {
                bc.killTimer(timeId);
            }
            timeId = bc.setTimer(new Runnable() {
                @Override
                public void run() {
                    UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow);
                    bc.subscribe(AiosApi.TTS.STATE);
                    TTSNode.getInstance().play(Configs.MarkedWords.NO_MUSIC);
                }
            }, 500);

            return null;
        }

        //如果没有安装“AIOS音乐”或者“酷我”
        if (url.equals(AiosApi.Music.DOMAIN)) {
            UiEventDispatcher.notifyUpdateUI(UIType.Awake);
        } else {
            if (url.startsWith(AiosApi.Music.SONGS_SELECT)) {
                if (url.endsWith("/select/prevpage")) {//上一页
                    AILog.d(TAG, "POI_SELECT_PREV_PAGE===" + url);
                    if (UiEventDispatcher.isListViewFirstPage()) {
                        return new BusClient.RPCResult(null, "current is the first page");
                    } else {
                        AILog.d(TAG, "next page");
                        UiEventDispatcher.notifyUpdateUI(UIType.ListViewPrePage, 0);
                    }

                } else if (url.endsWith("/select/nextpage")) {//下一页
                    AILog.d(TAG, "POI_SELECT_NEXT_PAGE");
                    if (UiEventDispatcher.isListViewLastPage()) {
                        return new BusClient.RPCResult(null, "current is the last page");
                    } else {
                        AILog.d(TAG, "next page");
                        UiEventDispatcher.notifyUpdateUI(UIType.ListViewNextPage, 0);
                    }
                }
            } else if (!url.equals(AiosApi.Music.DOMAIN)) {
                List<byte[]> bytes = mRpcRecall.getByteList();
                if (AiosApi.Music.PLAY.equals(url) && bytes.size() == 0) {
                    AIMusic.playRandom();
                    UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow, 0);
                    UITimer.getInstance().executeAppTask(new UITimerTask() , UITimer.DELAY_MIDDLE);
                    return null;
                }

                String musicListJson = StringUtil.getEncodedString(bytes.get(0));
                if (AiosApi.Music.PLAY.equals(url)) {
                    onItemSelect(musicListJson);
                } else if (AiosApi.Music.SONG_SEARCH.equals(url)) {//搜索歌曲
                    UiEventDispatcher.notifyUpdateUI(UIType.LoadingUI);
                    AIMusic.onSearchMusic(mRpcRecall.getByteList());
                } else if (AiosApi.Music.SONGS_LIST.equals(url)) {//列表呈现歌曲
                    fillList(musicListJson);
                }
            }
        }

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AIMusic.setOnMusicSearchedListener(this);
        AIMusic.setOnMusicAppListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AIMusic.onDestroy();
    }

    /**
     * 填充悬浮窗列表
     *
     * @param passData 内核返回的悬浮窗音乐结果列表json
     */
    private void fillList(String passData) {

        ArrayList<Object> musicBeanList = AIMusic.getFromKernelReturn(passData);

        if (musicBeanList.size() != 0) {//填充列表
            AILog.i(TAG, "悬浮窗显示音乐列表:" + musicBeanList.toString());

            String artist = mParam.getArtist();
            String name = mParam.getTitle();
            StringBuilder title = new StringBuilder();

            if (!TextUtils.isEmpty(artist) && !TextUtils.isEmpty(name)) {
                title.append(artist).append(" - ").append(name);
            } else if (!TextUtils.isEmpty(artist)) {
                title.append(artist);
            } else if (!TextUtils.isEmpty(name)) {
                title.append(name);
            }
            UiEventDispatcher.notifyUpdateUI(UIType.MusicUI, musicBeanList, title.toString());
        }
    }

    @Override
    public void onSearchSucceed(String musicListJson, MusicSearchParam param) {
        UiEventDispatcher.notifyUpdateUI(UIType.CancelLoadingUI);
        AILog.json(TAG, musicListJson);

        mParam = param;
        getBusClient().publish(AiosApi.Music.SEARCH_RESULT, musicListJson);
    }

    @Override
    public void onMusicAPPNotExist(String packageName) {
        UiEventDispatcher.notifyUpdateUI(UIType.CancelLoadingUI);
        AILog.i(TAG, packageName + "not installed");
        TTSNode.getInstance().play(MusicConfig.MarkedWords.NO_MUSIC);
    }

    /**
     * 悬浮窗结果列表选择一条
     *
     * @param jsonArray 悬浮窗列表项MusicBean对象的Json格式
     */
    public void onItemSelect(String jsonArray) {
        MyWindowManager.getInstance().removeSmallWindow();
        AIMusic.play(jsonArray);
        UITimer.getInstance().executeAppTask(new UITimerTask() , UITimer.DELAY_MIDDLE);
    }


    private List<String> mMusicPathList = new ArrayList<String>() {
        {
//            add("");
        }
    };

    /**
     * 通过启动AIOS-Music这个服务，就会扫描
     */
    private void startLocalMusicInitService() {
        mContext.startService(new Intent("com.aispeech.action.MUSIC_SEARCH"));
        SendBroadCastUtil.getInstance().sendBroadCast("com.aispeech.aios.music.ACTION_SEARCH_MUSICS",
                "pathList", new Gson().toJson(mMusicPathList), 1000);
    }

    /**
     * 通过启动AIOS-Music这个服务，就会监听音乐目录变化
     */
    private void startObserveSDCardService() {
        AILog.i(TAG);
        mContext.startService(new Intent("com.aispeech.aios.music.ACTION_OBSERVE_SD_CARD"));
    }

    /**
     * AIOS启动成功后调用这个方法，启动AIOS音乐的一个服务，
     * 然后在AIOS音乐里面同步音乐进行内核学习，学完后发广播
     * 执行onSynced()方法
     */
    public void onAiosStarted() {
        startLocalMusicInitService();
        startObserveSDCardService();
    }

}
