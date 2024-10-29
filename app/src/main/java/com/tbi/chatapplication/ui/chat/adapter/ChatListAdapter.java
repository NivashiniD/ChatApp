package com.tbi.chatapplication.ui.chat.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.tbi.chatapplication.databinding.RvChatBinding;
import com.tbi.chatapplication.ui.chat.activity.ChatActivity;
import com.tbi.chatapplication.ui.chat.model.ChatRoomModel;
import com.tbi.chatapplication.ui.model.UserModel;
import com.tbi.chatapplication.utils.AndroidUtil;
import com.tbi.chatapplication.utils.FirebaseUtil;
import com.tbi.chatapplication.utils.Loader;

import java.util.Objects;

public class ChatListAdapter extends FirestoreRecyclerAdapter<ChatRoomModel, ChatListAdapter.ChatroomModelViewHolder> {

    Context context;
    OnClickListener clickListener;


    public ChatListAdapter(@NonNull FirestoreRecyclerOptions<ChatRoomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onBindViewHolder(@NonNull ChatListAdapter.ChatroomModelViewHolder holder, int position, @NonNull ChatRoomModel model) {
        Objects.requireNonNull(FirebaseUtil.getOtherUserFromChatroom(model.getUserIds()))
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());


                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);

                        assert otherUserModel != null;
                        FirebaseUtil.getOtherProfilePicStorageRef(otherUserModel.getUserId()).getDownloadUrl()
                                .addOnCompleteListener(t -> {
                                    if (t.isSuccessful()) {
                                        Uri uri = t.getResult();
                                        AndroidUtil.setProfilePic(context, uri, holder.binding.profileImg);
                                    }
                                });

                        holder.binding.tvUseName.setText(otherUserModel.getUsername());
                        if (lastMessageSentByMe)
                            holder.binding.tvMessage.setText("You : " + model.getLastMessage());
                        else
                            holder.binding.tvMessage.setText(model.getLastMessage());
                            holder.binding.tvLastSeen.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

                        holder.itemView.setOnClickListener(v -> {
                            //navigate to chat activity
                            Intent intent = new Intent(context, ChatActivity.class);
                            AndroidUtil.passUserModelAsIntent(intent, otherUserModel);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });
                        holder.binding.profileImg.setOnClickListener(v -> {
                            Toast.makeText(context, "Oops !!! You can't View their Profile", Toast.LENGTH_SHORT).show();
                        });
                    }
                    Loader.getInstance().hideLoader();
                });
    }

    @NonNull
    @Override
    public ChatListAdapter.ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RvChatBinding binding = RvChatBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ChatroomModelViewHolder(binding);
    }

    public class ChatroomModelViewHolder extends RecyclerView.ViewHolder {
        RvChatBinding binding;

        public ChatroomModelViewHolder(RvChatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnClickListener {
        void OnIntent(ChatRoomModel model);

    }

    public void setOnClickListener(OnClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
