package com.unisound.unicar.gui.chat;

/**
 * Chat Object
 * 
 * @author
 * @date 20150820
 */
public class ChatObject {

    private String ask;
    private String answer;

    /**
     * 
     * @param ask
     * @param answer
     */
    public ChatObject(String ask, String answer) {
        this.ask = ask;
        this.answer = answer;
    }

    public String getAskText() {
        return ask;
    }

    public String getAnswerText() {
        return answer;
    }

}
