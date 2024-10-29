package com.tbi.chatapplication.ui.search;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.tbi.chatapplication.databinding.ActivitySearchUserBinding;
import com.tbi.chatapplication.ui.model.UserModel;
import com.tbi.chatapplication.ui.search.adapter.SearchUserAdapter;
import com.tbi.chatapplication.utils.FirebaseUtil;
import com.tbi.chatapplication.utils.Loader;

import java.util.ArrayList;
import java.util.List;

public class SearchUserActivity extends AppCompatActivity {
    ActivitySearchUserBinding binding;
    SearchUserAdapter adapter;
    FirestoreRecyclerOptions<UserModel> options;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.eTSearchUsername.requestFocus();
        onClick();
        initRv();

    }

    private void initRv() {
        setupSearchRecyclerView();
    }

    private void onClick() {
        binding.back.setOnClickListener(v -> onBackPressed());

        binding.btnSearch.setOnClickListener(v -> {

            String searchTerm = binding.eTSearchUsername.getText().toString().trim();
            if (!searchTerm.isEmpty() && searchTerm.length() >= 2) {
               /* Query query = FirebaseUtil.allUserCollectionReference()
                        .orderBy("username")
                        .startAt(searchTerm)
                        .endAt(searchTerm + "\uf8ff");

                options = new FirestoreRecyclerOptions.Builder<UserModel>()
                        .setQuery(query, UserModel.class).build();

                if (adapter != null) {
                    adapter.updateOptions(options);
                } else {
                    adapter = new SearchUserAdapter(options, getApplicationContext());
                    binding.rvSearch.setAdapter(adapter);
                }*/

                adapter.getFilter().filter(searchTerm);
            } else {

                binding.eTSearchUsername.setError("Invalid Username");
            }
        });
    }

    private void setupSearchRecyclerView() {
        /*Query query = FirebaseUtil.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("username", searchTerm)
                .whereLessThanOrEqualTo("username", searchTerm + '\uf8ff');*/
        Query query = FirebaseUtil.allUserCollectionReference();

        options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class).build();
        adapter = new SearchUserAdapter(options, getApplicationContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchUserActivity.this, LinearLayoutManager.VERTICAL, false);
        binding.rvSearch.setLayoutManager(linearLayoutManager);
        binding.rvSearch.setAdapter(adapter);
       // adapter.startListening();

        FirebaseUtil.allUserCollectionReference().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<UserModel> userList = task.getResult().toObjects(UserModel.class);
                adapter.setOriginalList(userList);
            } else {
                // Handle error
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.startListening();
    }

}