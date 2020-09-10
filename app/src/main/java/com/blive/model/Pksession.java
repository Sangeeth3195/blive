package com.blive.model;

import com.google.gson.annotations.SerializedName;

public class Pksession {

    @SerializedName("status")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @SerializedName("session_id")
    private String sessionId;
}
