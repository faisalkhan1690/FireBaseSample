package com.example.faisalkhan.firebasesample;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.net.URL;

import static com.example.faisalkhan.firebasesample.Notifications.FIREBASE_NOTIFICATION_INTENT_FILTER_NAME;
import static com.example.faisalkhan.firebasesample.Notifications.NOTIFICATION_IMAGE;
import static com.example.faisalkhan.firebasesample.Notifications.NOTIFICATION_MESSAGE;
import static com.example.faisalkhan.firebasesample.Notifications.NOTIFICATION_TITLE;

/**
 * Service Class for receiving notification from firebase.
 *
 * @author Faisal Khan
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String msg="";
        String title="";
        String image=null;
        if(remoteMessage!=null && remoteMessage.getNotification()!=null && remoteMessage.getNotification().getBody()!=null) {
            msg = remoteMessage.getNotification().getBody();
            Log.d(TAG,"Message in notification "+msg);
        }

        if(remoteMessage!=null && remoteMessage.getNotification()!=null && remoteMessage.getNotification().getTitle()!=null) {
            title = remoteMessage.getNotification().getTitle();
            Log.d(TAG,"Title in notification "+title);
        }

        if(remoteMessage!=null && remoteMessage.getData()!=null && remoteMessage.getData().containsKey("image")) {
            image = remoteMessage.getData().get("image");
            Log.d(TAG,"Image URl in notification "+image);
        }

        Intent intent = new Intent(FIREBASE_NOTIFICATION_INTENT_FILTER_NAME);
        intent.putExtra(NOTIFICATION_TITLE, title);
        intent.putExtra(NOTIFICATION_MESSAGE, msg);
        if (image != null)
            intent.putExtra(NOTIFICATION_IMAGE, image);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent); //send broadcast

        sendNotification(title, msg, getBitmapFromURL(image));
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param title title of notification
     * @param messageBody messageBody of notification
     * @param img image if available in notification
     */
    private void sendNotification(String title, String messageBody, Bitmap img) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder;
        if (img != null) {
            notificationBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title).setContentText(messageBody)
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(img))
                    .setAutoCancel(true).setSound(defaultSoundUri).setContentIntent(pendingIntent);
        } else {
            notificationBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title).setContentText(messageBody).setAutoCancel(true)
                    .setSound(defaultSoundUri).setContentIntent(pendingIntent);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    /**
     * Method to fetch image from url
     * @param src url
     * @return bitmap
     */
    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            return BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch(IOException e) {
            System.out.println(e);
            return null;
        }
    }
}

