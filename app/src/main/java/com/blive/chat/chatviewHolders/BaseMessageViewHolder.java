package com.blive.chat.chatviewHolders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blive.R;
import com.blive.chat.chatadapter.MessageAdapter;
import com.blive.chat.chatinterface.OnMessageItemClick;
import com.blive.chat.chatmodels.AttachmentTypes;
import com.blive.chat.chatmodels.ChatUser;
import com.blive.chat.chatmodels.Message;
import com.blive.chat.chatutil.ChatUtils;
import com.blive.chat.chatutil.DownloadFileEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class BaseMessageViewHolder extends RecyclerView.ViewHolder {
    protected static int lastPosition;
    public static boolean animate;
    protected static View newMessageView;
    private int attachmentType;
    protected static Context context;
    private static int _48dpInPx = -1;
    private Message message;
    private OnMessageItemClick itemClickListener;
    private TextView time, senderName;
    private ArrayList<Message> messages;
    CardView cardView;
    private LinearLayout linearLayoutMessageText;
    private LinearLayout timeLayout;
    private TextView timeLeft;
    private FrameLayout parentLayout;

    public BaseMessageViewHolder(View itemView, OnMessageItemClick itemClickListener,
                                 ArrayList<Message> messages) {
        super(itemView);
        this.messages = messages;
        if (itemClickListener != null)
            this.itemClickListener = itemClickListener;
        context = itemView.getContext();
        time = itemView.findViewById(R.id.time);
        senderName = itemView.findViewById(R.id.senderName);
        cardView = itemView.findViewById(R.id.card_view);
        linearLayoutMessageText = itemView.findViewById(R.id.ll_parent_message_text);
        timeLayout = itemView.findViewById(R.id.timeLayout);
        timeLeft = itemView.findViewById(R.id.timeLeft);
        parentLayout = itemView.findViewById(R.id.parentLayout);
        if (_48dpInPx == -1) _48dpInPx = ChatUtils.dpToPx(itemView.getContext(), 48);
    }

    public BaseMessageViewHolder(View itemView, int attachmentType, OnMessageItemClick itemClickListener) {
        super(itemView);
        this.itemClickListener = itemClickListener;
        this.attachmentType = attachmentType;
    }

    public BaseMessageViewHolder(View itemView, View newMessage,
                                 OnMessageItemClick itemClickListener, ArrayList<Message> messages) {
        this(itemView, itemClickListener, messages);
        this.itemClickListener = itemClickListener;
        if (newMessageView == null) newMessageView = newMessage;
    }

    protected boolean isMine() {
        return (getItemViewType() & MessageAdapter.OTHER) != MessageAdapter.OTHER;
    }

    public void setData(final Message message, int position, final HashMap<String, ChatUser> myUsersNameInPhoneMap) {
        try {
            this.message = message;

            if (attachmentType == AttachmentTypes.NONE_TYPING)
                return;
            time.setText(ChatUtils.getTime(message.getDate()));
            timeLeft.setText(ChatUtils.getTime(message.getDate()));
            senderName.setVisibility(View.GONE);

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) linearLayoutMessageText.getLayoutParams();
            if (isMine()) {
                timeLayout.setVisibility(View.VISIBLE);
                timeLeft.setVisibility(View.GONE);
                layoutParams.gravity = Gravity.RIGHT;
                layoutParams.leftMargin = _48dpInPx;
                time.setVisibility(View.VISIBLE);
                time.setCompoundDrawablesWithIntrinsicBounds(0, 0, message.isSent() ?
                        (message.isDelivered() ? (message.isReadMsg() ? R.drawable.ic_done_all_blue
                                : R.drawable.ic_done_all_black) : R.drawable.ic_done_black)
                        : R.drawable.ic_waiting, 0);
            } else {
                timeLayout.setVisibility(View.GONE);
                timeLeft.setVisibility(View.VISIBLE);
                time.setVisibility(View.GONE);
                layoutParams.gravity = Gravity.LEFT;
                layoutParams.rightMargin = _48dpInPx;
                //   itemView.startAnimation(AnimationUtils.makeInAnimation(itemView.getContext(), true));
            }

            linearLayoutMessageText.setLayoutParams(layoutParams);

            for (int i = 0; i < messages.size(); i++) {
                if (message.isSelected())
                    parentLayout.setBackgroundColor(Color.parseColor("#CBEBFC"));
                else
                    parentLayout.setBackgroundColor(Color.parseColor("#00000000"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void onItemClick(boolean b) {
        if (itemClickListener != null && message != null) {
            if (b)
                itemClickListener.OnMessageClick(message, getAdapterPosition());
            else
                itemClickListener.OnMessageLongClick(message, getAdapterPosition());
        }
    }

    void broadcastDownloadEvent() {
        Intent intent = new Intent(ChatUtils.BROADCAST_DOWNLOAD_EVENT);
        intent.putExtra("data", new DownloadFileEvent(message.getAttachmentType(),
                message.getAttachment(), getAdapterPosition()));
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}