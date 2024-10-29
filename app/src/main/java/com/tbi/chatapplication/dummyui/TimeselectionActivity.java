package com.tbi.chatapplication.dummyui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.tbi.chatapplication.R;
import com.tbi.chatapplication.databinding.ActivityTimeselectionBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeselectionActivity extends AppCompatActivity {
    ActivityTimeselectionBinding binding;
    MaterialTimePicker timePicker;
    MaterialDatePicker datePicker;
    Calendar calendar;
    private Calendar selectedDateCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityTimeselectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        onClick();
    }

    private void onClick() {
        binding.tvTime.setOnClickListener(v -> {
            Calendar currentTime = Calendar.getInstance();
            int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
            int currentMinute = currentTime.get(Calendar.MINUTE);

            timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(currentHour)
                    .setMinute(currentMinute)
                    .setTitleText("Select Alarm Time")
                    .build();
            timePicker.show(getSupportFragmentManager(), "Notify");

            timePicker.addOnPositiveButtonClickListener(view -> {
                if (timePicker.getHour() > 12) {
                    binding.tvTime.setText(
                            String.format("%02d", (timePicker.getHour() - 12)) + ":" + String.format("%02d", timePicker.getMinute()) + "PM"
                    );
                } else {
                    binding.tvTime.setText(timePicker.getHour() + ":" + timePicker.getMinute() + "AM");
                }
                calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendar.set(Calendar.MINUTE, timePicker.getMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
            });

        });
        binding.tvDate.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setTitleText("Select Date");
            datePicker = builder.build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                long selectedMillis = (long) selection;
                selectedDateCalendar = Calendar.getInstance();
                selectedDateCalendar.setTimeInMillis(selectedMillis);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String selectedDate = dateFormat.format(selectedDateCalendar.getTime());

                binding.tvDate.setText(selectedDate);
            });

            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        });
        binding.btnSetUp.setOnClickListener(v -> {

            if (selectedDateCalendar != null) {
                Calendar nextDateTime = (Calendar) selectedDateCalendar.clone();
                nextDateTime.add(Calendar.DAY_OF_MONTH, 6);
                nextDateTime.set(Calendar.HOUR_OF_DAY, 18);
                nextDateTime.set(Calendar.MINUTE, 0);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                String formattedDateTime = dateFormat.format(nextDateTime.getTime());

                binding.tvDateTime.setVisibility(View.VISIBLE);
                binding.tvDateTime.setText(formattedDateTime);
            } else {
                Toast.makeText(TimeselectionActivity.this, "Please select a date first", Toast.LENGTH_SHORT).show();
                System.out.println("##### date not selected");
            }
        });
    }

}