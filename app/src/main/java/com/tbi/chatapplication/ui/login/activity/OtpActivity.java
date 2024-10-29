package com.tbi.chatapplication.ui.login.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.tbi.chatapplication.databinding.ActivityOtpBinding;
import com.tbi.chatapplication.utils.AndroidUtil;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {
    ActivityOtpBinding binding;
    String phoneNumber;
    String verificationCode;
    Long timeoutSeconds = 60L;
    PhoneAuthProvider.ForceResendingToken  resendingToken;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(getIntent() != null && getIntent().hasExtra("phone")){
            phoneNumber =getIntent().getExtras().getString("phone");
            sendOtp(phoneNumber,false);
        }else {
            Toast.makeText(this, "Phone number not provided by OTP Activity", Toast.LENGTH_SHORT).show();

        }


        onClick();
    }
    private void onClick() {
        binding.nextBtn.setOnClickListener(v->{
            String enteredOtp  = binding.otpInput.getText().toString();
            PhoneAuthCredential credential =  PhoneAuthProvider.getCredential(verificationCode,enteredOtp);
            signIn(credential);
        });

        binding.resendOtpTextView.setOnClickListener((v)-> sendOtp(phoneNumber,true));

    }
    private void sendOtp(String phoneNumber, boolean isResend) {
        startResendTimer();
        setInProgress(true);
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                               // Toast.makeText(OtpActivity.this,"OTP verification failed",Toast.LENGTH_SHORT).show();
                                AndroidUtil.showToast(getApplicationContext(),"OTP verification failed");
                                setInProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                resendingToken = forceResendingToken;
                                AndroidUtil.showToast(getApplicationContext(),"OTP sent successfully");
                                setInProgress(false);
                            }
                        });
        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }


    }

    private void signIn(PhoneAuthCredential phoneAuthCredential) {
        //login and go to next activity
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
            setInProgress(false);
            if(task.isSuccessful()){
                Intent intent = new Intent(OtpActivity.this,LoginUserNameActivity.class);
                intent.putExtra("phone",phoneNumber);
                startActivity(intent);
            }else{
                AndroidUtil.showToast(getApplicationContext(),"OTP verification failed");
            }
        });
    }

    private void startResendTimer() {
        binding.resendOtpTextView.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                timeoutSeconds--;
                binding.resendOtpTextView.setText("Resend OTP in "+timeoutSeconds +" seconds");
                if(timeoutSeconds<=0){
                    timeoutSeconds =60L;
                    timer.cancel();
                    runOnUiThread(() -> binding.resendOtpTextView.setEnabled(true));
                }
            }
        },0,1000);
    }


   private void setInProgress(boolean inProgress){
        if(inProgress){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.nextBtn.setVisibility(View.GONE);
        }else{
            binding.progressBar.setVisibility(View.GONE);
            binding.nextBtn.setVisibility(View.VISIBLE);
        }
    }

}