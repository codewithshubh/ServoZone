package com.codewithshubh.servozone.Service;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.codewithshubh.servozone.Activity.MainActivity;
import com.codewithshubh.servozone.Model.Notification;
import com.codewithshubh.servozone.Utils.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;
import static com.codewithshubh.servozone.Constant.Constants.ACTION;
import static com.codewithshubh.servozone.Constant.Constants.ACTION_DESTINATION;
import static com.codewithshubh.servozone.Constant.Constants.IMAGE;
import static com.codewithshubh.servozone.Constant.Constants.MESSAGE;
import static com.codewithshubh.servozone.Constant.Constants.NOTIFICATION_UID;
import static com.codewithshubh.servozone.Constant.Constants.TITLE;
import static com.codewithshubh.servozone.Constant.Constants.ICON;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            handleData(data);

        } else if (remoteMessage.getNotification() != null) {
            handleNotification(remoteMessage.getNotification());
        }// Check if message contains a notification payload.
    }

    private void handleNotification(RemoteMessage.Notification RemoteMsgNotification) {
        String message = RemoteMsgNotification.getBody();
        String title = RemoteMsgNotification.getTitle();
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);

        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.displayNotification(notification, resultIntent);

        //notificationUtils.playNotificationSound();
    }

    private void handleData(Map<String, String> data) {
        String title = data.get(TITLE);
        String message = data.get(MESSAGE);
        String imageUrl = data.get(IMAGE);
        String iconUrl = data.get(ICON);
        String action = data.get(ACTION);
        String actionDestination = data.get(ACTION_DESTINATION);
        String uid = data.get(NOTIFICATION_UID);
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setIconUrl(iconUrl);
        notification.setImageUrl(imageUrl);
        notification.setAction(action);
        notification.setActionDestination(actionDestination);
        notification.setNotificationUid(uid);
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.displayNotification(notification, resultIntent);
        //notificationUtils.playNotificationSound();
    }


}
