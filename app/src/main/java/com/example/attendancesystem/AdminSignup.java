package com.example.attendancesystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminSignup extends AppCompatActivity {

    Button btnRegister;
    TextView txtLogin;
    EditText email, password, username, cnic, number, address;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_signup);

        btnRegister = findViewById(R.id.registerAdmin);
        txtLogin = findViewById(R.id.txtLogin);
        email = findViewById(R.id.adminEmail);
        password = findViewById(R.id.adminPassword);
        username = findViewById(R.id.companyName);
        cnic = findViewById(R.id.cnic);
        number = findViewById(R.id.contact);
        address = findViewById(R.id.companyAddress);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();



        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminSignup.this, Login.class);
                startActivity(intent);
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailStr = email.getText().toString();
                String passwordStr = password.getText().toString();
                String usernameStr = username.getText().toString();
                String cnicStr = cnic.getText().toString();
                String numberStr = number.getText().toString();
                String addressStr = address.getText().toString();
                registerUser(emailStr, passwordStr, usernameStr, numberStr, addressStr, cnicStr);
            }
        });

    }



    private void registerUser(String email, String password,String username, String number, String address, String cnic) {

        if (email.isEmpty() || password.isEmpty() || username.isEmpty() || cnic.isEmpty() || number.isEmpty() || address.isEmpty()) {
            Toast.makeText(AdminSignup.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        String userId = user.getUid();
                        writeNewUser(userId,username, email, number, address, cnic);
                    }
                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(AdminSignup.this, AdminProfile.class);
                    startActivity(intent);
                    finish();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminSignup.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void writeNewUser(String userId, String username, String email, String number, String address, String cnic) {
        AdminModel user = new AdminModel(username, email, number, address, cnic, null);
        mDatabase.child("users").child(userId).setValue(user);
    }



}