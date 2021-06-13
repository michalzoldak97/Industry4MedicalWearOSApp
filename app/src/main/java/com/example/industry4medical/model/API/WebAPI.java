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

    public static final String BASE_URL = "http://77.55.208.10:8081";

    private final Application mApplication;
    private final Model mModel;
    private RequestQueue mRequestQueue;

    public WebAPI(Model model, Application application){
        mApplication = application;
        mRequestQueue = Volley.newRequestQueue(application);
        mModel = model;
    }

    public void login(String email, String password, final APIListener listener){

        String url = BASE_URL + "api/login";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);

            Response.Listener<JSONObject> successListener = response -> {
                try {
                    User user = User.getUser(response);
                    listener.onLogin(user);
                } catch (JSONException e) {
                    Toast.makeText(mApplication, "Successful response, user creation fail",
                            Toast.LENGTH_LONG).show();
                }
            };

            Response.ErrorListener errorListener = error -> {
                Toast.makeText(mApplication, "Error response", Toast.LENGTH_LONG).show();

                testSuccessfulLoginScenario(listener);

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
    public void sendSleepData(JSONArray sleepData, APIListener listener) {
        String url = BASE_URL + "/smartwatchdata";

        Response.Listener<JSONArray> successListener = response -> {
            if(listener != null){
                listener.onPackageSent();
            }
        };
        Response.ErrorListener errorListener = error -> Toast.makeText(mApplication, "Send Error response", Toast.LENGTH_LONG).show();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, url, sleepData,
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
        try {
            System.out.println(request.getHeaders());
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
        System.out.println(sleepData);
        mRequestQueue.add(request);
    }

    private void testSuccessfulLoginScenario(APIListener listener){
        try {
            JSONObject testData = new JSONObject();
            testData.put("name", "jrocket@example.com");
            testData.put("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE2MjM1ODIyOTIsImV4cCI6MTY1NTExODI5MiwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoianJvY2tldEBleGFtcGxlLmNvbSIsInVzZXJJRCI6IjEifQ.yAem0cQhijdY9HvXBczLyEp8P5erPvfKYgL5Cn8x0_k");
            User user = User.getUser(testData);
            listener.onLogin(user);
        }catch (JSONException e){
            Toast.makeText(mApplication, "JSON exception"+ e.toString(),Toast.LENGTH_LONG).show();
        }
    }

}

//need add data to send data
