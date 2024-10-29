package com.tbi.chatapplication.dummyui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tbi.chatapplication.R;
import com.tbi.chatapplication.databinding.ActivityWeekdayBinding;
import com.tbi.chatapplication.dummyui.adapter.WeekAdapter;
import com.tbi.chatapplication.dummyui.mode.WeekModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WeekdayActivity extends AppCompatActivity {
    ActivityWeekdayBinding binding;
    WeekAdapter adapter;
    List<WeekModel> models = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityWeekdayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initRv();
        generateWeekData();

    }
    private void initRv() {
        adapter = new WeekAdapter(models);
        LinearLayoutManager buttonLayoutManager = new LinearLayoutManager(WeekdayActivity.this,
                LinearLayoutManager.VERTICAL, false);
        binding.rvWeek.setLayoutManager(buttonLayoutManager);
        binding.rvWeek.setAdapter(adapter);
    }
    @SuppressLint("NotifyDataSetChanged")
    private void generateWeekData() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            int id=i;
            String date = dateFormat.format(calendar.getTime());
            String day = dayFormat.format(calendar.getTime());
            boolean selected = true;
            models.add(new WeekModel(id,date, day,selected));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        adapter.notifyDataSetChanged();
    }
}