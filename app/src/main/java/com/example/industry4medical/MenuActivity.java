package com.example.industry4medical;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.industry4medical.databinding.ActivityMenuBinding;

public class MenuActivity extends Activity {

    private ActivityMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configViewComponents();
    }

    private void configViewComponents(){
        Button startInBtn = (Button) findViewById(R.id.menuStartButton);
        startInBtn.setOnClickListener(v -> startBtnAction());
    }

    private void startBtnAction(){
        startActivity(new Intent(MenuActivity.this, GetSleepDataActivity.class));
    }
}