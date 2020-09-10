package com.blive.model;

public class ChatMessage {

    int messageOrder;
    Long messageTime;
    Long messageId;
    String channelUrl;
    byte[] messadeData;
    String messagePath;

    public String getMessagePath() {
        return messagePath;
    }

    public void setMessagePath(String messagePath) {
        this.messagePath = messagePath;
    }

    public int getMessageOrder() {
        return messageOrder;
    }

    public void setMessageOrder(int messageOrder) {
        this.messageOrder = messageOrder;
    }

    public Long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(Long messageTime) {
        this.messageTime = messageTime;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getChannelUrl() {
        return channelUrl;
    }

    public void setChannelUrl(String channelUrl) {
        this.channelUrl = channelUrl;
    }

    public byte[] getMessadeData() {
        return messadeData;
    }

    public void setMessadeData(byte[] messadeData) {
        this.messadeData = messadeData;
    }
}
