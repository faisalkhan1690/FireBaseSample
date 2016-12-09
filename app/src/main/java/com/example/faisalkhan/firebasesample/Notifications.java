package com.example.faisalkhan.firebasesample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 *
 * Activity class for firebase notification.
 * Data will show on this activity when user is on this activity and receives a notification.
 *
 * Firebase Library has in build notification handler. mean you don't need to do any thing just send notification from firebase console.
 * And on clicking that notification your app will launch.
 *
 * But for doing custom handling you need to create two services. I have create in this project please refer classes MyFirebaseInstanceIDService.class
 * and MyFirebaseMessagingService.class
 *
 * For more info how to implement notification please refer link :- https://firebase.google.com/docs/notifications/
 *
 * @author Faisal Khan
 */
public class Notifications extends AppCompatActivity {

    private TextView mTvTitleNotification;
    private TextView mTvMessageNotification;
    private ImageView mIvImageNotification;
    public static final String FIREBASE_NOTIFICATION_INTENT_FILTER_NAME="firebase-notification";
    public static final String NOTIFICATION_TITLE="notification_title";
    public static final String NOTIFICATION_MESSAGE="notification_message";
    public static final String NOTIFICATION_IMAGE="notification_image";

    private final String TAG=Notifications.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        //setting action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Notifications");

        mTvTitleNotification=(TextView)findViewById(R.id.tv_title);
        mTvMessageNotification=(TextView)findViewById(R.id.tv_message);
        mIvImageNotification=(ImageView)findViewById(R.id.iv_image);

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("firebase-notification"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    /**
     * Broadcast receiver for receiving broadcast notification from MyFirebaseMessagingService
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle=intent.getExtras();
            if(bundle!=null){
                Log.d(TAG,"Receive message in mMessageReceiver");
                if(bundle.containsKey(NOTIFICATION_TITLE)){
                    mTvTitleNotification.setText(bundle.getString(NOTIFICATION_TITLE));
                }
                if(bundle.containsKey(NOTIFICATION_MESSAGE)){
                    mTvMessageNotification.setText(bundle.getString(NOTIFICATION_MESSAGE));
                }
                if(bundle.containsKey(NOTIFICATION_IMAGE)){
                    String imgUrl=bundle.getString(NOTIFICATION_IMAGE);
                    if(imgUrl!=null) {
                        Glide.with(Notifications.this).load(imgUrl)
                                .thumbnail(Glide.with(Notifications.this).load(R.drawable.default_loader))
                                .fitCenter().crossFade().into(mIvImageNotification);
                    }
                }
            }
        }
    };
}
