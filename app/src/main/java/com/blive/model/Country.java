package com.blive.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sans on 13-03-2019.
 **/

public class Country {

    @SerializedName("code")
    private String code;
    @SerializedName("name")
    private String name;
    @SerializedName("dialCode")
    private String dialCode;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDialCode() {
        return dialCode;
    }

    public void setDialCode(String dialCode) {
        this.dialCode = dialCode;
    }

    @Override
    public String toString() {
        return "Country{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", dialCode='" + dialCode + '\'' +
                '}';
    }
}
