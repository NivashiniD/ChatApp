package com.tbi.chatapplication.dummyui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tbi.chatapplication.R;
import com.tbi.chatapplication.databinding.ActivityDummyBinding;
import com.tbi.chatapplication.splash.SplashActivity;
import com.tbi.chatapplication.ui.model.UserModel;
import com.tbi.chatapplication.utils.FirebaseUtil;

import java.text.DecimalFormat;

public class DummyActivity extends AppCompatActivity {
    ActivityDummyBinding binding;

    SharedPreferences sharedPreferences;

    private static final char ADDITION = '+';
    private static final char SUBTRACTION = '-';
    private static final char MULTIPLICATION = '*';
    private static final char DIVISION = '/';
    private static final char PERCENT = '%';
    private char currentSymbol;
    private double firstValue = Double.NaN;
    private double secondValue;

    String expression = "";
    Float F1,F2,F;
    Boolean add = false;
    Boolean multi = false;
    Boolean div= false;
    Boolean sub= false;
    UserModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDummyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        boolean isNewUser = sharedPreferences.getBoolean("isNewUser", true);
        
        if (isNewUser) {
            showInstructions();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isNewUser", false);
            editor.apply();
        }
        newUser();


        String phoneNumber = sharedPreferences.getString("phone", ""); // Example retrieval from shared preferences
        model = new UserModel(phoneNumber, "username", null, "userId");

        onClick();
    }

    private void showInstructions() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DummyActivity.this);
        alertDialogBuilder.setTitle("Welcome to My App!");
        alertDialogBuilder
                .setMessage("Long press on the Equals Button Then You can secretly Login and Chat ")
                .setCancelable(false)
                .setPositiveButton("Got It",
                        (dialog, id) -> {

                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.brown_900));
    }

    private void newUser() {
      
    }

    @SuppressLint("SetTextI18n")
    private void onClick() {
        binding.btnNext.setOnClickListener(v->{
            Intent intent=new Intent(DummyActivity.this, TimeselectionActivity.class);
            startActivity(intent);
        });
        binding.btnPalindrome.setOnClickListener(v->{
            Intent intent=new Intent(DummyActivity.this, BasicProgramActivity.class);
            startActivity(intent);
        });
        binding.btnPattern.setOnClickListener(v->{
            Intent intent=new Intent(DummyActivity.this, PatternActivity.class);
            startActivity(intent);
        });
        binding.btnWeekday.setOnClickListener(v->{
            Intent intent=new Intent(DummyActivity.this, WeekdayActivity.class);
            startActivity(intent);
        });
        binding.btnEqual.setOnClickListener(v->{

            String input = binding.displayScreen.getText().toString();
            if (!input.isEmpty()) {
                try {
                    F2 = Float.parseFloat(input);
                    if(add){
                        F2=Float.parseFloat(binding.displayScreen.getText().toString());
                        F=F1+F2;
                        String Ans=String.valueOf(F);
                        binding.ansScreen.setText(Ans);
                        add=false;
                    } else if (multi) {
                        F2=Float.parseFloat(binding.displayScreen.getText().toString());
                        F=F1*F2;
                        String Ans=String.valueOf(F);
                        binding.ansScreen.setText(Ans);
                        multi=false;

                    } else if (sub) {
                        F2=Float.parseFloat(binding.displayScreen.getText().toString());
                        F=F1-F2;
                        String Ans=String.valueOf(F);
                        binding.ansScreen.setText(Ans);
                        sub=false;

                    } else if (div) {
                        F2=Float.parseFloat(binding.displayScreen.getText().toString());
                        F=F1/F2;
                        String Ans=String.valueOf(F);
                        binding.ansScreen.setText(Ans);
                        div=false;

                    }else {
                        Toast.makeText(DummyActivity.this,"ERROR",Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(DummyActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(DummyActivity.this, "Input is empty", Toast.LENGTH_SHORT).show();
            }

        });
        binding.btnEqual.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SHOW_GETIN_DIALOG();
                return true;
            }
        });
        binding.btnOne.setOnClickListener(v->{
            binding.displayScreen.setText(binding.displayScreen.getText()+"1");
        });
        binding.btnTwo.setOnClickListener(v->{
            binding.displayScreen.setText(binding.displayScreen.getText()+"2");
        });
        binding.btnThree.setOnClickListener(v->{
            binding.displayScreen.setText(binding.displayScreen.getText()+"3");
        });
        binding.btnFour.setOnClickListener(v->{
            binding.displayScreen.setText(binding.displayScreen.getText()+"4");
        });
        binding.btnFive.setOnClickListener(v->{
            binding.displayScreen.setText(binding.displayScreen.getText()+"5");
        });
        binding.btnSix.setOnClickListener(v->{
            binding.displayScreen.setText(binding.displayScreen.getText()+"6");
        });
        binding.btnSeven.setOnClickListener(v->{
            binding.displayScreen.setText(binding.displayScreen.getText()+"7");
        });
        binding.btnEight.setOnClickListener(v->{
            binding.displayScreen.setText(binding.displayScreen.getText()+"8");
        });
        binding.btnNine.setOnClickListener(v->{
            binding.displayScreen.setText(binding.displayScreen.getText()+"9");
        });
        binding.btnZero.setOnClickListener(v->{
            binding.displayScreen.setText(binding.displayScreen.getText()+"0");
        });
        binding.btnAdd.setOnClickListener(v->{
            F1=Float.parseFloat(binding.displayScreen.getText().toString());
            //addToExpression("+");
            binding.displayScreen.setText("");
            add=true;

        });
        binding.btnSub.setOnClickListener(v->{
            F1=Float.parseFloat(binding.displayScreen.getText().toString());
            //addToExpression("-");
            binding.displayScreen.setText("");
            sub=true;
        });
        binding.btnDiv.setOnClickListener(v->{

            F1=Float.parseFloat(binding.displayScreen.getText().toString());
            //addToExpression("/");
            binding.displayScreen.setText("");
            div=true;
        });
        binding.btnMulti.setOnClickListener(v->{
            F1=Float.parseFloat(binding.displayScreen.getText().toString());
            //addToExpression("*");
            binding.displayScreen.setText("");
            multi=true;
        });

        binding.btnDot.setOnClickListener(v->{
            binding.displayScreen.setText(binding.displayScreen.getText()+".");
        });
        binding.btnClear.setOnClickListener(v -> {
            binding.displayScreen.setText("");
            binding.ansScreen.setText("0");
        });


    }
    private void SHOW_GETIN_DIALOG() {
        //DialogGettinBinding dialogGettinBinding = DialogGettinBinding.inflate(getLayoutInflater());
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DummyActivity.this);
        //alertDialogBuilder.setView(dialogGettinBinding.getRoot());

        alertDialogBuilder.setTitle("Get In");
        alertDialogBuilder
                .setMessage("Do you want get in this App ?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        (dialog, id) -> {
                            doLogIn();
                        })
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.app_color));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.app_color));
    }
    private void doLogIn() {
        Intent i = new Intent(DummyActivity.this, SplashActivity.class);
        startActivity(i);
        /*String Password = binding.textViewResult.getText().toString();
        ///String savePassword = sharedPreferences.getString("password", null);
        if (model != null) {
            String savedPhoneNumber = model.getPhone();

            if (Password.equals(savedPhoneNumber)) {

            } else if (Password.isEmpty()) {
                Toast.makeText(DummyActivity.this, "Enter Number please..", Toast.LENGTH_SHORT).show();
            } else {
                // Handle incorrect password scenario
                Toast.makeText(DummyActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle case where UserModel object is null
            Toast.makeText(DummyActivity.this, "UserModel is null", Toast.LENGTH_SHORT).show();
        }
*/
    }

}