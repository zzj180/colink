package com.aispeech.aios.adapter.node;

import com.aispeech.aios.BaseNode;
import com.aispeech.aios.BusClient;
import com.aispeech.aios.adapter.config.AiosApi;

/**
 * rewrite by shun.zhang 2016-05-02, just a wrapper of SystemNode
 *
 * @desc TTS相关节点
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class TTSNode {
    private static TTSNode mInstance;

    private BaseNode baseNode;
    public static synchronized TTSNode getInstance() {
        if (mInstance == null) {
            mInstance = new TTSNode();
        }
        return mInstance;
    }

    private TTSNode() {
        baseNode = SystemNode.getInstance();
    }

    public boolean start() {
        return baseNode.start();
    }

    private BusClient bc() {
        return baseNode.getBusClient();
    }

    public BusClient getBusClient() {
        return bc();
    }

    /**
     * 语音播放一段文本，优先级默认为3
     * @param text 需要播报的串
     */
    public void play(String text) {
        if (bc() != null) {
            bc().publish(AiosApi.Other.SPEAK, text, "3");
        }
    }

    /**
     * 语音播放一段文本
     * @param text 需要播报的串
     * @param priority 优先级
     */
    public void play(String text, String priority) {
        if (bc() != null) {
            bc().publish(AiosApi.Other.SPEAK, text, priority);
        }
    }

    public void playRpc(String type, String text) {
        if(bc() != null) {
            bc().publish(type, text);
        }
    }

    public void playRpc(String type, String text, String error) {
        if(bc() != null) {
            bc().publish(type, text, error);
        }
    }

    public void playRpc(String type,String part1,String part2,String error){
        if(bc() !=null) {
            bc().publish(type, part1, part2, error);
        }
    }

    public BusClient.RPCResult call(String url, String... args) {
        if (bc() != null) {
            return bc().call(url, args);
        }
        return null;
    }
}
