package com.blive.chat.chatinterface;

import com.blive.chat.chatmodels.Message;

public interface OnMessageItemClick {
    void OnMessageClick(Message message, int position);

    void OnMessageLongClick(Message message, int position);
}
