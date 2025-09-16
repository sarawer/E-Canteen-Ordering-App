package com.example.e_canteenorderingapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_canteenorderingapp.data.AppDatabase;
import com.example.e_canteenorderingapp.data.CartItem;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "PaymentActivity";
    private TextView tvOrderAmount, tvTaxes, tvTotalAmount, tvFinalTotal, tvDeliveryTime;
    private LinearLayout layoutBkash, layoutNagad, layoutCash;
    private Button btnPayNow;
    private TextView btnBack;
    
    private String selectedPaymentMethod = "";
    private double orderAmount = 0.0;
    private double taxes = 0.0;
    private double totalAmount = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Log.d(TAG, "PaymentActivity onCreate started");
        
        try {
            Log.d(TAG, "Starting view initialization");
            initializeViews();
            Log.d(TAG, "Views initialized successfully");
            
            Log.d(TAG, "Starting cart data loading");
            loadCartData();
            Log.d(TAG, "Cart data loaded successfully");
            
            Log.d(TAG, "Starting click listener setup");
            setupClickListeners();
            Log.d(TAG, "Click listeners setup successfully");
            
            Log.d(TAG, "Starting payment method UI update");
            updatePaymentMethodUI();
            Log.d(TAG, "Payment method UI updated successfully");
            
            Log.d(TAG, "PaymentActivity onCreate completed successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error initializing payment screen: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() throws Exception {
        Log.d(TAG, "Initializing individual views");
        
        // Initialize each view with error checking
        tvOrderAmount = findViewById(R.id.tv_order_amount);
        if (tvOrderAmount == null) throw new Exception("tv_order_amount not found");
        Log.d(TAG, "tvOrderAmount initialized");
        
        tvTaxes = findViewById(R.id.tv_taxes);
        if (tvTaxes == null) throw new Exception("tv_taxes not found");
        Log.d(TAG, "tvTaxes initialized");
        
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        if (tvTotalAmount == null) throw new Exception("tv_total_amount not found");
        Log.d(TAG, "tvTotalAmount initialized");
        
        tvFinalTotal = findViewById(R.id.tv_final_total);
        if (tvFinalTotal == null) throw new Exception("tv_final_total not found");
        Log.d(TAG, "tvFinalTotal initialized");
        
        tvDeliveryTime = findViewById(R.id.tv_delivery_time);
        if (tvDeliveryTime == null) throw new Exception("tv_delivery_time not found");
        Log.d(TAG, "tvDeliveryTime initialized");
        
        layoutBkash = findViewById(R.id.layout_bkash);
        if (layoutBkash == null) throw new Exception("layout_bkash not found");
        Log.d(TAG, "layoutBkash initialized");
        
        layoutNagad = findViewById(R.id.layout_nagad);
        if (layoutNagad == null) throw new Exception("layout_nagad not found");
        Log.d(TAG, "layoutNagad initialized");
        
        layoutCash = findViewById(R.id.layout_cash);
        if (layoutCash == null) throw new Exception("layout_cash not found");
        Log.d(TAG, "layoutCash initialized");
        
        btnPayNow = findViewById(R.id.btn_pay_now);
        if (btnPayNow == null) throw new Exception("btn_pay_now not found");
        Log.d(TAG, "btnPayNow initialized");
        
        btnBack = findViewById(R.id.btn_back);
        if (btnBack == null) throw new Exception("btn_back not found");
        Log.d(TAG, "btnBack initialized");
        
        // Set default values
        tvOrderAmount.setText("৳0");
        tvTaxes.setText("৳0");
        tvTotalAmount.setText("৳0");
        tvFinalTotal.setText("৳0");
        tvDeliveryTime.setText("15-30mins");
        
        Log.d(TAG, "All views initialized and default values set");
    }

    private void loadCartData() {
        try {
            Log.d(TAG, "Setting up executor for cart data loading");
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    Log.d(TAG, "Getting database instance");
                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    if (db == null) {
                        Log.e(TAG, "Database is null");
                        return;
                    }
                    
                    Log.d(TAG, "Getting cart DAO");
                    if (db.cartDao() == null) {
                        Log.e(TAG, "Cart DAO is null");
                        return;
                    }
                    
                    Log.d(TAG, "Fetching cart items");
                    List<CartItem> cartItems = db.cartDao().getAll();
                    
                    // Calculate totals
                    orderAmount = 0.0;
                    if (cartItems != null) {
                        for (CartItem item : cartItems) {
                            if (item != null) {
                                orderAmount += (item.unitPriceCents * item.quantity) / 100.0;
                            }
                        }
                    }
                    
                    // Calculate taxes (5% of order amount)
                    taxes = orderAmount * 0.05;
                    totalAmount = orderAmount + taxes;
                    
                    Log.d(TAG, "Calculated totals - Order: " + orderAmount + ", Taxes: " + taxes + ", Total: " + totalAmount);
                    
                    runOnUiThread(() -> {
                        try {
                            tvOrderAmount.setText("৳" + String.format("%.2f", orderAmount));
                            tvTaxes.setText("৳" + String.format("%.2f", taxes));
                            tvTotalAmount.setText("৳" + String.format("%.2f", totalAmount));
                            tvFinalTotal.setText("৳" + String.format("%.2f", totalAmount));
                            
                            // Set delivery time based on order size
                            if (totalAmount > 50) {
                                tvDeliveryTime.setText("20-35mins");
                            } else {
                                tvDeliveryTime.setText("15-30mins");
                            }
                            
                            Log.d(TAG, "UI updated successfully");
                        } catch (Exception e) {
                            Log.e(TAG, "Error updating UI: " + e.getMessage(), e);
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Error loading cart data: " + e.getMessage(), e);
                    runOnUiThread(() -> {
                        Toast.makeText(PaymentActivity.this, "Error loading cart data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
            executor.shutdown();
            Log.d(TAG, "Executor shutdown");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up executor: " + e.getMessage(), e);
        }
    }

    private void setupClickListeners() {
        try {
            Log.d(TAG, "Setting up click listeners");
            
            btnBack.setOnClickListener(v -> {
                Log.d(TAG, "Back button clicked");
                finish();
            });
            
            layoutBkash.setOnClickListener(v -> {
                Log.d(TAG, "bKash layout clicked");
                selectedPaymentMethod = "bKash";
                updatePaymentMethodUI();
            });
            
            layoutNagad.setOnClickListener(v -> {
                Log.d(TAG, "Nagad layout clicked");
                selectedPaymentMethod = "Nagad";
                updatePaymentMethodUI();
            });

            layoutCash.setOnClickListener(v -> {
                Log.d(TAG, "Cash layout clicked");
                selectedPaymentMethod = "Cash";
                updatePaymentMethodUI();
            });
            
            btnPayNow.setOnClickListener(v -> {
                Log.d(TAG, "Pay Now button clicked");
                processPayment();
            });
            
            Log.d(TAG, "All click listeners setup successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up click listeners: " + e.getMessage(), e);
        }
    }

    private void updatePaymentMethodUI() {
        try {
            Log.d(TAG, "Updating payment method UI for: " + selectedPaymentMethod);
            Log.d(TAG, "Selected method length: " + selectedPaymentMethod.length());
            Log.d(TAG, "Selected method equals 'Cash': " + selectedPaymentMethod.equals("Cash"));
            Log.d(TAG, "Selected method equals 'CASH': " + selectedPaymentMethod.equals("CASH"));
            
            if (selectedPaymentMethod.equals("bKash")) {
                Log.d(TAG, "Setting bKash as selected");
                layoutBkash.setBackgroundResource(R.drawable.payment_selected_background);
                layoutNagad.setBackgroundResource(R.drawable.payment_unselected_background);
                layoutCash.setBackgroundResource(R.drawable.payment_unselected_background);
            } else if (selectedPaymentMethod.equals("Nagad")) {
                Log.d(TAG, "Setting Nagad as selected");
                layoutBkash.setBackgroundResource(R.drawable.payment_unselected_background);
                layoutNagad.setBackgroundResource(R.drawable.payment_selected_background);
                layoutCash.setBackgroundResource(R.drawable.payment_unselected_background);
            } else if (selectedPaymentMethod.equals("Cash")) {
                Log.d(TAG, "Setting Cash as selected");
                Log.d(TAG, "layoutCash background will be set to payment_selected_background");
                layoutBkash.setBackgroundResource(R.drawable.payment_unselected_background);
                layoutNagad.setBackgroundResource(R.drawable.payment_unselected_background);
                layoutCash.setBackgroundColor(0xFF8B4513); // Dark brown color
                Log.d(TAG, "Cash background set successfully");
            } else {
                Log.d(TAG, "No payment method selected, setting all to unselected");
                // No payment method selected - all should have the same unselected color
                layoutBkash.setBackgroundResource(R.drawable.payment_unselected_background);
                layoutNagad.setBackgroundResource(R.drawable.payment_unselected_background);
                layoutCash.setBackgroundColor(0xFFF5F5F5); // Light gray color
            }
            
            Log.d(TAG, "Payment method UI updated successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error updating payment method UI: " + e.getMessage(), e);
        }
    }

    private void processPayment() {
        try {
            Log.d(TAG, "Processing payment for amount: " + totalAmount);
            
            if (totalAmount <= 0) {
                Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show payment processing dialog
            Toast.makeText(this, "Processing " + selectedPaymentMethod + " payment...", Toast.LENGTH_SHORT).show();
            
            // Simulate payment processing
            btnPayNow.setEnabled(false);
            btnPayNow.setText("Processing...");
            
            // In a real app, you would integrate with actual payment gateways here
            // For now, we'll simulate a successful payment after 2 seconds
            
            btnPayNow.postDelayed(() -> {
                try {
                    Log.d(TAG, "Payment processing completed, clearing cart");
                    
                    // Clear cart after successful payment
                    clearCart();
                    
                    // Show success message
                    Toast.makeText(this, "Payment successful! Order placed.", Toast.LENGTH_LONG).show();
                    
                    // Navigate back to student home
                    Intent intent = new Intent(this, com.example.e_canteenorderingapp.student.StudentHomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Log.e(TAG, "Error processing payment: " + e.getMessage(), e);
                    btnPayNow.setEnabled(true);
                    btnPayNow.setText("Pay Now");
                }
            }, 2000);
            
            Log.d(TAG, "Payment processing started");
        } catch (Exception e) {
            Log.e(TAG, "Error in processPayment: " + e.getMessage(), e);
            Toast.makeText(this, "Error processing payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            btnPayNow.setEnabled(true);
            btnPayNow.setText("Pay Now");
        }
    }

    private void clearCart() {
        try {
            Log.d(TAG, "Clearing cart");
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    if (db != null && db.cartDao() != null) {
                        db.cartDao().clear();
                        Log.d(TAG, "Cart cleared successfully");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error clearing cart: " + e.getMessage(), e);
                }
            });
            executor.shutdown();
        } catch (Exception e) {
            Log.e(TAG, "Error setting up cart clearing: " + e.getMessage(), e);
        }
    }
}
