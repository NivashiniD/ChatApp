package com.tbi.chatapplication.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tbi.chatapplication.R;
import com.tbi.chatapplication.ui.dashboard.DashboardActivity;
import com.tbi.chatapplication.ui.login.activity.LoginPhoneNumActivity;
import com.tbi.chatapplication.ui.model.UserModel;
import com.tbi.chatapplication.utils.AndroidUtil;
import com.tbi.chatapplication.utils.FirebaseUtil;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Animation bounce_anim = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bounce);

        TextView tv = findViewById(R.id.tv);
        tv.startAnimation(bounce_anim);

        if (getIntent().getExtras() != null) {
            // Handle intent from notification
            handleNotificationIntent();
        } else {
            // Regular flow without notification
            startRegularFlow();
        }
    }

    private void handleNotificationIntent() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("userId")) {
            String userId = extras.getString("userId");
            if (userId != null) {
                FirebaseUtil.allUserCollectionReference().document(userId).get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                UserModel model = task.getResult().toObject(UserModel.class);
                                if (model != null) {
                                    Intent intent = new Intent(this, DashboardActivity.class);
                                    AndroidUtil.passUserModelAsIntent(intent, model);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    startRegularFlow();
                                }
                            } else {
                                startRegularFlow();
                            }
                        });
            } else {
                startRegularFlow();
            }
        } else {
            startRegularFlow();
        }
    }

    private void startRegularFlow() {
        new Handler().postDelayed(() -> {
            if (FirebaseUtil.isLoggedIn()) {
                startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, LoginPhoneNumActivity.class));
            }
            finish();
        }, 1000);
    }
}