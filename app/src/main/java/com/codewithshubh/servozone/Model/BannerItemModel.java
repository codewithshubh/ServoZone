package com.codewithshubh.servozone.Model;

public class BannerItemModel {
    private String imageUrl;

    public BannerItemModel(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BannerItemModel() {

    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
