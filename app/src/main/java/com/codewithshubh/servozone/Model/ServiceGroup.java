package com.codewithshubh.servozone.Model;

public class ServiceGroup {
    private String serviceName, serviceDetail, imageUrl;
    private long creationTimeStamp, updateTimeStamp;
    private String id;
    private boolean activeStatus;

    public ServiceGroup() {
    }

    public ServiceGroup(String serviceName, String serviceDetail, String imageUrl, long creationTimeStamp, long updateTimeStamp, String id, boolean activeStatus) {
        this.serviceName = serviceName;
        this.serviceDetail = serviceDetail;
        this.imageUrl = imageUrl;
        this.creationTimeStamp = creationTimeStamp;
        this.updateTimeStamp = updateTimeStamp;
        this.id = id;
        this.activeStatus = activeStatus;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceDetail() {
        return serviceDetail;
    }

    public void setServiceDetail(String serviceDetail) {
        this.serviceDetail = serviceDetail;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getCreationTimestamp() {
        return creationTimeStamp;
    }

    public void setCreationTimestamp(long creationTimestamp) {
        this.creationTimeStamp = creationTimestamp;
    }

    public long getUpdateTimestamp() {
        return updateTimeStamp;
    }

    public void setUpdateTimestamp(long updateTimestamp) {
        this.updateTimeStamp = updateTimestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(boolean activeStatus) {
        this.activeStatus = activeStatus;
    }
}
