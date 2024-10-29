package com.tbi.chatapplication.utils;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

public class FirebaseUtil {

    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static boolean isLoggedIn(){
        return currentUserId() != null;
    }

    public static DocumentReference currentUserDetails(){
        String userId = currentUserId();
        if (userId != null) {
            return FirebaseFirestore.getInstance().collection("users").document(userId);
        } else {
            return null;
        }
    }

    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static DocumentReference getChatroomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static String getChatroomId(String userId1,String userId2){
        if(userId1.hashCode() < userId2.hashCode()){
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    public static CollectionReference allChatroomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static DocumentReference getOtherUserFromChatroom(List<String> userIds){
        if (userIds.size() < 2) return null;

        String currentUserId = currentUserId();
        if (currentUserId == null) return null;

        String otherUserId = userIds.get(0).equals(currentUserId) ? userIds.get(1) : userIds.get(0);
        return allUserCollectionReference().document(otherUserId);
    }

    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    public static StorageReference  getCurrentProfilePicStorageRef(){
        String currentUserId = currentUserId();
        if (currentUserId != null) {
            return FirebaseStorage.getInstance().getReference().child("profile_pic").child(currentUserId);
        } else {
            return null;
        }
    }

    public static StorageReference  getOtherProfilePicStorageRef(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("profile_pic").child(otherUserId);
    }


    public static void sendFCMNotification(String userId, String title, String body) {
        FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(userId + "@fcm.googleapis.com")
                .setMessageId(Integer.toString(0))
                .addData("title", title)
                .addData("body", body)
                .build());
    }

    public static void sendChatNotification(String chatroomId, String message) {

        String[] userIds = chatroomId.split("_");
        if (userIds.length == 2) {
            String otherUserId = (userIds[0].equals(currentUserId())) ? userIds[1] : userIds[0];
            String title = "New Message";
            String body = message;
            sendFCMNotification(otherUserId, title, body);
        }
    }

    public static void sendNotificationToUser(String userId, String title, String body) {
        sendFCMNotification(userId, title, body);
    }
}










