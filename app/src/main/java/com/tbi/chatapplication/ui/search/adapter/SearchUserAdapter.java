package com.tbi.chatapplication.ui.search.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.tbi.chatapplication.databinding.RvSearchBinding;
import com.tbi.chatapplication.ui.chat.activity.ChatActivity;
import com.tbi.chatapplication.ui.model.UserModel;
import com.tbi.chatapplication.utils.AndroidUtil;
import com.tbi.chatapplication.utils.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchUserAdapter extends FirestoreRecyclerAdapter<UserModel, SearchUserAdapter.UserModelViewHolder> implements Filterable {

    Context context;
    OnClickListener clickListener;
    List<UserModel> originalList;
    List<UserModel> filteredList;

    public SearchUserAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
        this.originalList = new ArrayList<>();
        this.filteredList = new ArrayList<>();
    }
    public void setOriginalList(List<UserModel> originalList) {
        if (originalList != null) {
            this.originalList.clear();
            this.originalList.addAll(originalList);
            notifyDataSetChanged();
        }else{
            Toast.makeText(context, "List is Empty", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull SearchUserAdapter.UserModelViewHolder holder, int position, @NonNull UserModel model) {
        UserModel data=filteredList.get(position);
        holder.binding.tvUseName.setText(data.getUsername());
        //holder.binding.tvPhNo.setText(model.getPhone());
        Log.d("SearchUserAdapter", "Binding user: " + data.getUsername());
        if(data.getUserId().equals(FirebaseUtil.currentUserId())){
            holder.binding.tvUseName.setText(data.getUsername()+" (Me)");
        }

        FirebaseUtil.getOtherProfilePicStorageRef(data.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri  = t.getResult();
                        AndroidUtil.setProfilePic(context,uri,holder.binding.profileImg);
                    }
                });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtil.passUserModelAsIntent(intent,data);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            System.out.println("Moved to chat Activity");
        });
    }

    @NonNull
    @Override
    public SearchUserAdapter.UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RvSearchBinding binding=RvSearchBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new UserModelViewHolder(binding);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchTerm = constraint.toString().toLowerCase().trim();
                FilterResults results = new FilterResults();
                if (searchTerm.isEmpty()) {
                    filteredList = new ArrayList<>(originalList);
                } else {
                    List<UserModel> filtered = new ArrayList<>();
                    for (UserModel user : originalList) {
                        if (user.getUsername().toLowerCase().contains(searchTerm)) {
                            filtered.add(user);
                        }
                    }
                    filteredList = filtered;
                }
                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<UserModel>) results.values;
                notifyDataSetChanged(); // Notify adapter of dataset change
            }
        };
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public class UserModelViewHolder extends RecyclerView.ViewHolder {
        RvSearchBinding binding;
        public UserModelViewHolder(RvSearchBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
    public interface OnClickListener {
        void OnIntent(UserModel model);

    }

    public void setOnClickListener(OnClickListener clickListener) {
        this.clickListener = clickListener;
    }
}

