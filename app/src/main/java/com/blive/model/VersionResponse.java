package com.blive.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sans on 04-04-2019.
 **/

public class VersionResponse {

    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private Data data;

    public class Data {

        @SerializedName("currentVersionName")
        private String versionName;
        @SerializedName("currentVersionCode")
        private String versionCode;
        @SerializedName("updateType")
        private String updateType;

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public String getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(String versionCode) {
            this.versionCode = versionCode;
        }

        public String getUpdateType() {
            return updateType;
        }

        public void setUpdateType(String updateType) {
            this.updateType = updateType;
        }


    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

}
