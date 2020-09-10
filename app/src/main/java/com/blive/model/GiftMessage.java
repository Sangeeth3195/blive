package com.blive.model;

/**
 * Created by sans on 20-11-2018.
 **/

public class GiftMessage {
    private String account,message;

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

    @Override
    public String toString() {
        return "GiftMessage{" +
                "account='" + account + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
