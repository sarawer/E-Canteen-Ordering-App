package com.example.e_canteenorderingapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.regex.Pattern;

public class BkashPaymentActivity extends AppCompatActivity {

    private static final String TAG = "BkashPaymentActivity";
    private TextView tvTotalAmount, btnBack;
    private EditText etPhoneNumber;
    private Button btnProceed;
    private double totalAmount = 0.0;
    private String tableNumber = "";
    
    // Bangladeshi phone number pattern
    private static final Pattern BD_PHONE_PATTERN = Pattern.compile("^(\\+880|880|0)?(1[3-9]\\d{8})$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bkash_payment);

        Log.d(TAG, "BkashPaymentActivity onCreate started");

        try {
            // Get data from previous screen
            Intent intent = getIntent();
            if (intent != null) {
                totalAmount = intent.getDoubleExtra("total_amount", 0.0);
                tableNumber = intent.getStringExtra("table_number");
                Log.d(TAG, "Received total amount: " + totalAmount + ", table number: " + tableNumber);
            }

            initializeViews();
            setupClickListeners();
            setupPhoneValidation();

            Log.d(TAG, "onCreate completed successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error initializing bKash payment: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() throws Exception {
        Log.d(TAG, "Initializing views");

        btnBack = findViewById(R.id.btn_back);
        if (btnBack == null) throw new Exception("btn_back not found");

        tvTotalAmount = findViewById(R.id.tv_total_amount);
        if (tvTotalAmount == null) throw new Exception("tv_total_amount not found");

        etPhoneNumber = findViewById(R.id.et_phone_number);
        if (etPhoneNumber == null) throw new Exception("et_phone_number not found");

        btnProceed = findViewById(R.id.btn_proceed);
        if (btnProceed == null) throw new Exception("btn_proceed not found");

        // Set total amount
        tvTotalAmount.setText("৳" + String.format("%.2f", totalAmount));

        // Initially disable proceed button
        btnProceed.setEnabled(false);
        btnProceed.setAlpha(0.5f);

        Log.d(TAG, "All views initialized successfully");
    }

    private void setupClickListeners() {
        try {
            Log.d(TAG, "Setting up click listeners");

            btnBack.setOnClickListener(v -> {
                Log.d(TAG, "Back button clicked");
                finish();
            });

            btnProceed.setOnClickListener(v -> {
                Log.d(TAG, "Proceed button clicked");
                proceedToOTP();
            });

            Log.d(TAG, "Click listeners setup successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error setting up click listeners: " + e.getMessage(), e);
        }
    }

    private void setupPhoneValidation() {
        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String phoneNumber = s.toString().trim();
                boolean isValid = isValidBangladeshiPhoneNumber(phoneNumber);
                
                if (isValid) {
                    btnProceed.setEnabled(true);
                    btnProceed.setAlpha(1.0f);
                    etPhoneNumber.setError(null);
                } else {
                    btnProceed.setEnabled(false);
                    btnProceed.setAlpha(0.5f);
                    if (phoneNumber.length() > 0) {
                        etPhoneNumber.setError("Please enter a valid Bangladeshi phone number");
                    }
                }
            }
        });
    }

    private boolean isValidBangladeshiPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }
        
        // Remove spaces and dashes
        phoneNumber = phoneNumber.replaceAll("[\\s-]", "");
        
        return BD_PHONE_PATTERN.matcher(phoneNumber).matches();
    }

    private void proceedToOTP() {
        try {
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            
            if (!isValidBangladeshiPhoneNumber(phoneNumber)) {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            // Format phone number for display
            String formattedPhone = formatPhoneNumber(phoneNumber);

            Log.d(TAG, "Proceeding to OTP with phone: " + formattedPhone);

            // Navigate to OTP screen
            Intent intent = new Intent(this, OTPVerificationActivity.class);
            intent.putExtra("total_amount", totalAmount);
            intent.putExtra("payment_method", "bKash");
            intent.putExtra("phone_number", formattedPhone);
            intent.putExtra("table_number", tableNumber);
            startActivity(intent);

        } catch (Exception e) {
            Log.e(TAG, "Error proceeding to OTP: " + e.getMessage(), e);
            Toast.makeText(this, "Error processing payment", Toast.LENGTH_SHORT).show();
        }
    }

    private String formatPhoneNumber(String phoneNumber) {
        // Remove any existing formatting
        phoneNumber = phoneNumber.replaceAll("[\\s-+]", "");
        
        // Remove country code if present
        if (phoneNumber.startsWith("880")) {
            phoneNumber = phoneNumber.substring(3);
        } else if (phoneNumber.startsWith("0")) {
            phoneNumber = phoneNumber.substring(1);
        }
        
        // Format as +880 1XXX-XXXXXX
        if (phoneNumber.length() == 10) {
            return "+880 " + phoneNumber.substring(0, 4) + "-" + phoneNumber.substring(4);
        }
        
        return "+880 " + phoneNumber;
    }
}
