package com.blive.chat.chatviewHolders;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blive.R;
import com.blive.chat.chatinterface.OnMessageItemClick;
import com.blive.chat.chatmodels.Attachment;
import com.blive.chat.chatmodels.AttachmentTypes;
import com.blive.chat.chatmodels.ChatUser;
import com.blive.chat.chatmodels.Message;
import com.blive.chat.chatutil.ChatUtils;
import com.blive.chat.chatutil.FileUtils;
import com.blive.chat.chatutil.MyFileProvider;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MessageAttachmentAudioViewHolder extends BaseMessageViewHolder {
    private TextView text;
    private TextView durationOrSize;
    private LinearLayout ll;
    private ProgressBar progressBar;
    private ImageView playPauseToggle;
    private Message message;
    private File file;
    private ImageView statusImg;
    private RelativeLayout statusLay;
    private TextView statusText;
    private ArrayList<Message> messages;
    private LinearLayout linearLayoutMessageText;
    private LinearLayout backGround;
    private ImageView pImg;

    public MessageAttachmentAudioViewHolder(View itemView, OnMessageItemClick itemClickListener, ArrayList<Message> messages) {
        super(itemView, itemClickListener, messages);
        text = itemView.findViewById(R.id.text);
        durationOrSize = itemView.findViewById(R.id.duration);
        ll = itemView.findViewById(R.id.container);
        progressBar = itemView.findViewById(R.id.progressBar);
        playPauseToggle = itemView.findViewById(R.id.playPauseToggle);
        statusImg = itemView.findViewById(R.id.statusImg);
        statusLay = itemView.findViewById(R.id.statusLay);
        statusText = itemView.findViewById(R.id.statusText);
        linearLayoutMessageText = itemView.findViewById(R.id.ll_parent_message_text);
        backGround = itemView.findViewById(R.id.backGround);
        pImg = itemView.findViewById(R.id.pImg);
        this.messages = messages;
        itemView.setOnClickListener(v -> {
            if (!ChatUtils.CHAT_CAB)
                downloadFile();
            onItemClick(true);
        });

        itemView.setOnLongClickListener(v -> {
            onItemClick(false);
            return true;
        });
    }

    @Override
    public void setData(Message message, int position, HashMap<String, ChatUser> myUsers) {
        super.setData(message, position, myUsers);
        if (isMine()) {
            pImg.setVisibility(View.VISIBLE);
            Glide.with(context).load(R.drawable.go_live).into(pImg);
            backGround.setBackgroundResource(R.drawable.outgoing_bubble);
            int padding = itemView.getContext().getResources().getDimensionPixelOffset(R.dimen._10sdp);
            ll.setPadding(0, 0, padding, 0);
            text.setTextColor(Color.WHITE);
        } else {
            text.setTextColor(Color.BLACK);
            pImg.setVisibility(View.GONE);
            backGround.setBackgroundResource(R.drawable.chat_income_bubble);
            ll.setPadding(0, 0, 0, 0);
        }

        Attachment attachment = message.getAttachment();
        this.message = message;
        boolean loading = message.getAttachment().getUrl().equals("loading");
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        playPauseToggle.setVisibility(loading ? View.GONE : View.VISIBLE);

        file = new File(Environment.getExternalStorageDirectory() + "/"
                +
                context.getString(R.string.app_name) + "/" + AttachmentTypes.getTypeName(message.getAttachmentType()) + (isMine() ? "/.sent/" : "")
                , message.getAttachment().getName());
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            try {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(context, uri);
                String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                int millis = Integer.parseInt(durationStr);
                durationOrSize.setText(TimeUnit.MILLISECONDS.toMinutes(millis) + ":" + TimeUnit.MILLISECONDS.toSeconds(millis));
                mmr.release();
            } catch (Exception e) {
            }
        } else
            durationOrSize.setText(FileUtils.getReadableFileSize(attachment.getBytesCount()));
        text.setText(message.getAttachment().getName());

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
                        //replyName.setText(message1.getAttachment().getName());
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

    public void downloadFile() {
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = MyFileProvider.getUriForFile(context,
                    "com.blive.provider",
                    file);
            intent.setDataAndType(uri, ChatUtils.getMimeType(context, uri));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } else if (!isMine() && !message.getAttachment().getUrl().equals("loading")) {
            broadcastDownloadEvent();
        } else {
            Toast.makeText(context, "File unavailable", Toast.LENGTH_SHORT).show();
        }
    }
}
