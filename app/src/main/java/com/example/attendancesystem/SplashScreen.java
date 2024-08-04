package com.example.attendancesystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.os.Handler;
import android.widget.Toast;

public class SplashScreen extends AppCompatActivity {

    private static final long SPLASH_SCREEN_DURATION = 1000L;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserPreferences";
    private static final String KEY_ROLE = "userRole";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAuth = FirebaseAuth.getInstance();
                sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String currentUserUid = currentUser.getUid();
                    mDatabase = FirebaseDatabase.getInstance().getReference("users").child(currentUserUid);

                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String role;
                                if (dataSnapshot.hasChild("cnic")) {
                                    role = "admin";
                                } else {
                                    role = "user";
                                }

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(KEY_ROLE, role);
                                editor.apply();

                                if (role.equals("admin")) {
                                    startActivity(new Intent(SplashScreen.this, AdminProfile.class));
                                } else {
                                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                                }
                                finish();
                            } else {
                                Toast.makeText(SplashScreen.this, "User data not found.", Toast.LENGTH_SHORT).show();
                                redirectToLogin();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(SplashScreen.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            redirectToLogin();
                        }
                    });
                } else {
                    redirectToLogin();
                }
            }
        }, SPLASH_SCREEN_DURATION);


    }

    private void redirectToLogin() {
        startActivity(new Intent(SplashScreen.this, Login.class));
        finish();
    }
}