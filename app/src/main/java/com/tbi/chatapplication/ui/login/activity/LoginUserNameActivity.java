package com.tbi.chatapplication.ui.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.tbi.chatapplication.databinding.ActivityLoginUserNameBinding;
import com.tbi.chatapplication.ui.dashboard.DashboardActivity;
import com.tbi.chatapplication.ui.model.UserModel;
import com.tbi.chatapplication.utils.FirebaseUtil;
import java.util.Objects;

public class LoginUserNameActivity extends AppCompatActivity {
    ActivityLoginUserNameBinding binding;
    String phoneNumber;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginUserNameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

       /* Bundle extras = getIntent().getExtras();

        if (extras != null && extras.containsKey("phone")) {
            phoneNumber = extras.getString("phone");
            getUsername();
        } else {

             Toast.makeText(this, "Phone number not provided", Toast.LENGTH_SHORT).show();
             finish();
        }*/

        if (getIntent() != null && getIntent().hasExtra("phone")) {
             phoneNumber =getIntent().getExtras().getString("phone");
             getUsername();
        } else {

            Toast.makeText(this, "Phone number not provided UserNameActivity", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Proceed with other initialization

        onClick();
    }

    private void onClick() {
        binding.letMeInBtn.setOnClickListener((v -> setUsername()));
    }

    private void setUsername() {
        if (binding.loginUsername != null) {

            String username = binding.loginUsername.getText().toString();
            if (username.isEmpty() || username.length() < 3) {
                binding.loginUsername.setError("Username length should be at least 3 chars");
                return;
            }
            setInProgress(true);
            if (userModel != null) {
                userModel.setUsername(username);

            } else {
                userModel = new UserModel(phoneNumber, username, Timestamp.now(), FirebaseUtil.currentUserId());
            }

            FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(task -> {
                setInProgress(false);
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginUserNameActivity.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginUserNameActivity.this, "Error setting username: " + task.getException(), Toast.LENGTH_SHORT).show();
                    Log.e("LoginUserNameActivity", "Error setting username", task.getException());
                }
            });
        }else {
            Toast.makeText(this, "UserName not provided", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void setInProgress(boolean inProgress) {

            if(inProgress){
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.letMeInBtn.setVisibility(View.GONE);
            }else{
                binding.progressBar.setVisibility(View.GONE);
                binding.letMeInBtn.setVisibility(View.VISIBLE);
            }

    }

    private void getUsername() {
        setInProgress(true);
        DocumentReference currentUserRef = FirebaseUtil.currentUserDetails();
        if (currentUserRef != null) {
            currentUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            userModel = document.toObject(UserModel.class);
                            if (userModel != null) {
                                binding.loginUsername.setText(userModel.getUsername());
                                Log.d("FirestoreTestActivity", "User document exists: " + userModel.toString());
                            }
                        } else {
                            Log.d("FirestoreTestActivity", "User document does not exist");
                            Toast.makeText(LoginUserNameActivity.this, "User document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("FirestoreTestActivity", "Error getting user document: " );
                        Toast.makeText(LoginUserNameActivity.this, "Error getting user document: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {

            Log.d("FirestoreTestActivity", "User document reference is null");
            Toast.makeText(LoginUserNameActivity.this, "User document reference is null", Toast.LENGTH_SHORT).show();
        }
    }

}