package com.blive.constant;

import androidx.annotation.NonNull;

import com.blive.BuildConfig;
import com.blive.session.SessionUser;

public class Constants_api {

    //Base URL
    public static String domain = BuildConfig.api_host_name;
    public static String webDomain = BuildConfig.web_host_name;
    public static String agoraAAPI = " https://api.agora.io/dev/v1";

    public static String updateImage = domain + "updateUserProfile";
    public static String uploadVideo = domain + "video";

    //Web URL's
    public static String aboutUs = webDomain + "Terms/V1/aboutus.php";
    public static String suggestions = webDomain + "Terms/V1/suggestions.php?user_id=";
    public static String level = webDomain + "Terms/V1/level.php?userID=";
    public static String agreement = webDomain + "Terms/V1/streameragreement.php";
    public static String termsAndConditions = webDomain + "Terms/V1/privacypolicy.php";
    public static String wallet = webDomain + "purchase/wallet07.php?user_id=";
    public static String rewards = webDomain + "rewards/Rewards.php?user_id=";
    public static String dailyTask = webDomain + "dailyTask/dailyTask.php?user_id=";
    public static String progress = webDomain + "myProgress/myProgress.php?user_id=";
    public static String assets = webDomain + "myAssets/myasset18.php?user_id=";
    public static String helpAndFAQ = webDomain + "Terms/V1/helpfeedback.php";
    public static String topperBanner = webDomain + "banners/worldtopperbanner.php";
    public static String DailyCheckin = webDomain + "checkin/checkin.php?user_id=";
    public static String Daily_spin = webDomain + "Games/spin/free_spin.php?user_id=";
    public static String freeGiftWeb = webDomain + "freegift/modal1.php";
    public static String treasureBox = webDomain + "treasure/treasure.php";

    public static String topper_1 = "https://s3.ap-south-1.amazonaws.com/blive/others/topper1.webp";
    public static String topper_2 = "https://s3.ap-south-1.amazonaws.com/blive/others/topper2.webp";
    public static String topper_3 = "https://s3.ap-south-1.amazonaws.com/blive/others/topper3.webp";

    // Star Value
    static String starValue = "https://s3.ap-south-1.amazonaws.com/blive/WebpageAsserts/StarLvL/Star";

    //Banner
    /* public static String index1 = webDomain + "banners/Banner1/index.php";*/
    public static String index1 = webDomain + "reactbanner/index.html";

    //Level Up Image
    public static String levelUp = "https://s3.ap-south-1.amazonaws.com/blive/WebpageAsserts/Level_Up01.png";

    // New Mobile User Login
    public static String new_User_Mobile = "https://blive.s3.ap-south-1.amazonaws.com/100th+gift_gif/signup_reward.webp";

    // 100th Free Gift Acheived
    public static String hun_Free_Gift_Acheived = "https://blive.s3.ap-south-1.amazonaws.com/100th+gift_gif/100th_Gift.webp";

    //News
    public static String news = "https://s3.ap-south-1.amazonaws.com/blive/OfferPage/special-offer-button/index.html";

    //!00th FreeGift WebP
    public static String free_Gift_Level_100th = "https://blive.s3.ap-south-1.amazonaws.com/100th+gift_gif/100th_Gift.webp";

    //Guest Frame
    public static String guest_Frame = "https://blive.s3.ap-south-1.amazonaws.com/frames/guetsFrame1.webp";

    // Games
    public static String games = webDomain + "Games/index.php?user_id=";

    //treasure
    public static String getTreasureBox = "https://blive.s3.ap-south-1.amazonaws.com/gifts_V1_Beta/Treasure_Chest.webp";

    //Mid Bannner
    public static String midBanner = webDomain + "banners/Banner1/middle_banner.php";

    //Messages
    public static String messages = webDomain + "message/index.php?user_id=";

    //share API
    public static String shareAPI = webDomain + "link/index.html?user_id=";

    // Notification URL
    public static String notification = webDomain + "notification/index.php";

    // PK Topper List URl
    public static String pk_GiftTopperList = webDomain + "pkgifts_topper/pkGiftsTopper.php";

    // Gift Topper Page
    public static String getTopperList = webDomain + "topper/index.php?user_id=" ;

}

