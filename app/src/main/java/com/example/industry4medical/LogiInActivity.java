package com.example.industry4medical;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.industry4medical.databinding.ActivityLogiInBinding;

public class LogiInActivity extends Activity {

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
        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInBtnAction();
            }
        });
        Button backToMainBtn = (Button) findViewById(R.id.backToMainBtn);
        backToMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void logInBtnAction(){
        String email = emailTxt.getText().toString();
        String password = passTxt.getText().toString();

        Toast.makeText(this, "Email: " + email + " Password: " + password,
                Toast.LENGTH_LONG).show();
    }
}