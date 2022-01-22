package com.example.industry4medical.model.API;

import android.app.Application;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.industry4medical.model.Model;
import com.example.industry4medical.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class WebAPI implements API{

    public static final String BASE_URL = "http://192.168.1.17";

    private final Application mApplication;
    private final Model mModel;
    private RequestQueue mRequestQueue;

    public WebAPI(Model model, Application application){
        mApplication = application;
        mRequestQueue = Volley.newRequestQueue(application);
        mModel = model;
    }

    public void login(String email, String password, final APIListener listener){

        String url = BASE_URL + ":8092/login";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", email);
            jsonObject.put("password", password);

            Response.Listener<JSONObject> successListener = response -> {
                User user = User.getUser(response);
                if(user != null){
                    listener.onLogin(user);
                }
            };

            Response.ErrorListener errorListener = error -> {
                Toast.makeText(mApplication, "Error: Invalid user credentials" /*+ error.toString()*/, Toast.LENGTH_LONG).show();

            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    successListener, errorListener);

            mRequestQueue.add(request);

        }catch (JSONException e) {
            Toast.makeText(mApplication, "JSON exception" + e.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void sendSleepData(JSONObject sleepData, APIListener listener) {
        String url = BASE_URL + ":8091/smartwatchdata";

        Response.Listener<JSONObject> successListener = response -> {
            if(listener != null){
                listener.onPackageSent();
            }
        };
        Response.ErrorListener errorListener = error -> Toast.makeText(mApplication, "Send Error response", Toast.LENGTH_LONG).show();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, sleepData,
                successListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer " + mModel.getUser().getWebToken());
                return headers;
            }
        };
        System.out.println("Sleep data: " + sleepData);
        mRequestQueue.add(request);
    }
}
