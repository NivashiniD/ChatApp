package com.tbi.chatapplication.ui.chat.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.tbi.chatapplication.databinding.RvChatMsgBinding;

import com.tbi.chatapplication.ui.chat.model.ChatMessageModel;
import com.tbi.chatapplication.utils.FirebaseUtil;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class ChatMessageAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatMessageAdapter.ChatModelViewHolder> {
    Context context;
    OnClickListener clickListener;


    public ChatMessageAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options,Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatMessageAdapter.ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        Log.i("haushd","asjd");
        if(model.getSenderId().equals(FirebaseUtil.currentUserId())){
            holder.binding.leftChatLayout.setVisibility(View.GONE);
            holder.binding.rightChatLayout.setVisibility(View.VISIBLE);
            holder.binding.tvRightChat.setText(model.getMessage());
            String timeText = getTimeText(model.getTimestamp());
            holder.binding.tvRightChatTime.setText(timeText);

        }else{
            holder.binding.rightChatLayout.setVisibility(View.GONE);
            holder.binding.leftChatLayout.setVisibility(View.VISIBLE);
            holder.binding.tvLeftChat.setText(model.getMessage());
            String timeText = getTimeText(model.getTimestamp());
            holder.binding.tvLeftChatTime.setText(timeText);
        }
    }
    private String getTimeText(Timestamp timestamp) {
        if (timestamp != null) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return dateFormat.format(timestamp.toDate());
        } else {
            return "";
        }
    }

    @NonNull
    @Override
    public ChatMessageAdapter.ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RvChatMsgBinding binding=RvChatMsgBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ChatModelViewHolder(binding);
    }

    public class ChatModelViewHolder extends RecyclerView.ViewHolder {
        RvChatMsgBinding binding;
        public ChatModelViewHolder(RvChatMsgBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
    public interface OnClickListener {
        void OnIntent(ChatMessageModel model);

    }

    public void setOnClickListener(OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

}
