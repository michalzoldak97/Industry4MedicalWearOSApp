package com.example.industry4medical.model.API;

import org.json.JSONArray;
import org.json.JSONObject;

public interface API {
    void login(String email, String password, final APIListener listener);

    void sendSleepData(JSONArray sleepData, final APIListener listener);
}
