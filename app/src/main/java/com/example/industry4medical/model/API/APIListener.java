package com.example.industry4medical.model.API;

import com.example.industry4medical.model.User;

import org.json.JSONObject;

public interface APIListener {
    void onLogin(User user);
    void onPackageSent();
}
