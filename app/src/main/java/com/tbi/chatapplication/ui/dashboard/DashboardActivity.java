package com.tbi.chatapplication.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tbi.chatapplication.R;
import com.tbi.chatapplication.databinding.ActivityDashboardBinding;
import com.tbi.chatapplication.databinding.ActivityOtpBinding;
import com.tbi.chatapplication.ui.chat.ChatFragment;
import com.tbi.chatapplication.ui.profile.ProfileFragment;
import com.tbi.chatapplication.ui.search.SearchUserActivity;
import com.tbi.chatapplication.utils.FirebaseUtil;

public class DashboardActivity extends AppCompatActivity {
    ActivityDashboardBinding binding;
    ChatFragment chatFragment;
    ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializeFragments();
        onClick();
    }

    private void initializeFragments() {
        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();
    }

    private void onClick() {
        binding.searchButton.setOnClickListener((v)->{
            startActivity(new Intent(DashboardActivity.this, SearchUserActivity.class));
        });
       binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.menu_chat){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,chatFragment).commit();
                }
                if(item.getItemId()==R.id.menu_profile){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,profileFragment).commit();
                }
                return true;
            }
        });
        binding.bottomNavigation.setSelectedItemId(R.id.menu_chat);

        getFCMToken();
    }

    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task->{
            if (task.isSuccessful()){
                String token=task.getResult();
                FirebaseUtil.currentUserDetails().update("fcmToken",token);
                Log.i( "getFCMToken: ",token);
            }
        });
    }


}