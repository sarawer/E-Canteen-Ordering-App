package com.example.e_canteenorderingapp;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class OTPVerificationActivity extends AppCompatActivity {

    private static final String TAG = "OTPVerificationActivity";
    private TextView tvPhoneNumber, tvPaymentMethod, tvTotalAmount, btnBack, tvTimer, tvResendOTP;
    private EditText etOTP1, etOTP2, etOTP3, etOTP4, etOTP5, etOTP6;
    private Button btnVerifyPay;
    private LinearLayout layoutProcessing;
    private ProgressBar progressBar;
    private TextView tvProcessingText;
    
    private double totalAmount = 0.0;
    private String paymentMethod = "";
    private String phoneNumber = "";
    private String tableNumber = "";
    private String correctOTP = "123456"; // Simulated OTP
    private CountDownTimer countDownTimer;
    private boolean canResend = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        Log.d(TAG, "OTPVerificationActivity onCreate started");

        try {
            // Get data from previous screen
            Intent intent = getIntent();
            if (intent != null) {
                totalAmount = intent.getDoubleExtra("total_amount", 0.0);
                paymentMethod = intent.getStringExtra("payment_method");
                phoneNumber = intent.getStringExtra("phone_number");
                tableNumber = intent.getStringExtra("table_number");
                Log.d(TAG, "Received data - Amount: " + totalAmount + ", Method: " + paymentMethod + ", Phone: " + phoneNumber + ", Table: " + tableNumber);
            }

            initializeViews();
            setupClickListeners();
            setupOTPInput();
            startTimer();

            // Show simulated OTP in toast for demo purposes
            Toast.makeText(this, "Demo OTP: " + correctOTP, Toast.LENGTH_LONG).show();

            Log.d(TAG, "onCreate completed successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error initializing OTP verification: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() throws Exception {
        Log.d(TAG, "Initializing views");

        btnBack = findViewById(R.id.btn_back);
        if (btnBack == null) throw new Exception("btn_back not found");

        tvPaymentMethod = findViewById(R.id.tv_payment_method);
        if (tvPaymentMethod == null) throw new Exception("tv_payment_method not found");

        tvPhoneNumber = findViewById(R.id.tv_phone_number);
        if (tvPhoneNumber == null) throw new Exception("tv_phone_number not found");

        tvTotalAmount = findViewById(R.id.tv_total_amount);
        if (tvTotalAmount == null) throw new Exception("tv_total_amount not found");

        tvTimer = findViewById(R.id.tv_timer);
        if (tvTimer == null) throw new Exception("tv_timer not found");

        tvResendOTP = findViewById(R.id.tv_resend_otp);
        if (tvResendOTP == null) throw new Exception("tv_resend_otp not found");

        // OTP input fields
        etOTP1 = findViewById(R.id.et_otp_1);
        etOTP2 = findViewById(R.id.et_otp_2);
        etOTP3 = findViewById(R.id.et_otp_3);
        etOTP4 = findViewById(R.id.et_otp_4);
        etOTP5 = findViewById(R.id.et_otp_5);
        etOTP6 = findViewById(R.id.et_otp_6);

        btnVerifyPay = findViewById(R.id.btn_verify_pay);
        if (btnVerifyPay == null) throw new Exception("btn_verify_pay not found");

        layoutProcessing = findViewById(R.id.layout_processing);
        if (layoutProcessing == null) throw new Exception("layout_processing not found");

        progressBar = findViewById(R.id.progress_bar);
        if (progressBar == null) throw new Exception("progress_bar not found");

        tvProcessingText = findViewById(R.id.tv_processing_text);
        if (tvProcessingText == null) throw new Exception("tv_processing_text not found");

        // Set data
        tvPaymentMethod.setText(paymentMethod + " Payment");
        tvPhoneNumber.setText(phoneNumber);
        tvTotalAmount.setText("৳" + String.format("%.2f", totalAmount));

        // Initially disable verify button
        btnVerifyPay.setEnabled(false);
        btnVerifyPay.setAlpha(0.5f);

        // Hide processing layout initially
        layoutProcessing.setVisibility(View.GONE);

        Log.d(TAG, "All views initialized successfully");
    }

    private void setupClickListeners() {
        try {
            Log.d(TAG, "Setting up click listeners");

            btnBack.setOnClickListener(v -> {
                Log.d(TAG, "Back button clicked");
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                finish();
            });

            btnVerifyPay.setOnClickListener(v -> {
                Log.d(TAG, "Verify & Pay button clicked");
                verifyOTPAndPay();
            });

            tvResendOTP.setOnClickListener(v -> {
                Log.d(TAG, "Resend OTP clicked");
                if (canResend) {
                    resendOTP();
                }
            });

            Log.d(TAG, "Click listeners setup successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error setting up click listeners: " + e.getMessage(), e);
        }
    }

    private void setupOTPInput() {
        EditText[] otpFields = {etOTP1, etOTP2, etOTP3, etOTP4, etOTP5, etOTP6};

        for (int i = 0; i < otpFields.length; i++) {
            final int index = i;
            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1) {
                        // Move to next field
                        if (index < otpFields.length - 1) {
                            otpFields[index + 1].requestFocus();
                        }
                    } else if (s.length() == 0) {
                        // Move to previous field
                        if (index > 0) {
                            otpFields[index - 1].requestFocus();
                        }
                    }

                    // Check if all fields are filled
                    checkOTPComplete();
                }
            });
        }
    }

    private void checkOTPComplete() {
        EditText[] otpFields = {etOTP1, etOTP2, etOTP3, etOTP4, etOTP5, etOTP6};
        boolean allFilled = true;

        for (EditText field : otpFields) {
            if (field.getText().toString().trim().isEmpty()) {
                allFilled = false;
                break;
            }
        }

        if (allFilled) {
            btnVerifyPay.setEnabled(true);
            btnVerifyPay.setAlpha(1.0f);
        } else {
            btnVerifyPay.setEnabled(false);
            btnVerifyPay.setAlpha(0.5f);
        }
    }

    private String getEnteredOTP() {
        return etOTP1.getText().toString() +
               etOTP2.getText().toString() +
               etOTP3.getText().toString() +
               etOTP4.getText().toString() +
               etOTP5.getText().toString() +
               etOTP6.getText().toString();
    }

    private void verifyOTPAndPay() {
        try {
            String enteredOTP = getEnteredOTP();
            Log.d(TAG, "Verifying OTP: " + enteredOTP);

            if (enteredOTP.length() != 6) {
                Toast.makeText(this, "Please enter complete OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show processing animation
            showProcessingAnimation();

            // Simulate OTP verification and payment processing
            layoutProcessing.postDelayed(() -> {
                if (enteredOTP.equals(correctOTP)) {
                    // OTP is correct, process payment
                    processPayment();
                } else {
                    // Wrong OTP
                    hideProcessingAnimation();
                    Toast.makeText(this, "Invalid OTP. Please try again.", Toast.LENGTH_LONG).show();
                    clearOTPFields();
                }
            }, 2000);

        } catch (Exception e) {
            Log.e(TAG, "Error verifying OTP: " + e.getMessage(), e);
            hideProcessingAnimation();
            Toast.makeText(this, "Error processing payment", Toast.LENGTH_SHORT).show();
        }
    }

    private void showProcessingAnimation() {
        layoutProcessing.setVisibility(View.VISIBLE);
        tvProcessingText.setText("Verifying OTP...");
        
        // Animate progress bar
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 0, 50);
        progressAnimator.setDuration(1000);
        progressAnimator.start();

        // Change text after 1 second
        layoutProcessing.postDelayed(() -> {
            tvProcessingText.setText("Processing payment...");
            ObjectAnimator progressAnimator2 = ObjectAnimator.ofInt(progressBar, "progress", 50, 100);
            progressAnimator2.setDuration(1000);
            progressAnimator2.start();
        }, 1000);
    }

    private void hideProcessingAnimation() {
        layoutProcessing.setVisibility(View.GONE);
        progressBar.setProgress(0);
    }

    private void processPayment() {
        try {
            Log.d(TAG, "Processing payment");

            // Cancel timer
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }

            // Clear cart (simulate)
            clearCart();

            // Show success and navigate
            Toast.makeText(this, "Payment successful! Order placed.", Toast.LENGTH_LONG).show();

            // Navigate to order success screen
            Intent intent = new Intent(this, OrderSuccessActivity.class);
            intent.putExtra("order_number", generateOrderNumber());
            intent.putExtra("total_amount", totalAmount);
            intent.putExtra("payment_method", paymentMethod);
            intent.putExtra("phone_number", phoneNumber);
            intent.putExtra("table_number", tableNumber);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            Log.e(TAG, "Error processing payment: " + e.getMessage(), e);
            hideProcessingAnimation();
            Toast.makeText(this, "Payment failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearOTPFields() {
        etOTP1.setText("");
        etOTP2.setText("");
        etOTP3.setText("");
        etOTP4.setText("");
        etOTP5.setText("");
        etOTP6.setText("");
        etOTP1.requestFocus();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(60000, 1000) { // 1 minute countdown
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                tvTimer.setText(String.format("Resend OTP in %02d:%02d", seconds / 60, seconds % 60));
                canResend = false;
                tvResendOTP.setAlpha(0.5f);
            }

            @Override
            public void onFinish() {
                tvTimer.setText("OTP expired");
                canResend = true;
                tvResendOTP.setAlpha(1.0f);
            }
        };
        countDownTimer.start();
    }

    private void resendOTP() {
        // Generate new OTP (for demo, keep the same)
        Toast.makeText(this, "New OTP sent: " + correctOTP, Toast.LENGTH_LONG).show();
        clearOTPFields();
        startTimer();
    }

    private void clearCart() {
        // This would typically clear the cart from database
        // For now, just log it
        Log.d(TAG, "Cart cleared after successful payment");
    }

    private String generateOrderNumber() {
        // Generate order number in format: EC-YYYY-XXX
        return "EC-2024-" + String.format("%03d", (int) (Math.random() * 900) + 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
