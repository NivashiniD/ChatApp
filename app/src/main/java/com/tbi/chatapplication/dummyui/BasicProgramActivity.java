package com.tbi.chatapplication.dummyui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tbi.chatapplication.R;
import com.tbi.chatapplication.databinding.ActivityBasicProgramBinding;

public class BasicProgramActivity extends AppCompatActivity {
    ActivityBasicProgramBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityBasicProgramBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        onClick();

    }

    private void onClick() {
        binding.btnCheck.setOnClickListener(v -> {
            String inputString = binding.edtName.getText().toString();
            if(inputString.isEmpty()){
                Toast.makeText(BasicProgramActivity.this, "please enter a Name ", Toast.LENGTH_SHORT).show();
            }else {
                if (isPalindrome(inputString)) {
                    binding.tvResult.setVisibility(View.VISIBLE);
                    binding.tvResult.setText("The entered string is a palindrome.");
                } else {
                    binding.tvResult.setVisibility(View.VISIBLE);
                    binding.tvResult.setText("The entered string is not a palindrome.");
                }
            }
        });
        binding.btnRevStr.setOnClickListener(v->{
            String inputString = binding.edtName.getText().toString();
            if(inputString.isEmpty()){
                Toast.makeText(BasicProgramActivity.this, "please enter a Name ", Toast.LENGTH_SHORT).show();
            }else {
                String reversedString = reverseString(inputString);

                binding.tvResult.setVisibility(View.VISIBLE);
                binding.tvResult.setText("Reversed String: " + reversedString);
                Log.d("ReversedString", "Reversed String: " + reversedString);
            }
        });
        binding.btnFibbo.setOnClickListener(v->{
            String inputText= binding.edtName.getText().toString();
            int numberOfTerms = Integer.parseInt(inputText);
            String fibonacciSeries = generateFibonacciSeries(numberOfTerms);
            binding.tvResult.setVisibility(View.VISIBLE);
            binding.tvResult.setText(fibonacciSeries);

            String[] numbersStr = fibonacciSeries.split(", ");
            int[] numbers = new int[numbersStr.length];
            for (int i = 0; i < numbersStr.length; i++) {
                numbers[i] = Integer.parseInt(numbersStr[i]);
            }
            binding.btnMax.setVisibility(View.VISIBLE);
            binding.btnMax.setOnClickListener(data->{
                int max = findMax(numbers);
                int min = findMin(numbers);
                binding.tvSecResult.setVisibility(View.VISIBLE);
                binding.tvSecResult.setText("Maximum  num is " + max + "Minimum num  is: " + min);
            });
        });

        binding.btnFacto.setOnClickListener(v->{
            String inputText= binding.edtName.getText().toString();
            int num = Integer.parseInt(inputText);
            long factorial = calculateFactorial(num);
            binding.tvResult.setVisibility(View.VISIBLE);
            binding.tvResult.setText("Factorial of " + num + " is: " + factorial);

            binding.btnDigit.setVisibility(View.VISIBLE);
            binding.btnDigit.setOnClickListener(data->{
                int sum = sumOfDigits(factorial);
                binding.tvSecResult.setVisibility(View.VISIBLE);
                binding.tvSecResult.setText("Sum of digits of " + factorial + " is: " + sum);

            });

            binding.btnRevInt.setVisibility(View.VISIBLE);
            binding.btnRevInt.setOnClickListener(data->{
                int revN = reverseInteger(factorial);
                binding.tvSecResult.setVisibility(View.VISIBLE);
                binding.tvSecResult.setText("Reverse Number of " + factorial + " is: " + revN);

            });
        });

        binding.btnPrime.setOnClickListener(v->{
            String inputText= binding.edtName.getText().toString();
            int num = Integer.parseInt(inputText);
            boolean isPrime = isPrimeNumber(num);

            // Display the result
            if (isPrime) {
                binding.tvResult.setVisibility(View.VISIBLE);
                binding.tvResult.setText(num + " is a prime number.");
            } else {
                binding.tvResult.setVisibility(View.VISIBLE);
                binding.tvResult.setText(num + " is not a prime number.");
            }
        });
    }
    private boolean isPalindrome(String str) {
        str = str.replaceAll("\\s+", "").toLowerCase();
        int length = str.length();
        for (int i = 0; i < length / 2; i++) {
            if (str.charAt(i) != str.charAt(length - i - 1)) {
                return false;
            }
        }
        return true;

    }

    private String reverseString(String str) {

        char[] charArray = str.toCharArray();

        int left = 0;
        int right = charArray.length - 1;

        while (left < right) {

            char temp = charArray[left];
            charArray[left] = charArray[right];
            charArray[right] = temp;

            left++;
            right--;
        }

        return new String(charArray);
    }

    private String generateFibonacciSeries(int numberOfTerms) {
        StringBuilder seriesBuilder = new StringBuilder();

        int firstTerm = 0;
        int secondTerm = 1;

        seriesBuilder.append(firstTerm).append(", ").append(secondTerm);

        for (int i = 2; i < numberOfTerms; i++) {
            int nextTerm = firstTerm + secondTerm;
            seriesBuilder.append(", ").append(nextTerm);
            firstTerm = secondTerm;
            secondTerm = nextTerm;
        }

        return seriesBuilder.toString();
    }

    private long calculateFactorial(int n) {
        if (n < 0) {
            return -1;
        } else if (n == 0 || n == 1) {
            return 1;
        } else {
            long result = 1;
            for (int i = 2; i <= n; i++) {
                result *= i;
            }
            return result;
        }
    }
    private boolean isPrimeNumber(int n) {
        if (n <= 1) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
    private int sumOfDigits(long n) {
        int sum = 0;
        while (n > 0) {
            sum += n % 10;
            n /= 10;
        }
        return sum;
    }

    private int findMax(int[] numbers) {
        int max = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] > max) {
                max = numbers[i];
            }
        }
        return max;
    }

    // Method to find minimum number
    private int findMin(int[] numbers) {
        int min = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] < min) {
                min = numbers[i];
            }
        }
        return min;
    }

    public int reverseInteger(long n) {
        long reversed = 0;
        while (n != 0) {
            long digit = n % 10;
            reversed = reversed * 10 + digit;
            n /= 10;
        }
        return Math.toIntExact(reversed);
    }
}