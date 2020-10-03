package com.codewithshubh.servozone.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.codewithshubh.servozone.Activity.MainActivity;
import com.codewithshubh.servozone.Model.Notification;
import com.codewithshubh.servozone.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static com.codewithshubh.servozone.Constant.Constants.ACTION;
import static com.codewithshubh.servozone.Constant.Constants.ACTION_DESTINATION;
import static com.codewithshubh.servozone.Constant.Constants.ACTIVITY;
import static com.codewithshubh.servozone.Constant.Constants.CHANNEL_ID;
import static com.codewithshubh.servozone.Constant.Constants.CHANNEL_NAME;
import static com.codewithshubh.servozone.Constant.Constants.DATA;
import static com.codewithshubh.servozone.Constant.Constants.EMPTY;
import static com.codewithshubh.servozone.Constant.Constants.ICON;
import static com.codewithshubh.servozone.Constant.Constants.IMAGE;
import static com.codewithshubh.servozone.Constant.Constants.IMAGE_URL;
import static com.codewithshubh.servozone.Constant.Constants.MESSAGE;
import static com.codewithshubh.servozone.Constant.Constants.NOTIFICATION_UID;
import static com.codewithshubh.servozone.Constant.Constants.TITLE;
import static com.codewithshubh.servozone.Constant.Constants.TO;

public class NotificationUtils {
    private NotificationManager notificationManager;
    Map<String, Class> activityMap = new HashMap<>();
    private Context mContext;
    private static int NOTIFICATION_ID;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
        //Populate activity map
        activityMap.put("MainActivity", MainActivity.class);
    }

    /**
     * Displays notification based on parameters
     *
     * @param notification
     * @param resultIntent
     */
    public void displayNotification(Notification notification, Intent resultIntent) {
        {
            Random r = new Random();
            NOTIFICATION_ID = r.nextInt(1000000);
            String message = notification.getMessage();
            String title = notification.getTitle();
            String iconUrl = notification.getIconUrl();
            String imageUrl = notification.getImageUrl();
            String action = notification.getAction();
            String destination = notification.getActionDestination();
            String userUid = notification.getNotificationUid();
            String uniqueID = UUID.randomUUID().toString();
            SendNotificationDataToFirebase(title, message, iconUrl, imageUrl, userUid, NOTIFICATION_ID, uniqueID, action, destination);
            notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            Bitmap iconBitMap = null;
            Bitmap imageBitmap = null;
            if (iconUrl != null) {
                iconBitMap = getBitmapFromURL(iconUrl);
            }
            if (imageUrl != null)
            {
                imageBitmap = getBitmapFromURL(imageUrl);
            }
            final int icon = R.drawable.toolbar_icon;

            PendingIntent resultPendingIntent;

            if (IMAGE_URL.equals(action)) {
                Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(destination));

                resultPendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
            } else if (ACTIVITY.equals(action) && activityMap.containsKey(destination)) {
                resultIntent = new Intent(mContext, activityMap.get(destination));

                resultPendingIntent =
                        PendingIntent.getActivity(
                                mContext,
                                0,
                                resultIntent,
                                PendingIntent.FLAG_CANCEL_CURRENT
                        );
            } else {
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                resultPendingIntent =
                        PendingIntent.getActivity(
                                mContext,
                                0,
                                resultIntent,
                                PendingIntent.FLAG_CANCEL_CURRENT
                        );
            }

            //All notifications should go through NotificationChannel on Android 26 & above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Global Notifications");
                channel.enableLights(true);
                channel.setVibrationPattern(new long[]{0, 100, 200, 300});
                channel.enableVibration(true);
                Uri uri = Uri.parse("android.resource://"+ mContext.getPackageName()+"/" + R.raw.notification);

                AudioAttributes att = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build();
                channel.setSound(uri,att);
                notificationManager.createNotificationChannel(channel);

            }


            if (imageBitmap == null) {
                if (iconBitMap == null) {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                            mContext, CHANNEL_ID).setSmallIcon(icon)
                            .setWhen(System.currentTimeMillis())
                            .setAutoCancel(true)
                            .setContentTitle(title)
                            .setVibrate(new long[]{0, 100, 200, 300})
                            .setContentText(message)
                            .setContentIntent(resultPendingIntent)
                            .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.toolbar_icon))
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(message))
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                    NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                }
                else {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                            mContext, CHANNEL_ID).setSmallIcon(icon)
                            .setWhen(System.currentTimeMillis())
                            .setAutoCancel(true)
                            .setContentTitle(title)
                            .setVibrate(new long[]{0, 100, 200, 300})
                            .setContentText(message)
                            .setContentIntent(resultPendingIntent)
                            .setLargeIcon(iconBitMap)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(message))
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                    NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                }


            } else {
                if (iconBitMap == null) {
                    NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
                    bigPictureStyle.bigPicture(imageBitmap).bigLargeIcon(null);
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                            mContext, CHANNEL_ID).setSmallIcon(icon)
                            .setWhen(System.currentTimeMillis())
                            .setAutoCancel(true)
                            .setContentTitle(title)
                            .setContentIntent(resultPendingIntent)
                            .setStyle(bigPictureStyle)
                            .setLargeIcon(imageBitmap)
                            .setContentText(message)
                            .setVibrate(new long[]{0, 100, 200, 300})
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                    NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                }
                else {
                    //If Bitmap is created from URL, show big icon
                    NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
                    bigPictureStyle.bigPicture(imageBitmap).bigLargeIcon(null);
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                            mContext, CHANNEL_ID).setSmallIcon(icon)
                            .setWhen(System.currentTimeMillis())
                            .setAutoCancel(true)
                            .setContentTitle(title)
                            .setContentIntent(resultPendingIntent)
                            .setStyle(bigPictureStyle)
                            .setLargeIcon(iconBitMap)
                            .setContentText(message)
                            .setVibrate(new long[]{0, 100, 200, 300})
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                    NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                }
            }
        }
    }

    private void SendNotificationDataToFirebase(String title, String message, String iconUrl, String imageUrl,
                                                String uid, int notificationId, String uniqueID, String action, String destination) {
        iconUrl = iconUrl.isEmpty() || iconUrl == null ? "" : iconUrl;
        imageUrl = imageUrl.isEmpty() || imageUrl == null ? "" : imageUrl;
        DatabaseReference myRefNotificationSingle = FirebaseDatabase.getInstance().getReference("Notification").child(uniqueID);
        DatabaseReference myRefNotificationAll = FirebaseDatabase.getInstance().getReference("Notification");
        if (!uid.equals("") || uid!=null){
            Notification notificationModel = new Notification(title, message, iconUrl, imageUrl,action, destination, uid, String.valueOf(notificationId), false);
            myRefNotificationSingle.setValue(notificationModel);
            Map timestamp  = new HashMap();
            timestamp.put("timeStamp", ServerValue.TIMESTAMP);
            myRefNotificationSingle.updateChildren(timestamp);
        }
        else {

        }
    }

    /**
     * Downloads push notification image before displaying it in
     * the notification tray
     *
     * @param strURL : URL of the notification Image
     * @return : BitMap representation of notification Image
     */
    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Playing notification sound
     */
    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/" + R.raw.notification);
            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JsonObject buildNotificationPayload(String token, String title, String message, String iconUrl, String uid) {
        // compose notification json payload
        JsonObject payload = new JsonObject();
        payload.addProperty(TO, token);
        // compose data payload here
        JsonObject data = new JsonObject();
        data.addProperty(TITLE, title);
        data.addProperty(MESSAGE, message);
        data.addProperty(ICON, iconUrl);
        data.addProperty(IMAGE, EMPTY);
        data.addProperty(ACTION, EMPTY);
        data.addProperty(ACTION_DESTINATION, EMPTY);
        data.addProperty(NOTIFICATION_UID,uid);
        // add data payload
        payload.add(DATA, data);
        return payload;
    }
}
