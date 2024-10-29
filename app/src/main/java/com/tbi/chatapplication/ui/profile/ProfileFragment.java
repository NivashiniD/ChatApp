package com.tbi.chatapplication.ui.profile;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tbi.chatapplication.R;
import com.tbi.chatapplication.databinding.DialogDoneBinding;
import com.tbi.chatapplication.databinding.FragmentChatBinding;
import com.tbi.chatapplication.databinding.FragmentProfileBinding;
import com.tbi.chatapplication.splash.SplashActivity;
import com.tbi.chatapplication.ui.model.UserModel;
import com.tbi.chatapplication.utils.AndroidUtil;
import com.tbi.chatapplication.utils.FirebaseUtil;

import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.annotations.Nullable;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    ImageView profilePic;
    UserModel currentUserModel;
    private StorageReference storageReference;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;

    DialogDoneBinding doneBinding;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data!=null && data.getData()!=null){
                            selectedImageUri = data.getData();
                            AndroidUtil.setProfilePic(getContext(),selectedImageUri,profilePic);
                        }
                    }
                }
        );
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        imgPicLauncher();
        storageReference = FirebaseStorage.getInstance().getReference();
        getUserData();
        onClick();
        return view;
    }

    private void onClick() {
        binding.profUpdateBtn.setOnClickListener((v -> {
            updateBtnClick();
        }));

        binding.logoutBtn.setOnClickListener((v)->{
            SHOW_LOGOUT_DIALOG();
        });

        binding.profileImageView.setOnClickListener((v)->{
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });

        binding.shareBtn.setOnClickListener(v->{

            shareApp();
        });
    }
    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareBody = "Check out this amazing app!:https://drive.google.com/file/d/1jeYTb-ZZbKmhzpclYgt3uFutGoS3oQf8/view?usp=drive_link";
        String shareSubject = "My Awesome App";

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);

        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
    private void updateBtnClick() {
        String newUsername = binding.profUsername.getText().toString();
        if(newUsername.isEmpty() || newUsername.length()<3){
            binding.profUsername.setError("Username length should be at least 3 chars");
            return;
        }
        currentUserModel.setUsername(newUsername);
        setInProgress(true);


        if(selectedImageUri!=null){
            FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                    .addOnCompleteListener(task -> {
                        updateToFireStore();
                    });
        }else{
            updateToFireStore();
        }

    }

    private void updateToFireStore() {
        FirebaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if(task.isSuccessful()){
                        AndroidUtil.showToast(getContext(),"Updated successfully");
                       // showDoneDialog();

                    }else{
                        AndroidUtil.showToast(getContext(),"Updated failed");
                    }
                });
    }

    private void showDoneDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View rootView = inflater.inflate(R.layout.dialog_done, null);
        doneBinding = DialogDoneBinding.bind(rootView); // Initialize doneBinding here

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setView(rootView);

        AlertDialog dialog = alertDialog.create();
        dialog.show();

        LottieAnimationView lottieAnimationView = doneBinding.lottieAnimationView;
        lottieAnimationView.setAnimation("thumbs.json");
        lottieAnimationView.playAnimation();
    }

    private void getUserData() {
        setInProgress(true);

        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Uri uri  = task.getResult();
                        AndroidUtil.setProfilePic(getContext(),uri,binding.profileImageView);
                    }
                });

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            currentUserModel = task.getResult().toObject(UserModel.class);
            binding.profUsername.setText(currentUserModel.getUsername());
            binding.profPhone.setText(currentUserModel.getPhone());
        });
    }

    private void setInProgress(boolean inProgress) {
        if(inProgress){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.profUpdateBtn.setVisibility(View.GONE);
        }else{
            binding.progressBar.setVisibility(View.GONE);
            binding.profUpdateBtn.setVisibility(View.VISIBLE);
        }
    }

    private void imgPicLauncher() {
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            if (getContext() != null && binding != null && binding.profileImageView != null) {
                                try {
                                    AndroidUtil.setProfilePic(getContext(), selectedImageUri, binding.profileImageView);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    // Handle error loading image
                                }
                            }
                        }
                    }
                }
        );
    }

    private void SHOW_LOGOUT_DIALOG() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Logout");
        alertDialogBuilder
                .setMessage("Do you want to logout from this App ?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        (dialog, id) -> {
                            dialog.dismiss();
                            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        FirebaseUtil.logout();
                                        Intent intent = new Intent(getContext(), SplashActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });

                        })
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.app_color));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.app_color));
    }

}