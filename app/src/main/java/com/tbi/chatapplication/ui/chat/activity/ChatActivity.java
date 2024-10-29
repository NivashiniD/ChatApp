package com.tbi.chatapplication.ui.chat.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import com.tbi.chatapplication.R;
import com.tbi.chatapplication.databinding.ActivityChatBinding;
import com.tbi.chatapplication.ui.chat.adapter.ChatMessageAdapter;
import com.tbi.chatapplication.ui.chat.model.ChatMessageModel;
import com.tbi.chatapplication.ui.chat.model.ChatRoomModel;
import com.tbi.chatapplication.ui.chat.model.MusicPlaybackState;
import com.tbi.chatapplication.ui.model.UserModel;
import com.tbi.chatapplication.utils.AndroidUtil;
import com.tbi.chatapplication.utils.FirebaseUtil;
import com.tbi.chatapplication.utils.Loader;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    UserModel otherUser;

    ChatMessageAdapter adapter;
    String chatroomId;
    ChatRoomModel chatroomModel;
    private static final int REQUEST_CODE_PICK_MUSIC = 1;
    private MediaPlayer mediaPlayer;

    private DatabaseReference musicPlaybackRef;
    private ValueEventListener musicPlaybackListener;
    private boolean isMusicPlaying = false;

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(),otherUser.getUserId());

        initFB();
        onClick();
        initRv();
        CreateChatroomModel();
        mediaPlayer = new MediaPlayer();

        musicPlaybackRef = FirebaseDatabase.getInstance().getReference("musicPlayback");
        setupMusicPlaybackListener();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_EXTERNAL_STORAGE);
        }

       /* FirebaseStorageUtil.uploadFile("path/to/your/local/file", "path/in/firebase/storage", new FirebaseStorageUtil.UploadCallback() {
            @Override
            public void onSuccess(String downloadUrl) {
                // Handle successful upload, e.g., save download URL to database
                Log.d("FirebaseStorage", "Upload successful. Download URL: " + downloadUrl);
            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure to upload
                Log.e("FirebaseStorage", "Error uploading file: " + e.getMessage());
            }
        });*/
    }

    private void initFB() {
        StorageReference storageRef = FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId());
        // Log the path of the object
        Log.d("Storage", "Attempting to download object from path: " + storageRef.getPath());
        storageRef.getDownloadUrl().addOnCompleteListener(t -> {
            if(t.isSuccessful()){
                Uri uri  = t.getResult();
                AndroidUtil.setProfilePic(this, uri, binding.profilePic);
                Log.d("FirebaseStorage", "Download URL: " +uri);
            } else {

                Exception exception = t.getException();
                if (exception != null) {
                    Log.e("FirebaseStorage", "Error getting download URL: " + exception.getMessage());
                    if (exception instanceof StorageException) {
                        StorageException storageException = (StorageException) exception;
                        int errorCode = storageException.getErrorCode();
                        String errorMessage = storageException.getMessage();
                        Log.e("FirebaseStorage", "StorageException ErrorCode: " + errorCode + ", Message: " + errorMessage);
                    } else {
                        Log.e("FirebaseStorage", "Unknown exception type: " + exception.getClass().getSimpleName());
                    }
                }
            }
        }).addOnFailureListener(exception -> {
            // Handle failure to get download URL
            Log.e("FirebaseStorage", "Error getting download URL: " + exception.getMessage());
        });
    }

    private void onClick() {
        binding.back.setOnClickListener((v)-> onBackPressed());
        binding.otherUserName.setText(otherUser.getUsername());

        binding.sendMessageBtn.setOnClickListener((v -> {
            String message =binding.messageInput.getText().toString().trim();
            if(message.isEmpty())
            {
                Toast.makeText(ChatActivity.this,"Empty Message Can't send",Toast.LENGTH_SHORT).show();
                return;
            }

            sendMessageToUser(message);
        }));

        binding.profilePic.setOnClickListener(v->{
            Toast.makeText(this, "You Can't View their Profile Picture", Toast.LENGTH_SHORT).show();
        });

        binding.music.setOnClickListener(v->{
            if (isMusicPlaying) {
                pauseMusic(); // Pause music when the ImageView is clicked
            } else {
                openMusicPicker(); // Open music picker to select a song
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode ==REQUEST_READ_EXTERNAL_STORAGE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (isMusicPlaying) {
                    pauseMusic();
                } else {
                    openMusicPicker();
                }
            } else {

                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onMusicSelected(Uri selectedMusicUri) {
        if (selectedMusicUri != null) {
            onMusicPlaybackStateChanged(true, selectedMusicUri);
            playMusic(selectedMusicUri);
            sendMusicPlaybackState(selectedMusicUri, true);
            sendMusicPlaybackNotification(selectedMusicUri);
        }
    }

    private void playMusic(Uri musicUri) {

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getApplicationContext(), musicUri);
            mediaPlayer.prepare();

            mediaPlayer.setOnCompletionListener(mp -> {

                onMusicStateChanged(false);
            });
            mediaPlayer.start();
            onMusicStateChanged(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onMusicStateChanged(boolean isPlaying) {
        isMusicPlaying = isPlaying;
        updateMusicStateColor();
        Log.d("MusicState", "isMusicPlaying: " + isMusicPlaying);
        if (!isPlaying ) {
            Toast.makeText(ChatActivity.this, "Other user is playing music", Toast.LENGTH_SHORT).show();
            binding.music.setColorFilter(ContextCompat.getColor(this, R.color.color_5));
        }
    }

    private void updateMusicStateColor() {
        if (isMusicPlaying) {
            binding.music.setColorFilter(ContextCompat.getColor(this, R.color.green_600));
        } else {
            binding.music.setColorFilter(ContextCompat.getColor(this, R.color.red_800));
        }
    }

    private void openMusicPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");  // Filter for audio files
        startActivityForResult(intent, REQUEST_CODE_PICK_MUSIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_MUSIC && resultCode == RESULT_OK && data != null) {
            Uri selectedMusicUri = data.getData();
            if (selectedMusicUri != null) {
                onMusicSelected(selectedMusicUri);
            }
        }
    }

    private void initRv() {
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query,ChatMessageModel.class).build();

        adapter = new ChatMessageAdapter( options,ChatActivity.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false);
        binding.rvChatMsg.setLayoutManager(layoutManager);
        binding.rvChatMsg.setAdapter(adapter);
      /*  adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                if (adapter.getItemCount() > 0) {
                    Loader.getInstance().hideLoader();
                }
            }
        });*/
        adapter.startListening();
        adapter.setOnClickListener(data -> {
            System.out.println("######");

        });
    }

    private void CreateChatroomModel() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    chatroomModel = document.toObject(ChatRoomModel.class);
                    if (chatroomModel == null) {

                        chatroomModel = new ChatRoomModel(chatroomId,
                                Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUserId()),
                                Timestamp.now(),
                                ""
                        );
                        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                    }
                } else {
                    chatroomModel = new ChatRoomModel(chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

                }
            } else {

                Log.e("CreateChatroomModel", "Error getting document", task.getException());
            }
        });
    }

    private void sendMessageToUser(String message) {
        if (chatroomModel != null) {
            chatroomModel.setLastMessageTimestamp(Timestamp.now());
            chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
            chatroomModel.setLastMessage(message);

           // FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
            FirebaseUtil.getChatroomReference(chatroomId).update("lastMessage", message)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            binding.messageInput.setText("");
                            sendNotification(message);
                        } else {

                            Log.e("sendMessageToUser", "Error updating last message", task.getException());
                        }
                    });

/*
            chatroomModel = new ChatRoomModel(chatroomId,
                    Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUserId()),
                    Timestamp.now(),
                    "");*/

            ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now());
            FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            binding.messageInput.setText("");
                            sendNotification(message);
                        }else {
                            // Handle the case where creating the chat room fails
                            Log.e("sendMessageToUser", "Error creating chat room", task.getException());
                        }
                    });

        }else {
            Log.e("sendMessageToUser", "Chat room model is null");
        }
    }

    public void sendNotification(String message){

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                UserModel currentUser = task.getResult().toObject(UserModel.class);
                try{
                    JSONObject jsonObject  = new JSONObject();

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title",currentUser.getUsername());
                    notificationObj.put("body",message);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("userId",currentUser.getUserId());

                    jsonObject.put("notification",notificationObj);
                    jsonObject.put("data",dataObj);
                    jsonObject.put("to",otherUser.getFcmToken());

                    callApi(jsonObject);
                    Log.i("Notification", "Ready to SEnd");


                }catch (Exception e){
                    Log.e("Notification", "Error constructing JSON", e);
                }
            }else {
                Log.e("Notification", "Failed to retrieve user details", task.getException());
            }
        });

    }

    public void callApi(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization","Bearer AAAANdjjLGw:APA91bFT4kXQiIsbLFy8kwp47jbqPBuASB3KDNpRO3bJMsUJI5K_hUdbuYIfPZ-buW1h0MRypAcAh1Gn_wUhUOuhg9khw1QiVc_aayq-q6NdF8hRfuhUBjUWckryh3ephXrMqNJlvwNm")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("Notification", "Failed to send notification: " + e.getMessage());
                // Handle failure, e.g., show a toast or log an error message
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i("Notification", "Notification sent successfully");
                    // Handle success, e.g., show a toast or log a success message
                } else {
                    Log.e("Notification", "Failed to send notification: " + response.message());
                    // Handle failure, e.g., show a toast or log an error message
                }
            }
        });

    }

    private void pauseMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            onMusicPlaybackStateChanged(false, null);
            onMusicStateChanged(false);
            sendMusicPlaybackState(null, false);
        }
    }

    private void sendMusicPlaybackState(Uri musicUri, boolean isPlaying) {
        MusicPlaybackState musicState = new MusicPlaybackState(musicUri.toString(), isPlaying);
        musicPlaybackRef.setValue(musicState);

        musicPlaybackRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    MusicPlaybackState updatedMusicState = dataSnapshot.getValue(MusicPlaybackState.class);
                    Log.d("MusicState", "isPlaying: " + musicState.isPlaying());
                    if (updatedMusicState != null && updatedMusicState.isPlaying()) {
                        Uri musicUri = Uri.parse(musicState.getMusicUri());
                        playMusic(musicUri);
                        onMusicStateChanged(true);
                    } else {
                        // Music playback stopped, update UI accordingly
                        onMusicStateChanged(false);
                        pauseMusic();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChatActivity", "Music playback listener canceled: " + databaseError.getMessage());
            }
        });
        musicPlaybackRef.addValueEventListener(musicPlaybackListener);
    }

    private void setupMusicPlaybackListener() {
        musicPlaybackListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    MusicPlaybackState musicState = dataSnapshot.getValue(MusicPlaybackState.class);
                    if (musicState != null && musicState.isPlaying()) {
                        Uri musicUri = Uri.parse(musicState.getMusicUri());

                        playMusic(musicUri);

                        onMusicStateChanged(true);
                        Log.d("MusicState", "Other user is playing music");
                    } else {
                        // Music playback is stopped, pause the music or handle as needed
                        onMusicStateChanged(false);
                        pauseMusic();
                        Log.d("MusicState", "Other user is not playing music");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChatActivity", "Music playback listener canceled: " + databaseError.getMessage());
            }
        };

        musicPlaybackRef.addValueEventListener(musicPlaybackListener);
    }

    private void onMusicPlaybackStateChanged(boolean isPlaying, Uri musicUri) {
        if (isPlaying) {
            playMusic(musicUri);
        } else {
            pauseMusic();
        }
    }

    private void sendMusicPlaybackNotification(Uri musicUri) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", "Music Playback Started");
            if (FirebaseUtil.currentUserId().equals(otherUser.getUserId())) {
                notificationObj.put("body", "You started playing music with " + otherUser.getUsername());
            } else {
                notificationObj.put("body", otherUser.getUsername() + "!! User starts playing music with you");
            }

            JSONObject dataObj = new JSONObject();
            dataObj.put("userId", FirebaseUtil.currentUserId());
            dataObj.put("action", "PLAY_MUSIC");
            dataObj.put("musicUri", musicUri.toString());

            jsonObject.put("notification", notificationObj);
            jsonObject.put("data", dataObj);
            jsonObject.put("to", otherUser.getFcmToken());

            String jsonString = jsonObject.toString();
            Log.d("Notification", "JSON Payload: " + jsonString);
            callApi(jsonObject);
        } catch (Exception e) {
            Log.e("Notification", "Error constructing JSON", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release media player resources
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Remove the music playback listener
        if (musicPlaybackRef != null && musicPlaybackListener != null) {
            musicPlaybackRef.removeEventListener(musicPlaybackListener);
        }
    }
}