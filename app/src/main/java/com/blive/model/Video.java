package com.blive.model;

import java.io.Serializable;
import java.util.List;

public class Video implements Serializable {

    private String video_path;
    private String name;
    private String profile_pic;
    private String uploadedTime;
    private String created_at;
    private String user_id;
    private String id;
    private String liked;
    private String thumnail;
    private int video_Like_count;
    private int videoPausedPosition;
    private List<Comments> comments;

    public String getVideo_path() {
        return video_path;
    }

    public void setVideo_path(String video_path) {
        this.video_path = video_path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getUploadedTime() {
        return uploadedTime;
    }

    public void setUploadedTime(String uploadedTime) {
        this.uploadedTime = uploadedTime;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumnail() {
        return thumnail;
    }

    public void setThumnail(String thumnail) {
        this.thumnail = thumnail;
    }

    public String getLiked() {
        return liked;
    }

    public void setLiked(String liked) {
        this.liked = liked;
    }

    public int getVideo_Like_count() {
        return video_Like_count;
    }

    public void setVideo_Like_count(int video_Like_count) {
        this.video_Like_count = video_Like_count;
    }

    public int getVideoPausedPosition() {
        return videoPausedPosition;
    }

    public void setVideoPausedPosition(int videoPausedPosition) {
        this.videoPausedPosition = videoPausedPosition;
    }

    public List<Comments> getComments() {
        return comments;
    }

    public void setComments(List<Comments> comments) {
        this.comments = comments;
    }
}
