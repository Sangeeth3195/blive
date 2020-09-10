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
import com.blive.chat.chatinterface.OnUserGroupItemClick;
import com.blive.chat.chatmodels.ChatUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<ChatUser> userList;
    private OnUserGroupItemClick itemClickListener;

    public ContactsAdapter(Context context, ArrayList<ChatUser> userList) {
        this.context = context;
        this.userList = userList;
        if (context instanceof OnUserGroupItemClick) {
            this.itemClickListener = (OnUserGroupItemClick) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnUserGroupItemClick");
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_contact_number, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        ChatUser user = userList.get(position);

        holder.userName.setText(user.getNameToDisplay());
        holder.userId.setText(user.getId());

        Picasso.get()
                .load(user.getImage())
                .error(R.drawable.user)
                .placeholder(R.drawable.user)
                .into(holder.image);

        holder.itemLayout.setOnClickListener(v ->
                itemClickListener.OnUserClick(user, position, holder.image));

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userId;
        ImageView image;
        RelativeLayout itemLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userId = itemView.findViewById(R.id.userId);
            image = itemView.findViewById(R.id.image);
            itemLayout = itemView.findViewById(R.id.itemView);
        }
    }
}
