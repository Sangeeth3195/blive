package com.blive.chat.chatview;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blive.BLiveApplication;
import com.blive.R;
import com.blive.activity.ActivityViewProfile;
import com.blive.chat.chatadapter.MessageAdapter;
import com.blive.chat.chatinterface.OnMessageItemClick;
import com.blive.chat.chatmodels.Attachment;
import com.blive.chat.chatmodels.AttachmentTypes;
import com.blive.chat.chatmodels.Chat;
import com.blive.chat.chatmodels.ChatUser;
import com.blive.chat.chatmodels.Contact;
import com.blive.chat.chatmodels.Message;
import com.blive.chat.chatutil.ChatUtils;
import com.blive.chat.chatutil.DownloadFileEvent;
import com.blive.chat.chatutil.DownloadUtil;
import com.blive.chat.chatutil.FileUtils;
import com.blive.chat.chatutil.KeyboardUtil;
import com.blive.chat.chatviewHolders.BaseMessageViewHolder;
import com.blive.chat.chatviewHolders.MessageAttachmentRecordingViewHolder;
import com.blive.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;
import com.kbeanie.multipicker.api.AudioPicker;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.VideoPicker;
import com.kbeanie.multipicker.api.callbacks.AudioPickerCallback;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.callbacks.VideoPickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenAudio;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.kbeanie.multipicker.api.entity.ChosenVideo;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class FireBaseChatActivity extends ChatBaseActivity implements OnMessageItemClick,
        MessageAttachmentRecordingViewHolder.RecordingViewInteractor, View.OnClickListener, ImagePickerCallback,
        AudioPickerCallback, VideoPickerCallback {
    private static final int REQUEST_CODE_UPDATE_USER = 753;
    private static final int REQUEST_PERMISSION_RECORD = 159;
    private static String EXTRA_DATA_USER = "extradatauser";
    private static String EXTRA_DATA_LIST = "extradatalist";
    private static String DELETE_TAG = "deletetag";
    private MessageAdapter messageAdapter;
    private ArrayList<Message> dataList = new ArrayList<>();
    private RealmResults<Chat> queryResult;
    private String chatChild, userOrGroupId;
    private int countSelected = 0;
    private Handler recordWaitHandler, recordTimerHandler;
    private Runnable recordRunnable, recordTimerRunnable;
    private MediaRecorder mRecorder = null;
    private String recordFilePath;
    private float displayWidth;
    private ArrayList<Integer> adapterPositions = new ArrayList<>();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String currentlyPlaying = "";
    private Toolbar toolbar;
    private RelativeLayout toolbarContent;
    private TextView selectedCount, status, userName;
    private RecyclerView recyclerView;
    private EmojiEditText newMessage;
    private ImageView usersImage, addAttachment, sendMessage, attachment_emoji;
    private LinearLayout rootView, sendContainer, myAttachmentLLY;
    private EmojiPopup emojIcon;
    private String pickerPath;
    private ImagePicker imagePicker;
    private CameraImagePicker cameraPicker;
    private AudioPicker audioPicker;
    private VideoPicker videoPicker;
    private RelativeLayout replyLay;
    private TextView replyName;
    private ImageView replyImg;
    private HashMap<String, ChatUser> myUsersNameInPhoneMap;
    private String replyId = "0";


    String senderIdDelete = "";
    String recipientIdDelete = "";
    String bodyDelete = "";
    long dateDelete;
    private Utils utils;

    private boolean delete = false;

    private BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null)
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                    if (adapterPositions.size() > 0 && messageAdapter != null)
                        for (int pos : adapterPositions)
                            if (pos != -1)
                                messageAdapter.notifyItemChanged(pos);
                    adapterPositions.clear();
                }
        }
    };

    private BroadcastReceiver downloadEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadFileEvent downloadFileEvent = intent.getParcelableExtra("data");
            if (downloadFileEvent != null) {
                downloadFile(downloadFileEvent);
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
        if (user != null && user.getId().equals(valueUser.getId())) {
            valueUser.setNameToDisplay(user.getNameToDisplay());
            user = valueUser;
            status.setText("");
            status.setSelected(true);
            showTyping(user.isTyping());
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_chats);
        this.helper = new ChatUtils(FireBaseChatActivity.this);
        utils = new Utils(FireBaseChatActivity.this);
        this.myUsersNameInPhoneMap = helper.getCacheMyUsers();
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_DATA_USER)) {
            user = intent.getParcelableExtra(EXTRA_DATA_USER);
            ChatUtils.CURRENT_CHAT_ID = user.getId();
        } else {
            finish();
        }

        initUi();

        String nameText = null, statusText = null, imageUrl = null;
        if (user != null) {
            nameText = user.getNameToDisplay();
            statusText = "";
            imageUrl = user.getImage();
        }
        userName.setText(nameText);
        status.setText(statusText);
        userName.setSelected(true);
        status.setSelected(true);
        if (imageUrl != null && !imageUrl.isEmpty())
            Picasso.get()
                    .load(imageUrl)
                    .tag(this)
                    .error(R.mipmap.ic_launcher)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(usersImage);
        else
            usersImage.setBackgroundResource(R.mipmap.ic_launcher);

        //     chatChild = ChatUtils.getChatChild(user.getId(), userMe.getId());
        userOrGroupId = user.getId();

        messageAdapter = new MessageAdapter(this, dataList, userMe.getId(), newMessage);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);
        recyclerView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                recyclerView.postDelayed(() ->
                        recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1), 100);
            }
        });

        emojIcon = EmojiPopup.Builder.fromRootView(rootView).setOnEmojiPopupShownListener(() -> {
            if (myAttachmentLLY.getVisibility() == View.VISIBLE) {
                myAttachmentLLY.setVisibility(View.GONE);
                addAttachment.animate().setDuration(400).rotationBy(-45).start();
            }
        }).build(newMessage);

        displayWidth = ChatUtils.getDisplayWidth(this);

        mediaPlayer.setOnCompletionListener(mediaPlayer ->
                notifyRecordingPlaybackCompletion());

        getChatChild();
        registerUserUpdates();
        initSwipe();

        findViewById(R.id.videoRecord).setOnClickListener(v ->
                startActivityForResult(new Intent(FireBaseChatActivity.this, CameraActivity.class), 123));

        findViewById(R.id.closeReply).setOnClickListener(v -> {
            if (replyLay.getVisibility() == View.VISIBLE)
                replyLay.setVisibility(View.GONE);
            replyId = "0";
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initUi() {
        toolbar = findViewById(R.id.chatToolbar);
        toolbarContent = findViewById(R.id.chatToolbarContent);
        selectedCount = findViewById(R.id.selectedCount);
        usersImage = findViewById(R.id.users_image);
        status = findViewById(R.id.emotion);
        userName = findViewById(R.id.user_name);
        recyclerView = findViewById(R.id.recycler_view);
        newMessage = findViewById(R.id.new_message);
        addAttachment = findViewById(R.id.add_attachment);
        sendMessage = findViewById(R.id.send);
        sendContainer = findViewById(R.id.sendContainer);
        myAttachmentLLY = findViewById(R.id.layout_chat_attachment_LLY);
        rootView = findViewById(R.id.rootView);
        attachment_emoji = findViewById(R.id.attachment_emoji);
        replyLay = findViewById(R.id.replyLay);
        replyName = findViewById(R.id.replyName);
        replyImg = findViewById(R.id.replyImg);

        setSupportActionBar(toolbar);
        addAttachment.setOnClickListener(this);
        toolbarContent.setOnClickListener(this);
        attachment_emoji.setOnClickListener(this);
        sendMessage.setOnClickListener(this);
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.attachment_video).setOnClickListener(this);
        findViewById(R.id.attachment_gallery).setOnClickListener(this);
        findViewById(R.id.attachment_audio).setOnClickListener(this);
        newMessage.setOnTouchListener((v, event) -> {
            if (myAttachmentLLY.getVisibility() == View.VISIBLE) {
                myAttachmentLLY.setVisibility(View.GONE);
                addAttachment.animate().setDuration(400).rotationBy(-45).start();
            }
            return false;
        });
        sendMessage.setOnTouchListener(voiceMessageListener);


    }

    private View.OnTouchListener voiceMessageListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.i("TAG", "touched down");
                    if (newMessage.getText().toString().trim().isEmpty()) {
                        if (recordWaitHandler == null)
                            recordWaitHandler = new Handler();
                        recordRunnable = () ->
                                recordingStart();

                        recordWaitHandler.postDelayed(recordRunnable, 600);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.i("TAG", "moving: (" + displayWidth + ", " + x + ")");
                    if (mRecorder != null && newMessage.getText().toString().trim().isEmpty()) {
                        if (Math.abs(event.getX()) / displayWidth > 0.35f) {
                            recordingStop(false);
                            Toast.makeText(FireBaseChatActivity.this, "Recording cancelled",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i("TAG", "touched up");
                    if (recordWaitHandler != null && newMessage.getText().toString().trim().isEmpty())
                        recordWaitHandler.removeCallbacks(recordRunnable);
                    if (mRecorder != null && newMessage.getText().toString().trim().isEmpty()) {
                        recordingStop(true);
                    }
                    break;
            }
            return false;
        }
    };

    private void recordingStop(boolean send) {
        try {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        } catch (Exception ex) {
            Log.e("mRecorder", ex.getMessage());
            mRecorder = null;
        }
        KeyboardUtil.getInstance(this).closeKeyboard();
        recordTimerStop();
        if (send) {
            newFileUploadTask(recordFilePath, AttachmentTypes.RECORDING, null);
        } else {
            new File(recordFilePath).delete();
        }
    }

    private void recordingStart() {
        if (recordPermissionsAvailable()) {
            File recordFile = new File(Environment.getExternalStorageDirectory(), "/" +
                    getString(R.string.app_name) + "/" + AttachmentTypes.getTypeName(AttachmentTypes.RECORDING) +
                    "/.sent/");
            boolean dirExists = recordFile.exists();
            if (!dirExists)
                dirExists = recordFile.mkdirs();
            if (dirExists) {
                try {
                    recordFile = new File(Environment.getExternalStorageDirectory() + "/" +
                            getString(R.string.app_name) + "/" + AttachmentTypes.getTypeName(AttachmentTypes.RECORDING)
                            + "/.sent/", System.currentTimeMillis() + ".mp3");
                    if (!recordFile.exists())
                        recordFile.createNewFile();
                    recordFilePath = recordFile.getAbsolutePath();
                    mRecorder = new MediaRecorder();
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    mRecorder.setOutputFile(recordFilePath);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    mRecorder.prepare();
                    mRecorder.start();
                    recordTimerStart(System.currentTimeMillis());
                } catch (IOException | IllegalStateException e) {
                    e.printStackTrace();
                    mRecorder = null;
                }
            }
        } else {
            ActivityCompat.requestPermissions(this, permissionsRecord, REQUEST_PERMISSION_RECORD);
        }
    }

    private void recordTimerStart(final long currentTimeMillis) {
        Toast.makeText(this, "Recording...", Toast.LENGTH_SHORT).show();
        recordTimerRunnable = new Runnable() {
            public void run() {
                Long elapsedTime = System.currentTimeMillis() - currentTimeMillis;
                newMessage.setHint(ChatUtils.timeFormater(elapsedTime) + " (Slide left to cancel)");
                recordTimerHandler.postDelayed(this, 1000);
            }
        };
        if (recordTimerHandler == null)
            recordTimerHandler = new Handler();
        recordTimerHandler.post(recordTimerRunnable);
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) v.vibrate(100);
    }

    private void recordTimerStop() {
        recordTimerHandler.removeCallbacks(recordTimerRunnable);
        newMessage.setHint("Type your message");
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) v.vibrate(100);
    }

    private boolean recordPermissionsAvailable() {
        boolean available = true;
        for (String permission : permissionsRecord) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                available = false;
                break;
            }
        }
        return available;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(downloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(downloadEventReceiver, new
                IntentFilter(ChatUtils.BROADCAST_DOWNLOAD_EVENT));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(downloadCompleteReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(downloadEventReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ChatUtils.CHAT_CAB)
            undoSelectionPrepared();
        if (queryResult != null && realmChangeListener != null) {
            queryResult.removeChangeListener(realmChangeListener);
        }

        ChatUtils.CURRENT_CHAT_ID = null;
        markAllReadForThisUser();

        if (delete) {
            String userOrGroupId = userMe.getId().equals(senderIdDelete)
                    ? recipientIdDelete : senderIdDelete;
            final Chat chat = ChatUtils.getChat(rChatDb, userMe.getId(), userOrGroupId).findFirst();
            if (chat != null) {
                rChatDb.executeTransaction(realm -> {
                    RealmList<Message> realmList1 = new RealmList<>();
                    for (int i = 0; i < chat.getMessages().size(); i++) {
                        if (chat.getMessages().get(i) != null && !chat.getMessages().get(i).getDelete()
                                .equalsIgnoreCase(userMe.getId())) {
                            realmList1.add(chat.getMessages().get(i));
                        }
                    }
                    if (realmList1.size() == 0)
                        RealmObject.deleteFromRealm(chat);
                    else {
                        chat.setLastMessage(realmList1.get(realmList1.size() - 1).getBody());
                        chat.setTimeUpdated(realmList1.get(realmList1.size() - 1).getDate());
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (ChatUtils.CHAT_CAB)
            undoSelectionPrepared();
        else {
            KeyboardUtil.getInstance(this).closeKeyboard();
            if (Build.VERSION.SDK_INT > 21) {
                finishAfterTransition();
            } else {
                finish();
            }
        }
    }

    private void markAllReadForThisUser() {
        Chat thisChat = ChatUtils.getChat(rChatDb, userMe.getId(), userOrGroupId).findFirst();
        if (thisChat != null) {
            rChatDb.beginTransaction();
            thisChat.setRead(true);
            rChatDb.commitTransaction();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_copy:
                StringBuilder stringBuilder = new StringBuilder();
                for (Message message : dataList) {
                    if (message.isSelected() && !TextUtils.isEmpty(message.getBody())) {
                        stringBuilder.append(message.getBody());
                    }
                }

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("simple text", stringBuilder.toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Messages copied", Toast.LENGTH_SHORT).show();
                undoSelectionPrepared();
                break;
            case R.id.action_delete:
                FragmentManager manager = getSupportFragmentManager();
                Fragment frag = manager.findFragmentByTag(DELETE_TAG);
                if (frag != null) {
                    manager.beginTransaction().remove(frag).commit();
                }

                ConfirmationDialogFragment confirmationDialogFragment =
                        ConfirmationDialogFragment.newInstance("Delete messages",
                                "Continue deleting selected messages?",
                                view -> {
                                    countSelected = 0;
                                    for (final Message msg : new ArrayList<>(dataList)) {
                                        if (msg.isSelected()) {
                                            try {
                                                if (msg.getDelete() != null && msg.getDelete().isEmpty()) {
                                                    BLiveApplication.getChatRef().child(chatChild).child(msg.getId()).child("delete").setValue(userMe.getId());

                                                    senderIdDelete = msg.getSenderId();
                                                    recipientIdDelete = msg.getRecipientId();
                                                    bodyDelete = msg.getBody();
                                                    dateDelete = msg.getDate();
                                                    delete = true;
                                                    ChatUtils.deleteMessageFromRealm(rChatDb, msg.getId());
                                                } else if (msg.getDelete() != null && !msg.getDelete().isEmpty()) {
                                                    BLiveApplication.getChatRef().child(chatChild).child(msg.getId()).removeValue();
                                                    ChatUtils.deleteMessageFromRealm(rChatDb, msg.getId());
                                                }
                                            } catch (DatabaseException de) {
                                                if (msg.getId() != null) {
                                                    ChatUtils.deleteMessageFromRealm(rChatDb, msg.getId());
                                                }
                                                Log.e("DatabaseException", de.getMessage());
                                            }
                                        }
                                    }

                                    toolbar.getMenu().clear();
                                    selectedCount.setVisibility(View.GONE);
                                    toolbarContent.setVisibility(View.VISIBLE);
                                    ChatUtils.CHAT_CAB = false;
                                },
                                view ->
                                        undoSelectionPrepared());

                confirmationDialogFragment.show(manager, DELETE_TAG);
                break;
        }
        return true;
    }

    private void registerUserUpdates() {
        newMessage.addTextChangedListener(new TextWatcher() {
            CountDownTimer timer = null;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendMessage.setImageDrawable(ContextCompat.getDrawable(FireBaseChatActivity.this, s.length()
                        == 0 ? R.drawable.ic_keyboard_voice : R.drawable.ic_send));
                if (user != null) {
                    if (timer != null) {
                        timer.cancel();
                        BLiveApplication.getUserRef().child(userMe.getId()).child("typing").setValue(true);
                    }
                    timer = new CountDownTimer(1500, 1000) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            BLiveApplication.getUserRef().child(userMe.getId()).child("typing").setValue(false);
                        }
                    }.start();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void showTyping(boolean typing) {
        if (dataList != null && dataList.size() > 0 && RealmObject.isValid(dataList.get(dataList.size() - 1))) {
            boolean lastIsTyping = dataList.get(dataList.size() - 1).getAttachmentType() == AttachmentTypes.NONE_TYPING;
            if (typing && !lastIsTyping) {
                dataList.add(new Message(AttachmentTypes.NONE_TYPING));
                messageAdapter.notifyItemInserted(dataList.size() - 1);
                recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
            } else if (lastIsTyping && dataList.size() > 0) {
                dataList.remove(dataList.size() - 1);
                messageAdapter.notifyItemRemoved(dataList.size());
            }
        }
    }

    private RealmChangeListener<RealmResults<Chat>> realmChangeListener = new RealmChangeListener<RealmResults<Chat>>() {
        @Override
        public void onChange(RealmResults<Chat> element) {
            if (element != null && element.isValid() && element.size() > 0) {
                RealmList<Message> updatedList = element.get(0).getMessages();
                if (updatedList != null && updatedList.size() > 0) {
                    if (updatedList.size() < dataList.size()) {
                        dataList.clear();
                        for (int i = 0; i < element.get(0).getMessages().size(); i++) {
                            if (element.get(0).getMessages().get(i) != null && !element.get(0).getMessages().get(i).getDelete()
                                    .equalsIgnoreCase(userMe.getId())) {
                                dataList.add(element.get(0).getMessages().get(i));
                            }
                        }
                        messageAdapter.notifyDataSetChanged();
                    } else {
                        try {
                            showTyping(false);
                            int lastPos = dataList.size() - 1;
                            Message newMessage = updatedList.get(updatedList.size() - 1);
                            if (lastPos >= 0 && dataList.get(lastPos).getId().equals(newMessage.getId())) {
                                dataList.set(lastPos, newMessage);
                                messageAdapter.notifyItemChanged(lastPos);
                            } else {
                                if (newMessage != null && !newMessage.getDelete()
                                        .equalsIgnoreCase(userMe.getId())) {
                                    dataList.add(newMessage);
                                }
                                messageAdapter.notifyItemInserted(lastPos + 1);
                            }
                            for (int i = 0; i < dataList.size(); i++) {
                                if (dataList.get(i).getRecipientId().equalsIgnoreCase(userMe.getId())
                                        && !dataList.get(i).isReadMsg())
                                    BLiveApplication.getChatRef().child(chatChild).child(dataList.get(i).getId()).child("readMsg").setValue(true);
                            }
                            recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (updatedList.size() == 0) {
                    dataList.clear();
                    messageAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                ChatUtils.closeKeyboard(this, view);
                onBackPressed();
                break;
            case R.id.add_attachment:
                ChatUtils.closeKeyboard(this, view);
                if (myAttachmentLLY.getVisibility() == View.VISIBLE) {
                    myAttachmentLLY.setVisibility(View.GONE);
                    addAttachment.animate().setDuration(400).rotationBy(-45).start();
                } else {
                    myAttachmentLLY.setVisibility(View.VISIBLE);
                    addAttachment.animate().setDuration(400).rotationBy(45).start();
                    emojIcon.dismiss();
                }
                break;
            case R.id.send:
                if (!TextUtils.isEmpty(newMessage.getText().toString().trim())) {
                    BLiveApplication.getChatRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.hasChild(chatChild))
                                chatChild = user.getId() + "-" + userMe.getId();

                            sendMessage(newMessage.getText().toString(), AttachmentTypes.NONE_TEXT);
                            newMessage.setText("");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
                break;
            case R.id.chatToolbarContent:
                if (toolbarContent.getVisibility() == View.VISIBLE) {
                    if (user != null) {
                        Intent intent = new Intent(this, ActivityViewProfile.class);
                        intent.putExtra("image", user.getImage());
                        intent.putExtra("userId", user.getId());
                        intent.putExtra("from", "chat");
                        intent.putExtra("chatUser", user);
                        startActivity(intent);
                    }
                }
                break;
            case R.id.attachment_emoji:
                emojIcon.toggle();
                break;
            case R.id.attachment_gallery:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setMessage("Get image from");
                alertDialog.setPositiveButton("Camera", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    openImageClick();
                });
                alertDialog.setNegativeButton("Gallery", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    openImagePick();
                });
                alertDialog.create().show();
                break;
            case R.id.attachment_audio:
                openAudioPicker();
                break;
            case R.id.attachment_video:
                openVideoPicker();
               /* AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(this);
                alertDialog1.setMessage("Get Video from");
                alertDialog1.setPositiveButton("Camera", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    startActivityForResult(new Intent(FireBaseChatActivity.this, CameraActivity.class), 123);
                });
                alertDialog1.setNegativeButton("Gallery", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    openVideoPicker();
                });
                alertDialog1.create().show();*/

                break;
        }
    }

    private void prepareMessage(int attachmentType, Attachment attachment) {
        Message message = new Message();
        message.setAttachmentType(attachmentType);
        message.setAttachment(attachment);
        message.setBody(null);
        message.setDate(System.currentTimeMillis());
        message.setSenderId(userMe.getId());
        message.setSenderName(userMe.getName());
        message.setSent(false);
        message.setDelivered(false);
        message.setRecipientId(userOrGroupId);
        message.setId(attachment.getUrl() + attachment.getBytesCount() + attachment.getName());

        ChatUtils.deleteMessageFromRealm(rChatDb, message.getId());

        String userId = message.getRecipientId();
        String myId = message.getSenderId();
        Chat chat = ChatUtils.getChat(rChatDb, myId, userId).findFirst();
        rChatDb.beginTransaction();
        if (chat == null) {
            chat = rChatDb.createObject(Chat.class);
            chat.setMessages(new RealmList<>());
            chat.setLastMessage(message.getBody());
            chat.setMyId(myId);
            chat.setTimeUpdated(message.getDate());
            if (user != null) {
                chat.setUser(rChatDb.copyToRealm(user));
                chat.setUserId(userId);
            }
        }
        chat.setTimeUpdated(message.getDate());
        chat.getMessages().add(message);
        chat.setLastMessage(message.getBody());
        rChatDb.commitTransaction();

    }

    private void sendMessage(String messageBody, @AttachmentTypes.AttachmentType int attachmentType) {
        Message message = new Message();
        message.setAttachmentType(attachmentType);
        if (attachmentType != AttachmentTypes.NONE_TEXT)
            message.setAttachment(null);
        else
            BaseMessageViewHolder.animate = true;
        message.setBody(messageBody);
        message.setDate(System.currentTimeMillis());
        message.setSenderId(userMe.getId());
        message.setSenderName(userMe.getName());
        message.setSent(true);
        message.setDelivered(false);
        message.setRecipientId(userOrGroupId);
        message.setId(BLiveApplication.getChatRef().child(chatChild).push().getKey());
        message.setReplyId(replyId);
        message.setDelete("");

        BLiveApplication.getChatRef().child(chatChild).child(message.getId()).setValue(message);
        replyLay.setVisibility(View.GONE);
        replyId = "0";
        KeyboardUtil.getInstance(this).closeKeyboard();
    }

    private void checkAndCopy(String directory, File source) {
        File file = new File(Environment.getExternalStorageDirectory(), directory);
        boolean dirExists = file.exists();
        if (!dirExists)
            dirExists = file.mkdirs();
        if (dirExists) {
            try {
                file = new File(Environment.getExternalStorageDirectory()
                        + directory, Uri.fromFile(source).getLastPathSegment());
                boolean fileExists = file.exists();
                if (!fileExists)
                    fileExists = file.createNewFile();
                if (fileExists && file.length() == 0) {
                    FileUtils.copyFile(source, file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void openAudioPicker() {
        if (permissionsAvailable(permissionsStorage)) {
            audioPicker = new AudioPicker(this);
            audioPicker.setAudioPickerCallback(this);
            audioPicker.pickAudio();
        } else {
            ActivityCompat.requestPermissions(this, permissionsStorage, 25);
        }
    }

    public void openImagePick() {
        if (permissionsAvailable(permissionsStorage)) {
            imagePicker = new ImagePicker(this);
            imagePicker.shouldGenerateMetadata(true);
            imagePicker.shouldGenerateThumbnails(true);
            imagePicker.setImagePickerCallback(this);
            imagePicker.pickImage();
        } else {
            ActivityCompat.requestPermissions(this, permissionsStorage, 36);
        }
    }

    void openImageClick() {
        if (permissionsAvailable(permissionsCamera)) {
            cameraPicker = new CameraImagePicker(this);
            cameraPicker.shouldGenerateMetadata(true);
            cameraPicker.shouldGenerateThumbnails(true);
            cameraPicker.setImagePickerCallback(this);
            pickerPath = cameraPicker.pickImage();
        } else {
            ActivityCompat.requestPermissions(this, permissionsCamera, 47);
        }
    }

    private void openVideoPicker() {
        if (permissionsAvailable(permissionsStorage)) {
            videoPicker = new VideoPicker(this);
            videoPicker.shouldGenerateMetadata(true);
            videoPicker.shouldGeneratePreviewImages(true);
            videoPicker.setVideoPickerCallback(this);
            videoPicker.pickVideo();
        } else {
            ActivityCompat.requestPermissions(this, permissionsStorage, 41);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 25:
                if (permissionsAvailable(permissions))
                    openAudioPicker();
                break;
            case 36:
                if (permissionsAvailable(permissions))
                    openImagePick();
                break;
            case 47:
                if (permissionsAvailable(permissions))
                    openImageClick();
                break;
            case 41:
                if (permissionsAvailable(permissions))
                    openVideoPicker();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK) {
            switch (requestCode) {
                case Picker.PICK_IMAGE_DEVICE:
                    if (imagePicker == null) {
                        imagePicker = new ImagePicker(this);
                        imagePicker.setImagePickerCallback(this);
                    }
                    imagePicker.submit(data);
                    break;
                case Picker.PICK_IMAGE_CAMERA:
                    if (cameraPicker == null) {
                        cameraPicker = new CameraImagePicker(this);
                        cameraPicker.setImagePickerCallback(this);
                        cameraPicker.reinitialize(pickerPath);
                    }
                    cameraPicker.submit(data);
                    break;
                case Picker.PICK_VIDEO_DEVICE:
                    if (videoPicker == null) {
                        videoPicker = new VideoPicker(this);
                        videoPicker.setVideoPickerCallback(this);
                    }
                    videoPicker.submit(data);
                    break;
                case Picker.PICK_AUDIO:
                    audioPicker.submit(data);
                    break;
                case 123:
                    uploadThumbnail(data.getExtras().getString("videoPath"));
                    break;
            }
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_UPDATE_USER:
                    user = data.getParcelableExtra(EXTRA_DATA_USER);
                    userUpdated(user);
                    break;
            }
        }
    }

    private void uploadImage(String filePath) {
        newFileUploadTask(filePath, AttachmentTypes.IMAGE, null);
    }

    private void uploadThumbnail(final String filePath) {
        Toast.makeText(this, "Just a moment..", Toast.LENGTH_LONG).show();
        File file = new File(filePath);
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child(getString(R.string.app_name)).child("video").child("thumbnail").child(file.getName() + ".jpg");

        storageReference.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Attachment attachment = new Attachment();
                    attachment.setData(uri.toString());
                    newFileUploadTask(filePath, AttachmentTypes.VIDEO, attachment);
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        @SuppressLint("StaticFieldLeak") AsyncTask<String, Void, Bitmap> thumbnailTask =
                                new AsyncTask<String, Void, Bitmap>() {
                                    @Override
                                    protected Bitmap doInBackground(String... params) {
                                        return ThumbnailUtils.createVideoThumbnail(params[0], MediaStore.Video.Thumbnails.MINI_KIND);
                                    }

                                    @Override
                                    protected void onPostExecute(Bitmap bitmap) {
                                        super.onPostExecute(bitmap);
                                        if (bitmap != null) {
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                            byte[] data = baos.toByteArray();
                                            UploadTask uploadTask = storageReference.putBytes(data);

                                            uploadTask.continueWithTask(task -> {
                                                if (!task.isSuccessful()) {
                                                    throw task.getException();
                                                }
                                                return storageReference.getDownloadUrl();
                                            })
                                                    .addOnCompleteListener(task -> {
                                                        if (task.isSuccessful()) {
                                                            Uri downloadUri = task.getResult();
                                                            Attachment attachment = new Attachment();
                                                            attachment.setData(downloadUri.toString());
                                                            newFileUploadTask(filePath, AttachmentTypes.VIDEO, attachment);
                                                        } else {
                                                            newFileUploadTask(filePath, AttachmentTypes.VIDEO, null);
                                                        }
                                                    })
                                                    .addOnFailureListener(e1 -> newFileUploadTask(filePath,
                                                            AttachmentTypes.VIDEO, null));
                                        } else
                                            newFileUploadTask(filePath, AttachmentTypes.VIDEO, null);
                                    }
                                };
                        thumbnailTask.execute(filePath);
                    }
                });
    }

    private void newFileUploadTask(String filePath,
                                   @AttachmentTypes.AttachmentType final int attachmentType, final Attachment attachment) {
        if (myAttachmentLLY.getVisibility() == View.VISIBLE) {
            myAttachmentLLY.setVisibility(View.GONE);
            addAttachment.animate().setDuration(400).rotationBy(-45).start();
        }

        final File fileToUpload = new File(filePath);
        final String fileName = Uri.fromFile(fileToUpload).getLastPathSegment();

        Attachment preSendAttachment = attachment;
        if (preSendAttachment == null) preSendAttachment = new Attachment();
        preSendAttachment.setName(fileName);
        preSendAttachment.setBytesCount(fileToUpload.length());
        preSendAttachment.setUrl("loading");
        prepareMessage(attachmentType, preSendAttachment);

        checkAndCopy("/" + getString(R.string.app_name) + "/" +
                AttachmentTypes.getTypeName(attachmentType) + "/.sent/", fileToUpload);

        Intent intent = new Intent(ChatUtils.UPLOAD_AND_SEND);
        intent.putExtra("attachment", attachment);
        intent.putExtra("attachment_type", attachmentType);
        intent.putExtra("attachment_file_path", filePath);
        intent.putExtra("attachment_file_path", filePath);
        intent.putExtra("attachment_recipient_id", userOrGroupId);
        intent.putExtra("attachment_chat_child", chatChild);
        intent.putExtra("attachment_reply_id", replyId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        replyLay.setVisibility(View.GONE);
        replyId = "0";
        KeyboardUtil.getInstance(this).closeKeyboard();
    }

    public void downloadFile(DownloadFileEvent downloadFileEvent) {
        if (permissionsAvailable(permissionsStorage)) {
            new DownloadUtil().checkAndLoad(this, downloadFileEvent);
            adapterPositions.add(downloadFileEvent.getPosition());
        } else {
            ActivityCompat.requestPermissions(this, permissionsStorage, 47);
        }
    }

    @Override
    public void OnMessageClick(Message message, int position) {
        if (ChatUtils.CHAT_CAB && RealmObject.isValid(message)) {
            message.setSelected(!message.isSelected());
            messageAdapter.notifyItemChanged(position);
            if (message.isSelected())
                countSelected++;
            else
                countSelected--;

            selectedCount.setText(String.valueOf(countSelected));
            if (countSelected == 0)
                undoSelectionPrepared();
        }
    }

    @Override
    public void OnMessageLongClick(Message message, int position) {
        if (!ChatUtils.CHAT_CAB && RealmObject.isValid(message)) {
            prepareToSelect();
            message.setSelected(true);
            messageAdapter.notifyItemChanged(position);
            countSelected++;
            selectedCount.setText(String.valueOf(countSelected));
        }
    }

    private void prepareToSelect() {
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_chat_cab);
        getSupportActionBar().setTitle("");
        selectedCount.setText("1");
        selectedCount.setVisibility(View.VISIBLE);
        toolbarContent.setVisibility(View.GONE);
        ChatUtils.CHAT_CAB = true;
    }

    private void undoSelectionPrepared() {
        for (Message msg : dataList) {
            msg.setSelected(false);
        }
        countSelected = 0;
        messageAdapter.notifyDataSetChanged();
        toolbar.getMenu().clear();
        selectedCount.setVisibility(View.GONE);
        toolbarContent.setVisibility(View.VISIBLE);
        ChatUtils.CHAT_CAB = false;
    }

    public static Intent newIntent(Context context, ArrayList<Message> forwardMessages, ChatUser user) {
        Intent intent = new Intent(context, FireBaseChatActivity.class);
        intent.putExtra(EXTRA_DATA_USER, user);
        if (forwardMessages == null)
            forwardMessages = new ArrayList<>();
        intent.putParcelableArrayListExtra(EXTRA_DATA_LIST, forwardMessages);
        return intent;
    }

    @Override
    public boolean isRecordingPlaying(String fileName) {
        return isMediaPlayerPlaying() && currentlyPlaying.equals(fileName);
    }

    private boolean isMediaPlayerPlaying() {
        try {
            return mediaPlayer.isPlaying();
        } catch (IllegalStateException ex) {
            return false;
        }
    }

    @Override
    public void playRecording(File file, String fileName, int position) {
        if (recordPermissionsAvailable()) {
            if (isMediaPlayerPlaying()) {
                mediaPlayer.stop();
                notifyRecordingPlaybackCompletion();
                if (!fileName.equals(currentlyPlaying)) {
                    if (startPlayback(file)) {
                        currentlyPlaying = fileName;
                        messageAdapter.notifyItemChanged(position);
                    }
                }
            } else {
                if (startPlayback(file)) {
                    currentlyPlaying = fileName;
                    messageAdapter.notifyItemChanged(position);
                }
            }
        } else {
            ActivityCompat.requestPermissions(this, permissionsRecord, REQUEST_PERMISSION_RECORD);
        }
    }

    private boolean startPlayback(File file) {
        boolean started = true;
        resetMediaPlayer();
        try {
            FileInputStream is = new FileInputStream(file);
            FileDescriptor fd = is.getFD();
            mediaPlayer.setDataSource(fd);
            is.close();
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            started = false;
        }
        return started;
    }

    private void resetMediaPlayer() {
        try {
            mediaPlayer.reset();
        } catch (IllegalStateException ex) {
            mediaPlayer = new MediaPlayer();
        }
    }

    private void notifyRecordingPlaybackCompletion() {
        if (recyclerView != null && messageAdapter != null) {
            int total = dataList.size();
            for (int i = total - 1; i >= 0; i--) {
                if (dataList.get(i).getAttachment() != null) {
                    if (dataList.get(i).getAttachment().getName().contains(".wav")) {
                        if (dataList.get(i).getAttachment().getName().replace(".wav", ".mp3").equals(currentlyPlaying)) {
                            messageAdapter.notifyItemChanged(i);
                            break;
                        }
                    } else {
                        if (dataList.get(i).getAttachment().getName().equals(currentlyPlaying)) {
                            messageAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onVideosChosen(List<ChosenVideo> list) {
        if (list != null && !list.isEmpty()) {
            //  if (list.get(0).getSize() < 16777216)
            uploadThumbnail(Uri.parse(list.get(0).getOriginalPath()).getPath());
            // else
            //   Toast.makeText(this, "Maximum limit is 16 MB", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAudiosChosen(List<ChosenAudio> list) {
        if (list != null && !list.isEmpty())
            newFileUploadTask(Uri.parse(list.get(0).getOriginalPath()).getPath(), AttachmentTypes.AUDIO, null);
    }

    @Override
    public void onImagesChosen(List<ChosenImage> list) {
        if (list != null && !list.isEmpty()) {
            Uri originalFileUri = Uri.parse(list.get(0).getOriginalPath());
            File tempFile = new File(getCacheDir(), originalFileUri.getLastPathSegment());
            try {
                uploadImage(SiliCompressor.with(this).compress(originalFileUri.toString(), tempFile));
            } catch (Exception ex) {
                uploadImage(originalFileUri.getPath());
            }
        }
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("picker_path", pickerPath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("picker_path")) {
                pickerPath = savedInstanceState.getString("picker_path");
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
                replyLay.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() ->
                        messageAdapter.notifyItemChanged(viewHolder.getAdapterPosition()), 500);

                String nameToDisplay = "";
                if (myUsersNameInPhoneMap != null && myUsersNameInPhoneMap
                        .containsKey(dataList.get(viewHolder.getAdapterPosition()).getSenderId())) {
                    nameToDisplay = myUsersNameInPhoneMap.get(dataList.get(viewHolder.getAdapterPosition())
                            .getSenderId()).getNameToDisplay();
                }
                replyId = dataList.get(viewHolder.getAdapterPosition()).getId();
                replyName.setText(nameToDisplay);
                replyImg.setBackgroundResource(0);
                replyImg.setVisibility(View.VISIBLE);

                if (dataList.get(viewHolder.getAdapterPosition()).getAttachmentType() == AttachmentTypes.AUDIO) {
                    Picasso.get()
                            .load(R.drawable.ic_music)
                            .tag(FireBaseChatActivity.this)
                            .placeholder(R.drawable.ic_music)
                            .into(replyImg);
                } else if (dataList.get(viewHolder.getAdapterPosition()).getAttachmentType() == AttachmentTypes.RECORDING) {
                    Picasso.get()
                            .load(R.drawable.ic_music)
                            .tag(FireBaseChatActivity.this)
                            .placeholder(R.drawable.ic_music)
                            .into(replyImg);
                } else if (dataList.get(viewHolder.getAdapterPosition()).getAttachmentType() == AttachmentTypes.VIDEO) {
                    if (dataList.get(viewHolder.getAdapterPosition()).getAttachment().getData() != null)
                        Picasso.get()
                                .load(dataList.get(viewHolder.getAdapterPosition()).getAttachment().getData())
                                .tag(FireBaseChatActivity.this)
                                .placeholder(R.mipmap.ic_launcher)
                                .into(replyImg);
                    else
                        replyImg.setBackgroundResource(R.mipmap.ic_launcher);
                } else if (dataList.get(viewHolder.getAdapterPosition()).getAttachmentType() == AttachmentTypes.IMAGE) {
                    if (dataList.get(viewHolder.getAdapterPosition()).getAttachment().getUrl() != null)
                        Picasso.get()
                                .load(dataList.get(viewHolder.getAdapterPosition()).getAttachment().getUrl())
                                .tag(FireBaseChatActivity.this)
                                .placeholder(R.mipmap.ic_launcher)
                                .into(replyImg);
                    else
                        replyImg.setBackgroundResource(R.mipmap.ic_launcher);
                } else if (dataList.get(viewHolder.getAdapterPosition()).getAttachmentType() == AttachmentTypes.NONE_TEXT) {
                    replyName.setText(nameToDisplay + "\n" + dataList.get(viewHolder.getAdapterPosition()).getBody());
                    replyImg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView
                    recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void getChatChild() {
        utils.showProgress();
        chatChild = userMe.getId() + "-" + user.getId();
        BLiveApplication.getChatRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(chatChild)) {
                    chatChild = user.getId() + "-" + userMe.getId();
                }
                utils.hideProgress();
                RealmQuery<Chat> query = ChatUtils.getChat(rChatDb, userMe.getId(), userOrGroupId);
                queryResult = query.findAll();
                queryResult.addChangeListener(realmChangeListener);
                Chat prevChat = query.findFirst();
                if (prevChat != null) {

                    for (int i = 0; i < prevChat.getMessages().size(); i++) {
                        if (prevChat.getMessages().get(i) != null && !prevChat.getMessages().get(i).getDelete().equalsIgnoreCase(userMe.getId())) {
                            dataList.add(prevChat.getMessages().get(i));
                        }
                    }

                    for (int i = 0; i < dataList.size(); i++) {
                        if (!dataList.get(i).getSenderId().equalsIgnoreCase(userMe.getId()) && !dataList.get(i).isReadMsg())
                            BLiveApplication.getChatRef().child(chatChild).child(dataList.get(i).getId()).child("readMsg").setValue(true);
                    }
                    messageAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
