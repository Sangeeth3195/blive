package com.blive.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blive.R;
import com.blive.adapter.NotificationListAdapter;
import com.blive.db.SqlDb;
import com.blive.model.FCMModel;
import com.blive.session.SessionManager;

import java.util.ArrayList;

public class ActivityNotification extends AppCompatActivity implements View.OnClickListener {
    SqlDb sqlDb;
    RecyclerView recyclerView_notification;
    NotificationListAdapter adapter;
    ArrayList<FCMModel> fcmModels = new ArrayList<>();
    Button delete_notification;
    TextView tv_title;
    SessionManager sessionManager;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__notification);
        sessionManager = new SessionManager(this);
        sessionManager.storeSessionStringvalue("notification","notification","0");

        recyclerView_notification = findViewById(R.id.recycler_notify);
        delete_notification = findViewById(R.id.delete_notification);
        tv_title = findViewById(R.id.tv_title);
        back = findViewById(R.id.back);
        tv_title.setText("BLIVE TEAM");

        back.setOnClickListener(this);
        delete_notification.setOnClickListener(this);

        sqlDb = new SqlDb(this);
//        add();

        fcmModels.clear();

        fcmModels = sqlDb.getNotificationData();
        Log.d("Logs", String.valueOf(fcmModels.size()));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView_notification.setLayoutManager(linearLayoutManager);
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        adapter = new NotificationListAdapter(this, fcmModels);
        recyclerView_notification.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

//    private void add() {
//
//
//        FCMModel fcmModel = new FCMModel();
//        fcmModel.setNotificationTitle("empty");
//        fcmModel.setNotificationImage("https://homepages.cae.wisc.edu/~ece533/images/airplane.png");
//        fcmModel.setNotificationcontent("empty");
//        sqlDb.insertNotificationData(fcmModel);
//    }

    @Override
    public void onClick(View v) {
        if (v == delete_notification) {
            sqlDb.deleteNotificationdata();
            Log.d("Logs", String.valueOf(fcmModels.size()));
            fcmModels.clear();
            fcmModels = sqlDb.getNotificationData();
            adapter.notifyDataSetChanged();
        } else if (v == back) {
            finish();
        }
    }
}
