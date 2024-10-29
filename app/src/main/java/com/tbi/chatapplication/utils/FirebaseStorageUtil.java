package com.tbi.chatapplication.utils;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class FirebaseStorageUtil {
    public interface UploadCallback {
        void onSuccess(String downloadUrl);

        void onFailure(Exception e);
    }

    public static void uploadFile(String filePath, String storagePath, UploadCallback callback) {
        Uri fileUri = Uri.fromFile(new File(filePath));
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(storagePath);

        UploadTask uploadTask = storageRef.putFile(fileUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // File uploaded successfully, get download URL
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                callback.onSuccess(downloadUrl);
            }).addOnFailureListener(callback::onFailure);
        }).addOnFailureListener(callback::onFailure);
    }
}
