package com.codewithshubh.servozone.Model;

public class Notification {
    private String title;
    private String message;
    private String iconUrl;
    private String imageUrl;
    private String action;
    private String actionDestination;
    private String notificationUid;
    private String notificationId;
    private boolean isRead;
    private long timeStamp;

    public Notification() {
    }

    public Notification(String title, String message, String iconUrl, String imageUrl, String action,
                         String actionDestination, String notificationUid, String notificationId, boolean isRead, long timeStamp) {
        this.title = title;
        this.message = message;
        this.iconUrl = iconUrl;
        this.imageUrl = imageUrl;
        this.action = action;
        this.actionDestination = actionDestination;
        this.notificationUid = notificationUid;
        this.notificationId = notificationId;
        this.isRead = isRead;
        this.timeStamp = timeStamp;
    }

    public Notification(String title, String message, String iconUrl, String imageUrl, String action,
                        String actionDestination, String notificationUid, String notificationId, boolean isRead) {
        this.title = title;
        this.message = message;
        this.iconUrl = iconUrl;
        this.imageUrl = imageUrl;
        this.action = action;
        this.actionDestination = actionDestination;
        this.notificationUid = notificationUid;
        this.notificationId = notificationId;
        this.isRead = isRead;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActionDestination() {
        return actionDestination;
    }

    public void setActionDestination(String actionDestination) {
        this.actionDestination = actionDestination;
    }

    public String getNotificationUid() {
        return notificationUid;
    }

    public void setNotificationUid(String notificationUid) {
        this.notificationUid = notificationUid;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
