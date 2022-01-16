package com.example.industry4medical;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.industry4medical.databinding.ActivityLogiInBinding;
import com.example.industry4medical.model.API.AbstractAPIListener;
import com.example.industry4medical.model.Model;
import com.example.industry4medical.model.User;

public class LogInActivity extends Activity {

    private ActivityLogiInBinding binding;
    private EditText emailTxt, passTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLogiInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configViewComponents();
    }
    private void configViewComponents(){
        emailTxt = (EditText) findViewById(R.id.emailTextInput);
        passTxt = (EditText) findViewById(R.id.passwordTextInput);
        Button logInBtn = (Button) findViewById(R.id.loginBtn);
        logInBtn.setOnClickListener(v -> logInBtnAction());
        /*Button backToMainBtn = (Button) findViewById(R.id.backToMainBtn);
        backToMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/
    }
    private void logInBtnAction(){
        String email = emailTxt.getText().toString();
        String password = passTxt.getText().toString();

        final Model model = Model.getInstance(LogInActivity.this.getApplication());
        model.login(email, password, new AbstractAPIListener() {
            @Override
            public void onLogin(User user) {
                if (user != null) {
                    model.setUser(user);
                    startActivity(new Intent(LogInActivity.this, MenuActivity.class));
                }
            }
        });
    }
}