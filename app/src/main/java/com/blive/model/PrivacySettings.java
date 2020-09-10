package com.blive.model;

public class PrivacySettings {

    String user_id_hide;
    String age_hide;
    String location_hide;
    String gender_hide;

    public String getIs_the_user_id_hidden() {
        return user_id_hide;
    }

    public void setUser_id_hide(String user_id_hide) {
        this.user_id_hide = user_id_hide;
    }

    public String getIs_the_age_hidden() {
        return age_hide;
    }

    public void setAge_hide(String age_hide) {
        this.age_hide = age_hide;
    }

    public String getIs_the_location_hidden() {
        return location_hide;
    }

    public void setLocation_hide(String location_hide) {
        this.location_hide = location_hide;
    }

    public String getIs_the_gender_hide() {
        return gender_hide;
    }

    public void setGender_hide(String gender_hide) {
        this.gender_hide = gender_hide;
    }

    @Override
    public String toString() {
        return "PrivacySettings{" +
                "user_id_hide='" + user_id_hide + '\'' +
                ", age_hide='" + age_hide + '\'' +
                ", location_hide='" + location_hide + '\'' +
                ", gender_hide='" + gender_hide + '\'' +
                '}';
    }
}
