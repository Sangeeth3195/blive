package com.blive.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.blive.model.User;
import com.blive.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.net.URLDecoder;
import java.util.ArrayList;


public class AdapterFriendsShare extends RecyclerView.Adapter<AdapterFriendsShare.ViewHolder> {

    private Context mContext;
    private ArrayList<User> users;
    private ArrayList<User> selectedUser;
    private Listener listener;
    private  boolean selectedAll;
    int selectedAllStatus = 0;

    public AdapterFriendsShare(Context mContext, ArrayList<User> users) {
        this.mContext = mContext;
        this.users = users;
        selectedUser = new ArrayList<>();
        selectedAllStatus=0;
    }

    public AdapterFriendsShare(Context mContext, ArrayList<User> users,boolean selectedAll) {
        this.mContext = mContext;
        this.users = users;
        selectedUser = new ArrayList<>();
        this.selectedAll = selectedAll;
        selectedAllStatus = 1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sharing_friends, parent, false);
            return new ViewHolder(layout);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {
            final User user = users.get(position);

            String base64 = user.getProfile_pic();
            String image = URLDecoder.decode(base64,"UTF-8");

            if (image != null && !image.isEmpty()) {
                Picasso.get().load(image).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(holder.iv);
            }else {
                Picasso.get().load(R.drawable.user).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(holder.iv);
            }

            holder.tvName.setText(user.getName());
            if(selectedUser.size() >0){
                if(selectedUser.get(position).getUser_id().equals(user.getUser_id())){
                    holder.cbSelectFriends.setChecked(true);
                }
            }

            if(selectedAllStatus == 1){
                if(selectedAll){
                    holder.cbSelectFriends.setChecked(selectedAll);
                }else{
                    holder.cbSelectFriends.setChecked(selectedAll);
                }
            }

            holder.tvFriendsGoldCount.setText(user.getOver_all_gold());
            String level = "Level : "+user.getLevel();
            holder.tvLevel.setText(level);
           holder.cbSelectFriends.setOnCheckedChangeListener((compoundButton, checked) -> {
               if(checked){
                   if(selectedUser.size() >0){
                       for(int i=0;i<selectedUser.size();i++){
                           if(selectedUser.get(i).getUser_id().equals(user.getUser_id())){
                               selectedUser.remove(i);
                               selectedUser.add(user);
                           }
                       }

                   }else{
                       selectedUser.add(user);
                   }

               }else{
                   for(int i=0;i<selectedUser.size();i++){
                       if(selectedUser.get(i).getUser_id().equals(user.getUser_id())){
                           selectedUser.remove(i);
                       }
                   }
               }

           });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv;
        TextView tvPosition,tvLevel,tvFriendsGoldCount;
        TextView tvName;
        CheckBox cbSelectFriends;

        private ViewHolder(View itemView) {
            super(itemView);

            iv = itemView.findViewById(R.id.iv);
            tvName = itemView.findViewById(R.id.tv_name);
            tvLevel = itemView.findViewById(R.id.tv_level);
            tvFriendsGoldCount = itemView.findViewById(R.id.tv_friend_gold_count);
            tvPosition = itemView.findViewById(R.id.tv_position);
            cbSelectFriends = itemView.findViewById(R.id.cb_select_friends);
        }
    }

    public void setOnClickListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void OnClicked(User user);
    }

    public ArrayList<User> selectedFriends(){

        if(selectedAllStatus == 1){
            if(selectedAll){
                selectedUser.clear();
                selectedUser.addAll(users);
            }
        }
        return selectedUser;
    }
}
