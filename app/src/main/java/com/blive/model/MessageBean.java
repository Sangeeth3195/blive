package com.blive.model;

/**
 * Created by yt on 2017/12/14/014.
 */

public class MessageBean {
    private String account;
    private String message;
    private int background;
    private boolean beSelf;
    private boolean isWarning;
    private boolean isGift;

    public MessageBean(String account, String message, boolean beSelf,boolean isWarning, boolean isGift) {
        this.account = account;
        this.message = message;
        this.beSelf = beSelf;
        this.isWarning = isWarning;
        this.isGift = isGift;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isBeSelf() {
        return beSelf;
    }

    public void setBeSelf(boolean beSelf) {
        this.beSelf = beSelf;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public boolean isWarning() {
        return isWarning;
    }

    public void setWarning(boolean warning) {
        isWarning = warning;
    }

    public boolean isGift() {
        return isGift;
    }

    public void setGift(boolean gift) {
        isGift = gift;
    }
}
