package com.blive.model;
import com.google.gson.annotations.SerializedName;
public class YoutubeResponse {
    @SerializedName("id")
    private String Id;
    @SerializedName("ImageUrl")
    private String ImageUrl;
    @SerializedName("Title")
    private String Title;
    @SerializedName("VideoId")
    private String VideoId;
    public String getId() {
        return Id;
    }
    public void setId(String id) {
        Id = id;
    }
    public String getImageUrl() {
        return ImageUrl;
    }
    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
    public String getTitle() {
        return Title;
    }
    public void setTitle(String title) {
        Title = title;
    }
    public String getVideoId() {
        return VideoId;
    }
    public void setVideoId(String videoId) {
        VideoId = videoId;
    }
}