package com.blive.chat.chatadapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.blive.R;
import com.blive.chat.chatinterface.ContextualModeInteractor;
import com.blive.chat.chatinterface.OnUserGroupItemClick;
import com.blive.chat.chatmodels.AttachmentTypes;
import com.blive.chat.chatmodels.Chat;
import com.blive.chat.chatmodels.ChatUser;
import com.blive.chat.chatmodels.Message;
import com.blive.chat.chatutil.ChatUtils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

import io.realm.RealmList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Chat> dataList;
    private OnUserGroupItemClick itemClickListener;
    private ContextualModeInteractor contextualModeInteractor;
    private int selectedCount = 0;
    private String userId, from;

    public ChatAdapter(Context context, ArrayList<Chat> dataList, String userId, String from) {
        this.context = context;
        this.dataList = dataList;
        this.userId = userId;
        this.from = from;
        if (context instanceof OnUserGroupItemClick) {
            this.itemClickListener = (OnUserGroupItemClick) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnUserGroupItemClick");
        }

        if (context instanceof ContextualModeInteractor) {
            this.contextualModeInteractor = (ContextualModeInteractor) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ContextualModeInteractor");
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_item_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setData(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView status, name, lastMessage, time, msgCount, msgCountGroup;
        private ImageView image, myUserImageOnline, img;
        private RelativeLayout user_details_container;

        MyViewHolder(View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.emotion);
            name = itemView.findViewById(R.id.user_name);
            time = itemView.findViewById(R.id.time);
            lastMessage = itemView.findViewById(R.id.message);
            image = itemView.findViewById(R.id.user_image);
            msgCount = itemView.findViewById(R.id.msgCount);
            msgCountGroup = itemView.findViewById(R.id.msgCountGroup);
            img = itemView.findViewById(R.id.img);
            user_details_container = itemView.findViewById(R.id.user_details_container);
            myUserImageOnline = itemView.findViewById(R.id.user_image_online);
            user_details_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (contextualModeInteractor.isContextualMode()) {
                        toggleSelection(dataList.get(getAdapterPosition()), getAdapterPosition());
                    } else {
                        int pos = getAdapterPosition();
                        if (pos != -1) {
                            Chat chat = dataList.get(pos);
                            if (chat.getUser() != null)
                                itemClickListener.OnUserClick(chat.getUser(), pos, image);
                        }
                    }
                }
            });

            /*user_details_container.setOnLongClickListener(view -> {
                contextualModeInteractor.enableContextualMode();
                toggleSelection(dataList.get(getAdapterPosition()), getAdapterPosition());
                return true;
            });*/
        }

        private void showDialog(final String image, String profileName) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialogView = li.inflate(R.layout.dialog_image, null);
            ImageView profileImg = dialogView.findViewById(R.id.profileImg);
            TextView name = dialogView.findViewById(R.id.name);
            name.setText(profileName);
            if (!image.isEmpty()) {
                Picasso.get()
                        .load(image)
                        .tag(this)
                        .placeholder(R.drawable.ic_avatar)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(profileImg);

               /* BitmapDrawable drawable = (BitmapDrawable) profileImg.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                float imageWidthInPX = (float)profileImg.getWidth();
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Math.round(imageWidthInPX),
                        Math.round(imageWidthInPX * (float)bitmap.getHeight() / (float)bitmap.getWidth()));
                profileImg.setLayoutParams(layoutParams);*/
            } else {
                Picasso.get()
                        .load(R.drawable.ic_person_gray)
                        .tag(this)
                        .error(R.drawable.ic_person_gray)
                        .placeholder(R.drawable.ic_person_gray)
                        .into(profileImg);
            }
            builder.setView(dialogView).create().show();
        }

        private void setData(final Chat chat) {
            final ChatUser chatUser = chat.getUser();
            RealmList<Message> message = chat.getMessages();
//            Glide.with(context).load(chatUser != null ? chatUser.getImage() : chatGroup.getImage()).apply(new RequestOptions().placeholder(R.drawable.ic_placeholder)).into(image);

            if (chatUser != null && chatUser.getImage() != null && !chatUser.getImage().equalsIgnoreCase("")) {
                Picasso.get()
                        .load(chatUser.getImage())
                        .resizeDimen(R.dimen._40sdp, R.dimen._40sdp)
                        .centerInside()
                        .into(image);

            } else {
                Picasso.get()
                        .load(R.drawable.ic_avatar)
                        .resizeDimen(R.dimen._40sdp, R.dimen._40sdp)
                        .centerInside()
                        .into(image);
            }

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String profileName = name.getText().toString();
                    if (chatUser != null && chatUser.getImage() != null/* && !chatUser.getImage().equalsIgnoreCase("")*/) {
                        showDialog(chatUser.getImage(), profileName);
                    }
                }
            });

            name.setText(chatUser != null ? chatUser.getNameToDisplay() : "");
            if (from.equalsIgnoreCase("group") && !chat.isRead()) {
                msgCountGroup.setVisibility(View.VISIBLE);
            } else {
                msgCountGroup.setVisibility(View.GONE);
            }
            //name.setCompoundDrawablesWithIntrinsicBounds(0, 0, !chat.isRead() ? R.drawable.ring_blue : 0, 0);
            status.setText("");
            //time.setText(Helper.getTimeAgo(chat.getTimeUpdated(), context));
            time.setText(ChatUtils.getChatFormattedDate(chat.getTimeUpdated()));
            if (chatUser != null) {
                lastMessage.setText(chat.getLastMessage());
            }

            lastMessage.setTextColor(ContextCompat.getColor(context, !chat.isRead() ? R.color.textColorPrimary : R.color.textColorSecondary));

            user_details_container.setBackgroundColor(ContextCompat.getColor(context, (chat.isSelected() ? R.color.bg_gray : R.color.colorIcon)));

            try {
                if (chatUser != null && chatUser.isOnline()) {
                    myUserImageOnline.setVisibility(View.VISIBLE);
                    lastMessage.setCompoundDrawablesWithIntrinsicBounds(0, 0, chatUser.isOnline() ? R.drawable.ring_green : 0, 0);
                } else {
                    myUserImageOnline.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (message != null && message.size() > 0) {
                    if (message != null && message.get(message.size() - 1).getAttachmentType() == AttachmentTypes.AUDIO) {
                        img.setVisibility(View.VISIBLE);
                        img.setBackgroundResource(R.drawable.ic_audiotrack_gray);
                        lastMessage.setText(context.getString(R.string.audio));
                    } else if (message != null && message.get(message.size() - 1).getAttachmentType() == AttachmentTypes.RECORDING) {
                        img.setVisibility(View.VISIBLE);
                        img.setBackgroundResource(R.drawable.ic_audiotrack_gray);
                        lastMessage.setText(context.getString(R.string.recording));
                    } else if (message != null && message.get(message.size() - 1).getAttachmentType() == AttachmentTypes.VIDEO) {
                        img.setVisibility(View.VISIBLE);
                        img.setBackgroundResource(R.drawable.ic_videocam_gray);
                        lastMessage.setText(context.getString(R.string.video));
                    } else if (message != null && message.get(message.size() - 1).getAttachmentType() == AttachmentTypes.IMAGE) {
                        img.setVisibility(View.VISIBLE);
                        img.setBackgroundResource(R.drawable.ic_wallpaper_gray);
                        lastMessage.setText(context.getString(R.string.image));
                    } else if (message != null && message.get(message.size() - 1).getAttachmentType() == AttachmentTypes.NONE_TEXT) {
                        img.setVisibility(View.GONE);
                    }

                    if (chatUser != null) {
                        msgCountGroup.setVisibility(View.GONE);
                        int count = 0;
                        for (int i = 0; i < message.size(); i++) {
                            if (!message.get(i).isReadMsg() && !message.get(i).getSenderId().equalsIgnoreCase(userId))
                                count++;
                        }

                        if (count > 99) {
                            msgCount.setVisibility(View.VISIBLE);
                            msgCount.setText("+99");
                        } else if (count > 0) {
                            msgCount.setVisibility(View.VISIBLE);
                            msgCount.setText("" + count);
                        } else {
                            msgCount.setVisibility(View.GONE);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void toggleSelection(Chat chat, int position) {
        chat.setSelected(!chat.isSelected());
        notifyItemChanged(position);

        if (chat.isSelected())
            selectedCount++;
        else
            selectedCount--;

        contextualModeInteractor.updateSelectedCount(selectedCount);
    }

    public void disableContextualMode() {
        selectedCount = 0;
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).isSelected()) {
                dataList.get(i).setSelected(false);
                notifyItemChanged(i);
            }
        }
    }
}
