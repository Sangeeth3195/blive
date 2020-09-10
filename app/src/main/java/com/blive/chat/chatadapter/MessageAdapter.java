package com.blive.chat.chatadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.blive.R;
import com.blive.chat.chatinterface.OnMessageItemClick;
import com.blive.chat.chatmodels.AttachmentTypes;
import com.blive.chat.chatmodels.ChatUser;
import com.blive.chat.chatmodels.Message;
import com.blive.chat.chatutil.ChatUtils;
import com.blive.chat.chatviewHolders.BaseMessageViewHolder;
import com.blive.chat.chatviewHolders.MessageAttachmentAudioViewHolder;
import com.blive.chat.chatviewHolders.MessageAttachmentImageViewHolder;
import com.blive.chat.chatviewHolders.MessageAttachmentRecordingViewHolder;
import com.blive.chat.chatviewHolders.MessageAttachmentVideoViewHolder;
import com.blive.chat.chatviewHolders.MessageTextViewHolder;
import com.blive.chat.chatviewHolders.MessageTypingViewHolder;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageAdapter extends RecyclerView.Adapter<BaseMessageViewHolder> {
    private ChatUtils helper;
    private OnMessageItemClick itemClickListener;
    private MessageAttachmentRecordingViewHolder.RecordingViewInteractor recordingViewInteractor;
    private String myId;
    private Context context;
    private ArrayList<Message> messages;
    private View newMessage;
    private HashMap<String, ChatUser> myUsersNameInPhoneMap;
    private ImageView statusImg;
    private RelativeLayout statusLay;
    TextView statusText;

    public static final int MY = 0x00000000;
    public static final int OTHER = 0x0000100;

    public MessageAdapter(Context context, ArrayList<Message> messages, String myId, View newMessage) {
        this.context = context;
        this.messages = messages;
        this.myId = myId;
        this.newMessage = newMessage;
        this.helper = new ChatUtils(context);
        this.myUsersNameInPhoneMap = helper.getCacheMyUsers();

        if (context instanceof OnMessageItemClick) {
            this.itemClickListener = (OnMessageItemClick) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnUserGroupItemClick");
        }

        if (context instanceof MessageAttachmentRecordingViewHolder.RecordingViewInteractor) {
            this.recordingViewInteractor = (MessageAttachmentRecordingViewHolder.RecordingViewInteractor) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement RecordingViewInteractor");
        }
    }

    @Override
    public BaseMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        viewType &= 0x00000FF;
        switch (viewType) {
            case AttachmentTypes.RECORDING:
                return new MessageAttachmentRecordingViewHolder(LayoutInflater
                        .from(context).inflate(R.layout.item_message_attachment_recording, parent,
                                false), itemClickListener, recordingViewInteractor, messages);
            case AttachmentTypes.AUDIO:
                return new MessageAttachmentAudioViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.item_message_attachment_audio, parent, false), itemClickListener, messages);
            case AttachmentTypes.VIDEO:
                return new MessageAttachmentVideoViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.item_message_attachment_video, parent, false), itemClickListener, messages);
            case AttachmentTypes.IMAGE:
                return new MessageAttachmentImageViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.item_message_attachment_image, parent, false), itemClickListener, messages);
            case AttachmentTypes.NONE_TYPING:
                return new MessageTypingViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.item_message_typing, parent, false));
            case AttachmentTypes.NONE_TEXT:
            default:
                return new MessageTextViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.item_message_text, parent, false), newMessage, itemClickListener, messages);
        }
    }

    @Override
    public void onBindViewHolder(BaseMessageViewHolder holder, int position) {
        try {
            holder.setData(messages.get(position), position, myUsersNameInPhoneMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        try {
            if (getItemCount() == 0) {
                return super.getItemViewType(position);
            } else {
                Message message = messages.get(position);
                int userType;
                if (message.getSenderId().equals(myId))
                    userType = MY;
                else
                    userType = OTHER;
                return message.getAttachmentType() | userType;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
