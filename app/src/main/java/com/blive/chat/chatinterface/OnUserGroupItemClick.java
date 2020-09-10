package com.blive.chat.chatinterface;

import android.view.View;

import com.blive.chat.chatmodels.ChatUser;

public interface OnUserGroupItemClick {
    void OnUserClick(ChatUser user, int position, View userImage);
}