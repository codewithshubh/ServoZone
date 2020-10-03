package com.codewithshubh.servozone.Model;

public class User {
    private String Name, Phone, Email, ImageUrl, UID, role, deviceToken;
    private boolean activeStatus;
    long creationTimeStamp;
    public User() {
    }

    public User(String name, String phone, String email, String imageUrl, String uid, boolean activeStatus, String role, long creationTimeStamp, String deviceToken) {
        Name = name;
        Phone = phone;
        Email = email;
        ImageUrl = imageUrl;
        UID = uid;
        this.activeStatus = activeStatus;
        this.role = role;
        this.creationTimeStamp = creationTimeStamp;
        this.deviceToken = deviceToken;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String uid) {
        UID = uid;
    }

    public boolean isActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public long getCreationTimeStamp() {
        return creationTimeStamp;
    }

    public void setCreationTimeStamp(long creationTimeStamp) {
        this.creationTimeStamp = creationTimeStamp;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
