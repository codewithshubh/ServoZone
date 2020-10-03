package com.codewithshubh.servozone.Interface;

import com.codewithshubh.servozone.Model.ServiceGroup;

import java.util.List;

public interface IFirebaseLoadListener {

    void onFirebaseLoadSuccess(List<ServiceGroup> serviceGroupList);
    void onFirebaseLoadFailed(String message);
}
