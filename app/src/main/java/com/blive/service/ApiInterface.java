package com.blive.service;

import com.blive.model.AccountResponse;
import com.blive.model.AccountResponseInapp;
import com.blive.model.ActiveUserResponse;
import com.blive.model.AudienceResponse;
import com.blive.model.DataListResponse;
import com.blive.model.FollowResponse;
import com.blive.model.GenericResponse;
import com.blive.model.GetGlobalPk;
import com.blive.model.GetTarget;
import com.blive.model.GiftResponse;
import com.blive.model.Giftrewards;
import com.blive.model.KaraokeResponse;
import com.blive.model.LanguageResponse;
import com.blive.model.LeaderBoardResponse;
import com.blive.model.NotificationResponse;
import com.blive.model.PinResponse;
import com.blive.model.PkGiftDetailsModel;
import com.blive.model.Pksession;
import com.blive.model.ProfileResponse;
import com.blive.model.Sendnotification;
import com.blive.model.SignupResponse;
import com.blive.model.TopFansResponse;
import com.blive.model.URL;
import com.blive.model.UsersResponse;
import com.blive.model.VersionResponse;
import com.blive.model.VideosResponse;
import com.blive.model.YoutubeVideo;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("appVersion")
    Call<VersionResponse> getAppVersion();

    @GET("account")
    Call<AccountResponse> checkAccount(@Query("email") String email, @Query("mobile") String mobile, @Query("login_domain") String login_domain, @Query("device_id") String device_id);

    @POST("signUp")
    Call<SignupResponse> createAccount(@Query("name") String name, @Query("username") String username, @Query("email") String email, @Query("login_domain") String login_domain, @Query("profile_pic")
            String profile_pic, @Query("mobile") String mobile, @Query("gender") String gender, @Query("referral") String referral,
                                       @Query("device_id") String device_id, @Query("gcm_registration_id") String fcmId);

    @GET("activeUser")
    Call<ActiveUserResponse> getActiveUsers(@Query("gcm_registration_id") String fcmId, @Query("country") String country, @Query("page") String page, @Query("broadcast_type") String broadcast_type,
                                            @Query("id") String id, @Query("user_id") String user_id, @Query("device_id") String device_id, @Query("latitude") String latitude, @Query("longitude") String longitude);

    @GET("universe")
    Call<UsersResponse> getUniverseUsers(@Query("broadcast_type") String broadcast_type, @Query("country") String country, @Query("page") String page, @Query("user_id") String user_id);

    @GET("nearBy")
    Call<UsersResponse> getNearByUsers(@Query("broadcast_type") String broadcast_type, @Query("city") String city, @Query("page") String page, @Query("user_id") String user_id);

    @GET("audienceList")
    Call<UsersResponse> getUsers(@Query("type") String type, @Query("page") String page, @Query("user_id") String user_id);

    @GET("topperList")
    Call<LeaderBoardResponse> getLeaderBoard(@Query("type") String type, @Query("country") String country, @Query("user_id") String user_id);

    @POST("profile")
    Call<ProfileResponse> getProfile(@Query("user_id") String user_id, @Query("follower_id") String followerId);

    @POST("BlockAndUnblock")
    Call<GenericResponse> block(@Query("type") String type, @Query("block_user_id") String block_user_id, @Query("user_id") String user_id);

    @POST("followAndUnfollow")
    Call<FollowResponse> follow(@Query("type") String type, @Query("follower_id") String follower_id, @Query("user_id") String user_id);

    @GET("blockList")
    Call<UsersResponse> getBlockedUsers(@Query("page") String page, @Query("user_id") String user_id);

    @GET("notificationList")
    Call<NotificationResponse> getNotifications(@Query("user_id") String user_id);

    @GET("clearNotification")
    Call<GenericResponse> clearNotification(@Query("user_id") String user_id);

    @POST("search")
    Call<UsersResponse> search(@Query("user_id") String user_id, @Query("page") String page, @Query("searchValue") String searchValue, @Query("filterSearch") String filterSearch, @Query("filterValue") String filterValue);

    @POST("logout")
    Call<GenericResponse> logout(@Query("username") String username);

    @GET("videoList")
    Call<VideosResponse> getVideos(@Query("page") String page, @Query("user_id") String user_id);
//
//    @POST("messageList")
//    Call<UsersResponse> getMessageUsers(@Query("user_id") String user_id);

    @POST("message")
    Call<GenericResponse> message(@Query("messaging_user_id") String messaging_user_id, @Query("user_id") String user_id);

    @POST("qrCode")
    Call<GenericResponse> scanQrCode(@Query("qr_code") String qr_code, @Query("user_id") String user_id);

    @POST("privacy")
    Call<ProfileResponse> updatePrivacy(@Query("user_id") String user_id, @Query("user_id_hidden") String user_id_hidden, @Query("age_hidden") String age_hidden, @Query("dob_hidden") String dob_hidden, @Query("gender_hidden") String gender_hidden, @Query("location_hidden") String location_hidden);

    @POST("userPin")
    Call<PinResponse> updatePin(@Query("pin") String pin, @Query("user_id") String user_id);

    @POST("updateUserDetails")
    Call<ProfileResponse> updateProfile(@Query("user_id") String user_id, @Query("username") String username, @Query("mobile") String mobile, @Query("date_of_birth") String dob, @Query("gender") String gender, @Query("city") String city, @Query("state") String state,
                                        @Query("country") String country, @Query("about_me") String about_me, @Query("hobbies") String hobbies, @Query("carrier") String carrier, @Query("profile_pic") String profile_pic, @Query("profile_pic1") String profile_pic1,
                                        @Query("profile_pic2") String profile_pic2, @Query("reference_user_id") String reference_user_id);

    @GET("getCountries")
    Call<DataListResponse> getCountries();

    @POST("getStates")
    Call<DataListResponse> getStates(@Query("code") String code);

    @POST("getCities")
    Call<DataListResponse> getCities(@Query("code") String code);

    @POST("shareNotification")
    Call<GenericResponse> shareNotification(@Query("user_id") String user_id, @Query("shared_user_id") String shared_user_id);

    @POST("videoLikeAndDelete")
    Call<GenericResponse> video(@Query("user_id") String user_id, @Query("video_id") String video_id, @Query("type") String type);

    @POST("liveStatus")
    Call<GenericResponse> goLive(@Query("user_id") String user_id, @Query("status") String status, @Query("broadcast_type") String broadcast_type, @Query("broadcasting_time") String broadcasting_time, @Query("idle_time") String idel_time,
                                 @Query("actual_broadcasting_time") String actual_broadcasting_time, @Query("latitude") String latitude, @Query("longitude") String longitude);

    @POST("textMute")
    Call<GenericResponse> textMute(@Query("user_id") String user_id, @Query("broadcaster_id") String broadcaster_id, @Query("text_mute") String text_mute);

    @POST("userOffLine")
    Call<GenericResponse> offline(@Query("user_id") String user_id);

    @POST("userOffLine")
    Call<ActiveUserResponse> offlines(@Query("user_id") String user_id);

    @POST("report")
    Call<GenericResponse> report(@Query("user_id") String user_id, @Query("reporter_user_id") String reporter_user_id, @Query("type") String type, @Query("description") String description);

    @POST("videoMute")
    Call<GenericResponse> videoMute(@Query("guest_id") String guest_id, @Query("broadcaster_id") String broadcaster_id, @Query("video_mute") String video_mute);

    @POST("addGuest")
    Call<UsersResponse> addGuest(@Query("user_id") String user_id, @Query("broadcaster_id") String broadcaster_id);

    @POST("removeGuest")
    Call<UsersResponse> removeGuest(@Query("user_id") String user_id, @Query("broadcaster_id") String broadcaster_id);

    @GET("guestUserList")
    Call<UsersResponse> getGuests(@Query("user_id") String user_id, @Query("broadcaster_id") String broadcaster_id);

    @GET("gettingProfile")
    Call<UsersResponse> getAudiences(@Query("user_id") String user_id, @Query("broadcaster_id") String broadcaster_id);

    @GET("giftList")
    Call<GiftResponse> getGifts(@Query("broadcast_type") String broadcast_type, @Query("user_id") String user_id);

    @POST("removingLiveCustomers")
    Call<UsersResponse> removeAudience(@Query("user_id") String user_id, @Query("broadcaster_id") String broadcaster_id, @Query("broadcasting_time") String broadcasting_time);

    @POST("gift")
    Call<GiftResponse> sendGift(@Query("user_id") String user_id, @Query("senderID") String senderID, @Query("receiverID") String receiverID, @Query("giftName") String giftName, @Query("giftValue") String giftValue, @Query("gift_count") String giftCount);

    @GET("userNotification")
    Call<UsersResponse> getUserData(@Query("user_id") String user_id, @Query("broadCasterID") String broadcast_id);
//
//    @GET("getLanguage")
//    Call<LanguageResponse> getLanguages();
//
//    @POST("KaraokeSearch")
//    Call<KaraokeResponse> getKaraokeSongs(@Query("language") String language, @Query("songName") String songName);

    @GET("broadCasterTopperList")
    Call<TopFansResponse> getTopFans(@Query("user_id") String user_id);

    @GET("getBroadcastType")
    Call<UsersResponse> getBroadcastType(@Query("user_id") String user_id);

    @POST("checkIn")
    Call<GenericResponse> checkIn(@Query("user_id") String user_id);

//    @POST("videoPauseAndResume")
//    Call<GenericResponse> sendStreamTime(@Query("user_id") String user_id, @Query("type") String type, @Query("broadCastType") String broadcastType, @Query("viewerCount") String viewersCount);

    @POST("share")
    Call<GenericResponse> share(@Query("user_id") String user_id, @Query("mode") String mode, @Query("domain") String domain);

    @GET("getDeviceDetails")
    Call<GenericResponse> sendDeviceDetails(@Query("user_id") String user_id, @Query("device_id") String device_id, @Query("os") String os, @Query("version") String version);

    @POST("visitorLog")
    Call<UsersResponse> visitorLog(@Query("user_id") String user_id, @Query("broadcaster_id") String broadcast_id, @Query("type") String type);

    @GET("audienceList")
    Call<AudienceResponse> getInvitesList(@Query("type") String type, @Query("page") String page, @Query("user_id") String user_id);
//
//    @POST("general_notification")
//    Call<Sendnotification> sendnotification(@Query("user_id") String userid, @Query("title") String title, @Query("message") String message);

   /* @POST("pkChallenge")
    Call<GenericResponse> pkChallenge(@Query("user_id") String user_id, @Query("guest_user_id") String guest_user_id, @Query("challenge_duration") String challenge_duration, @Query("rematch") String rematch);*/

    @POST("pk_reward")
    Call<Giftrewards> pk_reward(@Query("user_id") String user_id, @Query("gold") String gold);

    @POST("start_pksession")
    Call<Pksession> startPkSession(@Query("pk_broadcaster") String user_id, @Query("pk_guest") String guest_user_id, @Query("pk_channel_name") String pk_channel_name);

    @POST("end_pksession")
    Call<GenericResponse> endPkSession(@Query("pk_broadcaster") String pk_broadcaster, @Query("pk_guest") String pk_guest, @Query("session_id") String session_id);

    @POST("start_pkchallenge")
    Call<GenericResponse> startPKChallenge(@Query("session_id") String user_id, @Query("pk_broadcaster") String guest_user_id, @Query("pk_guest") String challenge_duration, @Query("duration") String duration);

    @POST("end_pkchallenge")
    Call<GenericResponse> endPKChallenge(@Query("session_id") String session_id, @Query("pk_broadcaster") String pk_broadcaster_id, @Query("pk_guest") String pk_guest_id);

    @Headers("Content-Type: application/json")
    @POST("shorten")
    Call<linkshorten> linkshorten(@Body URL long_url);

    @POST("kicking-rule")
    Call<agora> agora(@Body URL long_url);

    @GET("getPkChallengeDetails")
    Call<PkGiftDetailsModel> getDetailsGold(@Query("pk_broadcaster") String requester_user_id, @Query("pk_guest") String requestee_user_id);

//    @POST("profile")
//    Call<ProfileResponse> getProfile1(@Query("user_id") String user_id, @Query("follower_id") String followerId);

//    @GET("getBroadcastType")
//    Call<UsersResponse> getBroadcastType1(@Query("user_id") String user_id);

    @GET("YoutubevideoList")
    Call<YoutubeVideo> getYouTubeVideoList(@Query("page") int page);

    @POST("inApp")
    Call<AccountResponseInapp> getAllPhotos(@Query("user_id") String user_id, @Query("orderId") String orderId, @Query("packageName") String packageName, @Query("productId") String productId, @Query("purchaseTime") String purchaseTime, @Query("purchaseState") String purchaseState, @Query("purchaseToken") String purchaseToken, @Query("acknowledged") String acknowledged);

    @POST("ActiveNotification")
    Call<GenericResponse> activeNotification(@Query("user_id") String user_id, @Query("friend_user_id") String shared_user_id);
    @GET("getTarget")
    Call<GetTarget> getTarget(@Query("user_id") String user_id);
}
