package com.blive.chat.chatviewHolders;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Handler;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blive.R;
import com.blive.chat.chatinterface.OnMessageItemClick;
import com.blive.chat.chatmodels.AttachmentTypes;
import com.blive.chat.chatmodels.ChatUser;
import com.blive.chat.chatmodels.Message;
import com.blive.chat.chatutil.ChatUtils;
import com.blive.chat.chatutil.LinkTransformationMethod;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;
import java.util.HashMap;

import static com.blive.chat.chatadapter.MessageAdapter.MY;

public class MessageTextViewHolder extends BaseMessageViewHolder {
    private EmojiTextView text;
    private LinearLayout ll;
    private ImageView statusImg;
    private RelativeLayout statusLay;
    private TextView statusText;
    private ArrayList<Message> messages;
    private LinearLayout linearLayoutMessageText;
    private LinearLayout backGround;
    private ImageView pImg;
    private Message message;
    private static int _4dpInPx = -1;


    public MessageTextViewHolder(View itemView, View newMessageView,
                                 OnMessageItemClick itemClickListener, ArrayList<Message> messages) {
        super(itemView, newMessageView, itemClickListener, messages);
        text = itemView.findViewById(R.id.text);
        ll = itemView.findViewById(R.id.container);
        statusImg = itemView.findViewById(R.id.statusImg);
        statusLay = itemView.findViewById(R.id.statusLay);
        statusText = itemView.findViewById(R.id.statusText);

        linearLayoutMessageText = itemView.findViewById(R.id.ll_parent_message_text);
        backGround = itemView.findViewById(R.id.backGround);
        pImg = itemView.findViewById(R.id.pImg);

        this.messages = messages;
        text.setTransformationMethod(new LinkTransformationMethod());
        text.setMovementMethod(LinkMovementMethod.getInstance());
        if (_4dpInPx == -1) _4dpInPx = ChatUtils.dpToPx(itemView.getContext(), 4);

        itemView.setOnClickListener(v ->
                onItemClick(true));

        itemView.setOnLongClickListener(v -> {
            onItemClick(false);
            return true;
        });
    }

    @Override
    public void setData(Message message, int position, HashMap<String, ChatUser> myUsers) {
        super.setData(message, position, myUsers);
        this.message = message;

        if (isMine()) {
            pImg.setVisibility(View.VISIBLE);
            Glide.with(context).load(R.drawable.go_live).into(pImg);
            backGround.setBackgroundResource(R.drawable.outgoing_bubble);
            text.setTextColor(Color.WHITE);
        } else {
            text.setTextColor(Color.BLACK);
            pImg.setVisibility(View.GONE);
            backGround.setBackgroundResource(R.drawable.chat_income_bubble);
        }

        text.setText(message.getBody());
        if (getItemViewType() == MY) {
            animateView(position);
        }
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

    private void animateView(int position) {
        if (animate && position > lastPosition) {
            itemView.post(() -> {
                float originalX = cardView.getX();
                final float originalY = itemView.getY();
                int[] loc = new int[2];
                newMessageView.getLocationOnScreen(loc);
                cardView.setX(loc[0] / 2);
                itemView.setY(loc[1]);
                ValueAnimator radiusAnimator = new ValueAnimator();
                radiusAnimator.setFloatValues(80, _4dpInPx);
                radiusAnimator.setDuration(850);

                radiusAnimator.addUpdateListener(animation ->
                        cardView.setRadius((Float) animation.getAnimatedValue()));

                radiusAnimator.start();
                cardView.animate().x(originalX).setDuration(900).setInterpolator(new DecelerateInterpolator()).start();
                itemView.animate().y(originalY - _4dpInPx).setDuration(750).setInterpolator(new DecelerateInterpolator()).start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        itemView.animate().y(originalY + _4dpInPx)
                                .setDuration(250).setInterpolator(new DecelerateInterpolator()).start();
                    }
                }, 750);
            });
            lastPosition = position;
            animate = false;
        }
    }
}
