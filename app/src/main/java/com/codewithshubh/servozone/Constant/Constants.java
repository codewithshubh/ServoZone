package com.codewithshubh.servozone.Constant;

public class Constants {
    public static final String SERVICE_ID = "service_id";
    public static final String SERVICE_NAME = "service_name";
    public static final String SERVICE_CATEGORY_NAME = "service_category_name";
    public static final String SERVICE_CATEGORY_ID = "service_category_id";
    public static final String SERVICE_IMAGE_URL = "service_image_url";
    public static final String CHARGES = "category_service_charge";
    public static final String BOOKING_ID = "booking_id";
    public static final String USER_NAME = "name";
    public static final String USER_ADDRESS = "address";
    public static final String USER_PHONE = "phone";
    public static final String USER_PIN = "pincode";
    public static final String ADDRESS_DETAIL = "addressDetail";
    public static final String ADDRESS_ID = "address_id";
    public static final String USER_NUMBER = "number";
    public static boolean IS_CONNECTED = false;

    //booking status
    public static final String PENDING = "pending";
    public static final String CONFIRMED = "confirmed";
    public static final String SM_ASSIGNED = "service_man_assigned";
    public static final String COMPLETED = "completed";
    public static final String CANCELLED = "cancelled";
    public static final String NA = "NA";

    //notification topic
    public static final String ALL_USERS = "global";
    public static final String REGISTERED_USERS = "registered";
    public static final String UNREGISTERED_USERS = "unregistered";

    //Firebase messaging
    public static final String TO = "to";
    public static final String TITLE = "title";
    public static final String EMPTY = "";
    public static final String MESSAGE = "message";
    public static final String IMAGE = "image";
    public static final String ICON = "icon";
    public static final String ACTION = "action";
    public static final String DATA = "data";
    public static final String ACTION_DESTINATION = "action_destination";
    public static final String NOTIFICATION_UID = "uid";


    //Notification
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String CHANNEL_ID = "myChannel";
    public static final String CHANNEL_NAME = "Global";
    public static final String IMAGE_URL = "url";
    public static final String ACTIVITY = "activity";
    public static int NOTIFICATION_COUNTER = 0;

    public static String MY_PREFERENCES = "mypreferences";

    public static final int SECOND_MILLIS = 1000;
    public static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    public static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    public static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    public static final int WEEK_MILLIS = 7 * DAY_MILLIS;
}
