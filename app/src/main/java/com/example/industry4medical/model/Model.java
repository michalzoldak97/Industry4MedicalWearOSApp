package com.example.industry4medical.model;

import android.app.Application;

import com.example.industry4medical.model.API.API;
import com.example.industry4medical.model.API.APIListener;
import com.example.industry4medical.model.API.AbstractAPIListener;
import com.example.industry4medical.model.API.WebAPI;

import org.json.JSONArray;
import org.json.JSONObject;

public class Model {
    private static Model sInstance = null;
    private final API mApi;
    private User mUser;
    public User getUser() {return mUser;}
    public void setUser(User user) {this.mUser = user;}

    public static Model getInstance(Application application){
        if(sInstance == null){
            sInstance = new Model(application);
        }
        return sInstance;
    }
    private final Application mApplication;

    private Model(Application application) {
        mApplication = application;
        mApi = new WebAPI(this, mApplication);
    }

    public Application getApplication() {return mApplication;}

    public void login(String email, String password, APIListener listener){
        mApi.login(email, password, listener);
    }
    public void sendData(JSONObject sleepData, APIListener listener){
        mApi.sendSleepData(sleepData, listener);
    }
}
