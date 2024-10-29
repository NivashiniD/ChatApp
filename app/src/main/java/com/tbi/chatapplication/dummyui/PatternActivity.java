package com.tbi.chatapplication.dummyui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tbi.chatapplication.R;
import com.tbi.chatapplication.databinding.ActivityPatterBinding;
import com.tbi.chatapplication.dummyui.mode.SpinnerModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PatternActivity extends AppCompatActivity {
    ActivityPatterBinding binding;
    List<String> spinIdList = new ArrayList<>();
    List<String> spinNameList = new ArrayList<>();
    String spinId = "", spinName = "";

    String clearFilters = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPatterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        onClick();
        spinner();
    }

    private void onClick() {

        binding.btnDraw.setOnClickListener(v -> {
            int numRows = Integer.parseInt(Objects.requireNonNull(binding.edtRow.getText()).toString());
            int numCols = Integer.parseInt(Objects.requireNonNull(binding.edtColumn.getText()).toString());
            //String shape = spinNameList.get(binding.spnShape.getSelectedItemPosition());
            int radius=5;
            String pattern = generateStarPattern( numRows, numCols, radius);
            binding.tvStar.setVisibility(View.VISIBLE);
            binding.tvStar.setText(pattern);
        });
    }
    private void spinner() {
        List<SpinnerModel> spinList = new ArrayList<>();
        spinList.add(new SpinnerModel("Rectangle", 0));
        spinList.add(new SpinnerModel("Square", 1));
        spinList.add(new SpinnerModel("Circle", 2));
        spinList.add(new SpinnerModel("Pyramid", 3));
        spinList.add(new SpinnerModel("Diamond", 4));

        spinList.forEach(data -> {
            spinNameList.add(data.getName());
            spinIdList.add(String.valueOf(data.getId()));

        });


        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(PatternActivity.this, R.layout.custome_spinner_item, spinNameList);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spnShape.setSelection(0);
        binding.spnShape.setAdapter(adapter1);

        binding.spnShape.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String item = spinNameList.get(position);
                spinId = spinIdList.get(position);
                System.out.println("### filtersId = " + spinId);
                clearFilters = "1";
                // Perform actions based on the selected item
                // doTaskList("", false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
    }

    private String generateStarPattern(int numRows, int numCols, int radius) {
        StringBuilder pattern = new StringBuilder();
        if (spinId .equals("1")  || spinId .equals("0"))  {
            for (int i = 0; i < numRows; i++) {
                for (int j = 0; j < numCols; j++) {
                    pattern.append(" *  ");
                }
                pattern.append("\n");
            }
        } else if (spinId.equals("2")) {
            int centerX = radius;
            int centerY = radius;

            // Iterate through each point in the grid

            for (int i = 0; i <= 2 * radius; i++) {
                for (int j = 0; j <= 2 * radius; j++) {
                    // Calculate the distance from the current point to the center of the circle
                    double distance = Math.sqrt(Math.pow(i - centerX, 2) + Math.pow(j - centerY, 2));
                    // If the distance is approximately equal to the radius, print a star, otherwise print a space
                    if (Math.abs(distance - radius) < 0.5) {
                        pattern.append("* ");
                    } else {
                        pattern.append("  "); // two spaces to maintain the shape
                    }
                }
                pattern.append("\n");
            }
            return pattern.toString();

        } else if (spinId.equals("3")) {

        }
        return pattern.toString();

    }
}