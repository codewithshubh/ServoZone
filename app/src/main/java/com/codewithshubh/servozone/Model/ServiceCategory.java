package com.codewithshubh.servozone.Model;

public class ServiceCategory {
    private String categoryName, imageUrl, charges, id, serviceId;
    boolean activeStatus;
    long creationTimeStamp, updateTimeStamp;

    public ServiceCategory() {
    }

    public ServiceCategory(String categoryName, String imageUrl, String charges, String id, String serviceId, boolean activeStatus,
                           long creationTimeStamp, long updateTimeStamp) {
        this.categoryName = categoryName;
        this.imageUrl = imageUrl;
        this.charges = charges;
        this.id = id;
        this.serviceId = serviceId;
        this.activeStatus = activeStatus;
        this.creationTimeStamp = creationTimeStamp;
        this.updateTimeStamp = updateTimeStamp;
    }

    public String getCharges() {
        return charges;
    }

    public void setCharges(String charges) {
        this.charges = charges;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public boolean isActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    public long getCreationTimestamp() {
        return creationTimeStamp;
    }

    public void setCreationTimestamp(long creationTimeStamp) {
        this.creationTimeStamp = creationTimeStamp;
    }

    public long getUpdationTimestamp() {
        return updateTimeStamp;
    }

    public void setUpdationTimestamp(long updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }
}
