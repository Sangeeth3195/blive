package com.blive.chat.chatviewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blive.R;
import com.blive.chat.chatinterface.OnMessageItemClick;
import com.blive.chat.chatmodels.AttachmentTypes;
import com.blive.chat.chatmodels.ChatUser;
import com.blive.chat.chatmodels.Message;
import com.blive.chat.chatutil.ChatUtils;
import com.blive.chat.chatview.ImageViewerActivity;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageAttachmentImageViewHolder extends BaseMessageViewHolder {
    private ImageView image;
    private LinearLayout ll;
    private ProgressBar myProgressBar;
    private ImageView statusImg;
    private RelativeLayout statusLay;
    private TextView statusText;
    private ArrayList<Message> messages;
    private LinearLayout linearLayoutMessageText;
    private LinearLayout backGround;
    private ImageView pImg;

    public MessageAttachmentImageViewHolder(View itemView, OnMessageItemClick itemClickListener, ArrayList<Message> messages) {
        super(itemView, itemClickListener, messages);
        image = itemView.findViewById(R.id.image);
        ll = itemView.findViewById(R.id.container);
        myProgressBar = itemView.findViewById(R.id.progressBar);
        statusImg = itemView.findViewById(R.id.statusImg);
        statusLay = itemView.findViewById(R.id.statusLay);
        statusText = itemView.findViewById(R.id.statusText);
        linearLayoutMessageText = itemView.findViewById(R.id.ll_parent_message_text);
        backGround = itemView.findViewById(R.id.backGround);
        pImg = itemView.findViewById(R.id.pImg);
        this.messages = messages;

        itemView.setOnClickListener(v ->
                onItemClick(true));

        itemView.setOnLongClickListener(v -> {
            onItemClick(false);
            return true;
        });
    }

    @Override
    public void setData(final Message message, int position, HashMap<String, ChatUser> myUsers) {
        super.setData(message, position, myUsers);

        if (isMine()) {
            pImg.setVisibility(View.VISIBLE);
            Glide.with(context).load(R.drawable.go_live).into(pImg);
            backGround.setBackgroundResource(R.drawable.outgoing_bubble);
            int padding = itemView.getContext().getResources().getDimensionPixelOffset(R.dimen._10sdp);
            ll.setPadding(0, 0, padding, 0);
        } else {
            pImg.setVisibility(View.GONE);
            backGround.setBackgroundResource(R.drawable.chat_income_bubble);
            ll.setPadding(0, 0, 0, 0);
        }

        Picasso.get()
                .load(message.getAttachment().getUrl())
                .tag(context)
                .placeholder(R.mipmap.ic_launcher)
                .into(image, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        myProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        myProgressBar.setVisibility(View.VISIBLE);
                    }
                });

        image.setOnClickListener(v -> {
            if (!ChatUtils.CHAT_CAB)
                context.startActivity(ImageViewerActivity.newInstance(context, message.getAttachment().getUrl()));
        });

        if (message.getStatusUrl() != null && !message.getStatusUrl().isEmpty()) {
            statusLay.setVisibility(View.VISIBLE);
        } else if (message.getReplyId() != null && !message.getReplyId().equalsIgnoreCase("0")) {
            for (int i = 0; i < messages.size(); i++) {
                if (messages.get(i).getId() != null &&
                        messages.get(i).getId().equalsIgnoreCase(message.getReplyId())) {
                    statusLay.setVisibility(View.VISIBLE);
                    Message message1 = messages.get(i);
                    if (message1.getAttachmentType() == AttachmentTypes.AUDIO) {
                        Picasso.get()
                                .load(R.drawable.ic_music)
                                .tag(context)
                                .placeholder(R.drawable.ic_music)
                                .into(statusImg);
                        statusText.setText("Audio");
                    } else if (message1.getAttachmentType() == AttachmentTypes.RECORDING) {
                        Picasso.get()
                                .load(R.drawable.ic_music)
                                .tag(context)
                                .placeholder(R.drawable.ic_music)
                                .into(statusImg);
                        statusText.setText("Recording");
                    } else if (message1.getAttachmentType() == AttachmentTypes.VIDEO) {
                        if (message1.getAttachment().getData() != null) {
                            Picasso.get()
                                    .load(message1.getAttachment().getData())
                                    .tag(context)
                                    .placeholder(R.mipmap.ic_launcher)
                                    .into(statusImg);
                            statusText.setText("Video");
                        } else
                            statusImg.setBackgroundResource(R.mipmap.ic_launcher);

                    } else if (message1.getAttachmentType() == AttachmentTypes.IMAGE) {
                        if (message1.getAttachment().getUrl() != null) {
                            Picasso.get()
                                    .load(message1.getAttachment().getUrl())
                                    .tag(context)
                                    .placeholder(R.mipmap.ic_launcher)
                                    .into(statusImg);
                            statusText.setText("Image");
                        } else
                            statusImg.setBackgroundResource(R.mipmap.ic_launcher);
                    } else if (message1.getAttachmentType() == AttachmentTypes.NONE_TEXT) {
                        statusText.setText(message1.getBody());
                        statusImg.setVisibility(View.GONE);
                    }
                }
            }
        } else {
            statusLay.setVisibility(View.GONE);
        }
    }
}
