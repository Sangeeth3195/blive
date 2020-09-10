package com.blive.adapter;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blive.R;
import com.blive.activity.ActivityLiveRoom;
import com.blive.agora.rtmChat.ChatHandler;
import com.blive.agora.rtmChat.ChatManager;
import com.blive.model.User;
import com.blive.session.SessionUser;
import com.blive.utils.TransformImgCircle;
import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URLDecoder;
import java.util.ArrayList;

import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmMessage;

import static android.content.ContentValues.TAG;
public class InviteListAdapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private Context mContext;
    private ArrayList<User> users;
    private Listener listener;
    private ChatManager chatManager;
    private RtmClient rtmClient;
    private RtmMessage rtmMessage;
    private ChatHandler chatHandler;
    private JSONObject messageParams;
    public InviteListAdapter(Context mContext, ArrayList<User> users, Listener listener1) {
        this.mContext = mContext;
        this.users = users;
        this.listener = listener1;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_invites_list, parent, false);
            vh = new InviteListAdapter.DataViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_layout, parent, false);
            vh = new InviteListAdapter.ProgressViewHolder(v);
        }
        return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof InviteListAdapter.DataViewHolder) {
                User user = users.get(holder.getAdapterPosition());
                Log.d("adp_user_status", user.getStatus());

                    String base64 = user.getProfile_pic();
                    String image = URLDecoder.decode(base64, "UTF-8");
                    try {
                        if (image != null && !image.isEmpty()) {
                            Picasso.get().load(image).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.add_user_foreground).transform(new TransformImgCircle()).into(((InviteListAdapter.DataViewHolder) holder).iv);
                        } else {
                            Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(((InviteListAdapter.DataViewHolder) holder).iv);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "onBindViewHolder: " + e);
                    }

                ((InviteListAdapter.DataViewHolder) holder).tvName.setText(user.getName());
                    if (user.getStatus().equalsIgnoreCase("ACTIVE")){
                        ((InviteListAdapter.DataViewHolder) holder).live.setVisibility(View.VISIBLE);
                    }else {
                        ((InviteListAdapter.DataViewHolder) holder).live.setVisibility(View.GONE);
                    }
                    ((InviteListAdapter.DataViewHolder) holder).sendRequest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendInvite(user);
                        }
                    });
            } else {
                ((InviteListAdapter.ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            }
        } catch (Exception e) {
            Log.e(TAG, "onBindViewHolder: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /*private void sendInvite(User user) {
        Log.e(TAG, "sendInvite: " + user);
        Log.e(TAG, "pkRequestSent: " + ActivityLiveRoom.pkRequestSent);
        if(!ActivityLiveRoom.pkRequestSent)
        {
            ActivityLiveRoom.pkRequestSent = true;
            Log.e(TAG, "pkRequestSent1: " + ActivityLiveRoom.pkRequestSent);
            Log.d(TAG+"running","sendInvite method is running.");
            ((ActivityLiveRoom)mContext).findViewById(R.id.iv_pk).setVisibility(View.GONE);
            ((ActivityLiveRoom)mContext).findViewById(R.id.menu).setZ(40.0f);
            messageParams = new JSONObject();
            try
            {
                messageParams.put("request_type",mContext.getString(R.string.pk_request_type));
                messageParams.put("user_id", SessionUser.getUser().getUser_id());
                messageParams.put("user_name", SessionUser.getUser().getUsername());
                messageParams.put("id", SessionUser.getUser().getId());
                messageParams.put("user_fname", SessionUser.getUser().getName());
                messageParams.put("sender_pic", SessionUser.getUser().getProfile_pic());
                messageParams.put("sender_activation_code", SessionUser.getUser().getActivation_code());
//                messageParams.put("pk_time", SessionUser.getPkTime());
                messageParams.put("sender",new Gson().toJson(SessionUser.getUser()));
                Log.d("messageParamarams", messageParams.toString());
                listener.onRequestSent(user.getUser_id(),messageParams.toString());
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                Log.i("autolog", "e: " + e);
            }
        }else {
            Toast.makeText(mContext, "Pk Request In progress", Toast.LENGTH_SHORT).show();
        }
    }*/
    @Override
    public int getItemViewType(int position) {
        Log.d("item_view_username", users.get(position).getUsername());
        return (users.get(position) != null) ? VIEW_ITEM : VIEW_PROG;
    }
    public void refresh(ArrayList<User> mNewUsers) {
        users.clear();
        users.addAll(mNewUsers);
        notifyDataSetChanged();
    }
    public void update(ArrayList<User> mNewUsers) {
        users.addAll(mNewUsers);
        notifyDataSetChanged();
    }
    public void removeLastItem() {
        users.remove(users.size() - 1);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
       /* int countActiveFriends = 0;
        for (User user : users) {
            if (user.getStatus().equals(mContext.getString(R.string.user_active_status)))
                countActiveFriends++;
        }*/
        return users.size();
    }
    public class DataViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView tvName;
        LinearLayout ll;
        ImageView sendRequest;
        RelativeLayout live;
        private DataViewHolder(View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv_invite_pic);
            tvName = itemView.findViewById(R.id.tv_invite_username);
            ll = itemView.findViewById(R.id.inviteUserLl);
            sendRequest = itemView.findViewById(R.id.iv_invitation_send);
            live = itemView.findViewById(R.id.live);
        }
    }
    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progress_bar_bottom);
        }
    }
    public void setOnClickListener(InviteListAdapter.Listener listener) {
        this.listener = listener;
    }
    public interface Listener {
        void onRequestSent(String userName, String jsonObject);
        void onRequestSendFailure(String userName);
    }

    private void sendInvite(User user) {

        if (user.getStatus().equalsIgnoreCase(mContext.getString(R.string.user_active_status))){



            ((ActivityLiveRoom) mContext).findViewById(R.id.iv_pk).setVisibility(View.GONE);
            ((ActivityLiveRoom) mContext).findViewById(R.id.menu).setZ(40.0f);
            messageParams = new JSONObject();
            try {
                messageParams.put("request_type", mContext.getString(R.string.pk_request_type));
                messageParams.put("user_id", SessionUser.getUser().getUser_id());
                messageParams.put("user_name", SessionUser.getUser().getUsername());
                messageParams.put("id", SessionUser.getUser().getId());
                messageParams.put("user_fname", SessionUser.getUser().getName());
                messageParams.put("sender_pic", SessionUser.getUser().getProfile_pic());
                messageParams.put("user_image", SessionUser.getUser().getProfile_pic());
                messageParams.put("sender_activation_code", SessionUser.getUser().getActivation_code());
                messageParams.put("sender", new Gson().toJson(SessionUser.getUser()));
                Log.d("messageParamarams", messageParams.toString());
                Log.e(TAG, "sendInvite: " + user.getUser_id() + " + "+ messageParams.toString() );
                listener.onRequestSent(user.getUsername(),messageParams.toString());
                /*listener.onRequestSent(user.getUsername(), messageParams.toString());*/
//                                chatManager = BLiveApplication.getInstance().getChatManager();
//                                rtmClient = chatManager.getRtmClient();
//                                rtmClient.sendMessageToPeer(user.getUsername(), rtmMessage, new ResultCallback<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid)
//                                    {
//                                        Log.d("Pk_msg_sent","message sent successfully");
//
//                                    }
//                                    @Override
//                                    public void onFailure(ErrorInfo errorInfo)
//                                    {
//                                        Log.d("Pk_msg_sendFail","Failed to send message. Reason: " + errorInfo.getErrorDescription());
//                                        listener.onRequestSendFailure(user.getUsername());
//                                    }
//                                });
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("autolog", "e: " + e);
            }
        }else {
            Toast.makeText(mContext, user.getName()+" is Offline", Toast.LENGTH_SHORT).show();
        }
    }

}