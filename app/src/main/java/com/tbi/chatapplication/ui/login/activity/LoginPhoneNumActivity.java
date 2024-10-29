package com.tbi.chatapplication.ui.login.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tbi.chatapplication.R;
import com.tbi.chatapplication.databinding.ActivityLoginPhoneNumBinding;

public class LoginPhoneNumActivity extends AppCompatActivity {
    ActivityLoginPhoneNumBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginPhoneNumBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.progressBar.setVisibility(View.GONE);

        binding.countryCodePicker.registerCarrierNumberEditText(binding.phoneInput);
        onClick();
    }

    private void onClick() {
        binding.sendOtpBtn.setOnClickListener(v -> {
            if(!binding.countryCodePicker.isValidFullNumber()){
                binding.phoneInput.setError("Phone number not valid");

            }else {
                Intent intent = new Intent(LoginPhoneNumActivity.this, OtpActivity.class);
                intent.putExtra("phone", binding.countryCodePicker.getFullNumberWithPlus());
                startActivity(intent);
            }
        });
    }
}