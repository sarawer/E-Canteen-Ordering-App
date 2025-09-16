package com.example.e_canteenorderingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText etUserId, etPassword;
    private Button btnSignIn;
    private TextView tvRegister, tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_nav);

        etUserId = findViewById(R.id.et_user_id);
        etPassword = findViewById(R.id.et_password);
        btnSignIn = findViewById(R.id.btn_sign_in);
        tvRegister = findViewById(R.id.tv_register);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);

        String prefill = getIntent().getStringExtra("prefill_id");
        if (prefill != null) etUserId.setText(prefill);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Forgot Password clicked", Toast.LENGTH_SHORT).show();
            }
        });
        // Bottom nav: highlight Home by default to direct users back to browse
        android.view.View navHome = findViewById(R.id.nav_home);
        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, com.example.e_canteenorderingapp.student.StudentHomeActivity.class));
                overridePendingTransition(0, 0);
            });
            navHome.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.nav_pulse));
        }
        android.view.View navCart = findViewById(R.id.nav_cart);
        if (navCart != null) navCart.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, com.example.e_canteenorderingapp.student.CartActivity.class));
            overridePendingTransition(0, 0);
        });
        android.view.View navProfile = findViewById(R.id.nav_profile);
        if (navProfile != null) navProfile.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, com.example.e_canteenorderingapp.student.ProfileActivity.class));
            overridePendingTransition(0, 0);
        });
    }

    private void handleLogin() {
        String id = etUserId.getText().toString().trim();
        String pw = etPassword.getText().toString().trim();
        if (id.isEmpty() || pw.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sp = getSharedPreferences("users", MODE_PRIVATE);
        String savedPw = sp.getString("user_" + id + "_password", null);
        String role = sp.getString("user_" + id + "_role", null);

        if (savedPw == null) {
            Toast.makeText(this, "User not found. Please register first.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pw.equals(savedPw)) {
            Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save session
        sp.edit()
                .putString("session_user_id", id)
                .putString("session_role", role)
                .apply();

        routeByRole(role);
    }

    private void routeByRole(String role) {
        if ("admin".equalsIgnoreCase(role)) {
            startActivity(new Intent(MainActivity.this, com.example.e_canteenorderingapp.admin.AdminHomeActivity.class));
        } else {
            startActivity(new Intent(MainActivity.this, com.example.e_canteenorderingapp.student.StudentHomeActivity.class));
        }
        finish();
    }
}