package com.zzt.br.send;

/**
 * @author: zeting
 * @date: 2025/4/24
 */
public class MessageEvent {
    private Integer count;

    public MessageEvent(Integer count) {
        this.count = count;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
