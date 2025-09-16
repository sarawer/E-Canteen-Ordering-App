package com.example.e_canteenorderingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class RegistrationActivity extends AppCompatActivity {

    private EditText etName, etUserId, etPassword, etConfirm;
    private RadioGroup rgRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        etName = findViewById(R.id.et_reg_name);
        etUserId = findViewById(R.id.et_reg_user_id);
        etPassword = findViewById(R.id.et_reg_password);
        etConfirm = findViewById(R.id.et_reg_confirm);
        rgRole = findViewById(R.id.rg_role);
        Button btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register() {
        String name = etName.getText().toString().trim();
        String id = etUserId.getText().toString().trim();
        String pw = etPassword.getText().toString().trim();
        String cf = etConfirm.getText().toString().trim();

        if (name.isEmpty() || id.isEmpty() || pw.isEmpty() || cf.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pw.equals(cf)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        int checked = rgRole.getCheckedRadioButtonId();
        String role = (checked == R.id.rb_admin) ? "admin" : "student";

        // Enforce ID length rule
        if ("student".equals(role) && id.length() != 13) {
            Toast.makeText(this, "Student ID must be exactly 13 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        if ("admin".equals(role) && id.length() != 3) {
            Toast.makeText(this, "Admin ID must be exactly 3 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sp = getSharedPreferences("users", MODE_PRIVATE);
        if (sp.contains("user_" + id + "_password")) {
            Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        sp.edit()
                .putString("user_" + id + "_name", name)
                .putString("user_" + id + "_password", pw)
                .putString("user_" + id + "_role", role)
                .apply();

        Toast.makeText(this, "Registered successfully! Please login.", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(RegistrationActivity.this, MainActivity.class);
        i.putExtra("prefill_id", id);
        startActivity(i);
        finish();
    }
}
