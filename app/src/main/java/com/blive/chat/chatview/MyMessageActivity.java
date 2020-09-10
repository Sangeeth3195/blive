package com.blive.chat.chatview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blive.BLiveApplication;
import com.blive.R;
import com.blive.chat.chatadapter.ChatAdapter;
import com.blive.chat.chatinterface.ContextualModeInteractor;
import com.blive.chat.chatinterface.OnUserGroupItemClick;
import com.blive.chat.chatmodels.Chat;
import com.blive.chat.chatmodels.ChatUser;
import com.blive.chat.chatmodels.Contact;
import com.blive.chat.chatservice.FetchMyUsersService;
import com.blive.chat.chatutil.ChatUtils;
import com.blive.chat.chatutil.MyRecyclerView;
import com.blive.session.SessionUser;
import com.blive.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class MyMessageActivity extends ChatBaseActivity implements OnUserGroupItemClick, ContextualModeInteractor {
    private MyRecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private Realm rChatDb;
    private ChatUser userMe;
    private RealmResults<Chat> resultList;
    private ArrayList<Chat> chatDataList = new ArrayList<>();
    private ChatUtils helper;
    public Utils utils;

    private RealmChangeListener<RealmResults<Chat>> chatListChangeListener =
            new RealmChangeListener<RealmResults<Chat>>() {
                @Override
                public void onChange(RealmResults<Chat> element) {
                    if (element != null && element.isValid() && element.size() > 0) {
                        chatDataList.clear();
                        chatDataList.addAll(rChatDb.copyFromRealm(element));
                        //badgeCount();
                        setUserNamesAsInPhone();
                    }
                }
            };


    @Override
    void myUsersResult(ArrayList<ChatUser> myUsers) {

    }

    @Override
    void myContactsResult(ArrayList<Contact> myContacts) {

    }

    @Override
    void userAdded(ChatUser valueUser) {

    }

    @Override
    void userUpdated(ChatUser valueUser) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utils = new Utils(MyMessageActivity.this);
        setContentView(R.layout.fragment_main_recycler);
        uiInit();
    }

    private void uiInit() {
        helper = new ChatUtils(MyMessageActivity.this);
        Realm.init(MyMessageActivity.this);
        rChatDb = ChatUtils.getRealmInstance();
        recyclerView = findViewById(R.id.recycler_view);
        mySwipeRefreshLayout = findViewById(R.id.swipe_refresh_lay);
        mySwipeRefreshLayout.setRefreshing(false);
        recyclerView.setEmptyView(findViewById(R.id.emptyView));
        recyclerView.setEmptyImageView(findViewById(R.id.emptyImage));
        recyclerView.setEmptyTextView(findViewById(R.id.emptyText));

        recyclerView.setLayoutManager(new LinearLayoutManager(MyMessageActivity.this));

        mySwipeRefreshLayout.setOnRefreshListener(() -> {
            try {
                RealmQuery<Chat> query = rChatDb.where(Chat.class).equalTo("myId", userMe.getId());
                resultList = query.isNotNull("user").sort("timeUpdated", Sort.DESCENDING).findAll();
                chatDataList.clear();
                chatDataList.addAll(rChatDb.copyFromRealm(resultList));
                chatAdapter = new ChatAdapter(MyMessageActivity.this, chatDataList, userMe.getId(), "chat");
                recyclerView.setAdapter(chatAdapter);
                resultList.addChangeListener(chatListChangeListener);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mySwipeRefreshLayout.setRefreshing(false);
            setUserNamesAsInPhone();


        });

        findViewById(R.id.addConversation).setOnClickListener(v ->
                startActivity(new Intent(MyMessageActivity.this, MyContactActivity.class)));

        findViewById(R.id.back).setOnClickListener(v ->
                finish());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (resultList != null)
            resultList.removeChangeListener(chatListChangeListener);
    }

    public void setUserNamesAsInPhone() {
        if (chatDataList != null) {
            for (Chat chat : chatDataList) {
                ChatUser user = chat.getUser();
                if (user != null) {
                    if (helper.getCacheMyUsers() != null && helper.getCacheMyUsers().containsKey(user.getId())) {
                        user.setNameToDisplay(helper.getCacheMyUsers().get(user.getId()).getNameToDisplay());
                    }
                }
            }
        }
        if (chatAdapter != null)
            chatAdapter.notifyDataSetChanged();
    }

    public void deleteSelectedChats() {
        for (Chat chat : chatDataList) {
            if (chat.isSelected()) {
                final String[] chatChild = {userMe.getId() + "-" + chat.getUserId()};
                BLiveApplication.getChatRef().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.hasChild(chatChild[0])) {
                            chatChild[0] = chat.getUserId() + "-" + userMe.getId();
                        }
                        rChatDb.beginTransaction();
                        BLiveApplication.getChatRef().child(chatChild[0]).removeValue();
                        Chat chatToDelete = rChatDb.where(Chat.class).equalTo("myId",
                                userMe.getId()).equalTo("userId", chat.getUserId()).findFirst();
                        if (chatToDelete != null) {
                            RealmObject.deleteFromRealm(chatToDelete);
                        }
                        rChatDb.commitTransaction();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

        try {
            RealmQuery<Chat> query = rChatDb.where(Chat.class).equalTo("myId", userMe.getId());
            resultList = query.isNotNull("user").sort("timeUpdated", Sort.DESCENDING).findAll();
            chatDataList.clear();
            chatDataList.addAll(rChatDb.copyFromRealm(resultList));
            chatAdapter = new ChatAdapter(MyMessageActivity.this, chatDataList, userMe.getId(), "chat");
            recyclerView.setAdapter(chatAdapter);
            resultList.addChangeListener(chatListChangeListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setUserNamesAsInPhone();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(myUsersReceiver, new IntentFilter(ChatUtils.BROADCAST_MY_USERS));

        if (!FetchMyUsersService.STARTED) {
            utils.showProgress();
            FetchMyUsersService.startMyUsersService(MyMessageActivity.this, SessionUser.getUser().getUser_id(), "");
        }


    }

    private BroadcastReceiver myUsersReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            helper.setCacheMyUsers(intent.getParcelableArrayListExtra("data"));
            try {
                userMe = helper.getLoggedInUser();
                RealmQuery<Chat> query = rChatDb.where(Chat.class).equalTo("myId", userMe.getId());
                resultList = query.isNotNull("user").sort("timeUpdated", Sort.DESCENDING).findAll();

                chatDataList.clear();
                chatDataList.addAll(rChatDb.copyFromRealm(resultList));
                chatAdapter = new ChatAdapter(MyMessageActivity.this, chatDataList, userMe.getId(), "chat");
                recyclerView.setAdapter(chatAdapter);

                resultList.addChangeListener(chatListChangeListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
            utils.hideProgress();
            setUserNamesAsInPhone();
        }
    };

    @Override
    public void OnUserClick(final ChatUser user, int position, View userImage) {
        Intent intent = FireBaseChatActivity.newIntent(this, new ArrayList<>(), user);
        startActivity(intent);
    }


    @Override
    public void enableContextualMode() {

    }

    @Override
    public boolean isContextualMode() {
        return false;
    }

    @Override
    public void updateSelectedCount(int count) {

    }
}
