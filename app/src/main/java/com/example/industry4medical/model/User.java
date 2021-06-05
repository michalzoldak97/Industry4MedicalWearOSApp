package com.example.industry4medical.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public class User {
    public static User getUser(JSONObject jsonObject) throws JSONException{
        String email = jsonObject.getString("name");
        String webToken = jsonObject.getString("token");

        return new User(email, webToken);
    }

    private String email, webToken;

    public User(String email, String webToken){
        this.email = email;
        this.webToken = webToken;
    }

    public String getEmail(){ return email; }
    public void setEmail(String email){ this.email = email; }

    public String getWebToken(){ return webToken; }
    public void setWebToken(String webToken){ this.webToken = email; }

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        boolean result = false;
        if(obj instanceof User){
            User that = (User) obj;
            if(this.email.equalsIgnoreCase(that.email)){
                result = true;
            }
        }
        return result;
    }

    @Override
    public @NotNull String toString() {
        return this.email;
    }
}
