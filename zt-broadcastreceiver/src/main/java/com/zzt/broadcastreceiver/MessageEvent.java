package com.zzt.broadcastreceiver;

/**
 * @author: zeting
 * @date: 2025/4/24
 */
public class MessageEvent {
    private String sendMsg;

    public MessageEvent(String sendMsg) {
        this.sendMsg = sendMsg;
    }

    public String getSendMsg() {
        return sendMsg;
    }

    public void setSendMsg(String sendMsg) {
        this.sendMsg = sendMsg;
    }
}
