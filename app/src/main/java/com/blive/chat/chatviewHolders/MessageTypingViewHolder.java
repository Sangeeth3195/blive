package com.blive.chat.chatviewHolders;

import android.view.View;

import com.blive.chat.chatmodels.AttachmentTypes;

public class MessageTypingViewHolder extends BaseMessageViewHolder {
    public MessageTypingViewHolder(View itemView) {
        super(itemView, AttachmentTypes.NONE_TYPING,null);
    }
}
