package com.tbi.chatapplication.ui.chat;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.tbi.chatapplication.databinding.FragmentChatBinding;
import com.tbi.chatapplication.ui.chat.adapter.ChatListAdapter;
import com.tbi.chatapplication.ui.chat.model.ChatRoomModel;
import com.tbi.chatapplication.utils.FirebaseUtil;


public class ChatFragment extends Fragment {
    FragmentChatBinding binding;

    ChatListAdapter adapter;
    FirestoreRecyclerOptions<ChatRoomModel> options;

    public ChatFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initFB();
        initRv();
       // Loader.getInstance().showLoader(getContext());
        onClick();
        return view;
    }

    private void initFB() {
    }

    private void initRv() {
        Query query = FirebaseUtil.allChatroomCollectionReference()
                .whereArrayContains("userIds",FirebaseUtil.currentUserId())
                .orderBy("lastMessageTimestamp",Query.Direction.DESCENDING);

        options = new FirestoreRecyclerOptions.Builder<ChatRoomModel>()
                .setQuery(query,ChatRoomModel.class).build();

        adapter = new ChatListAdapter(options,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.rvChat.setLayoutManager(layoutManager);
        binding.rvChat.setAdapter(adapter);
        /*adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
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

    private void onClick() {
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }
}