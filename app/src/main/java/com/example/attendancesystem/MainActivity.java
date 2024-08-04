package com.example.attendancesystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ImageView profileImageView;






    TextView txtUsername, txtUseremail;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    MaterialButton presentButton, leaveButton, viewAttendanceButton;
    DatabaseReference attendanceRef;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        profileImageView = findViewById(R.id.profileImageView);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        txtUsername = findViewById(R.id.userName);
        txtUseremail = findViewById(R.id.ueserEmail);
        presentButton = findViewById(R.id.markAttendanceButton);
        leaveButton = findViewById(R.id.leaveButton);
        viewAttendanceButton = findViewById(R.id.viewAttendanceButton);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();




        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
                uploadImageToFirebase();
            }
        });


        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Coming soon \n Working on it", Toast.LENGTH_SHORT).show();
            }
        });




        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            loadUserProfile(userId);
        }



        viewAttendanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserAttendance.class);
                startActivity(intent);

            }
        });


        presentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    loadUserProfile(userId);
                    markAttendance();
                } else {
                    Toast.makeText(MainActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void loadUserProfile(String userId) {
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel user = dataSnapshot.getValue(UserModel.class);
                if (user != null) {
                    txtUsername.setText(user.getUsername());
                    txtUseremail.setText(user.getEmail());
                    Glide.with(MainActivity.this)
                            .load(user.getProfileImageUrl())
                            .placeholder(R.drawable.profileimage)
                            .error(R.drawable.profile1)
                            .into(profileImageView);
                } else {
                    Toast.makeText(MainActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }






    private void markAttendance() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference userRef;
        userRef = mDatabase.child("users").child(userId);


        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserModel user = dataSnapshot.getValue(UserModel.class);
                    if (user != null) {
                        String userEmail = user.getEmail();
                        String userName = user.getUsername();
                        processAttendance(userId, userEmail, userName);
                    } else {
                        Toast.makeText(getApplicationContext(), "User data not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "User not registered", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error retrieving user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processAttendance(String userId, String userEmail, String userName) {
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);



        DatabaseReference userAttendanceRef = mDatabase.child("users").child(userId).child("attendance").child(currentDate);

        DatabaseReference publicAttendanceRef = mDatabase.child("public").child("attendance").child(userId).child(currentDate);

        userAttendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Attendance Already Marked")
                            .setMessage("You have already marked your attendance for today.\n\n Come back tomorrow for your attendance.")
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                            .setIcon(R.drawable.clear)
                            .show();

                } else {
                    String isPresent = currentHour < 10 ? "Present" : "Absent";

                    AttendanceModel attendance = new AttendanceModel(userId, currentDate, isPresent, userEmail, userName);

                    Map<String, Object> updates = new HashMap<>();
                    updates.put(userAttendanceRef.getPath().toString(), attendance);
                    updates.put(publicAttendanceRef.getPath().toString(), attendance);

                    mDatabase.updateChildren(updates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        if (currentHour < 10) {
                                            new AlertDialog.Builder(MainActivity.this)
                                                    .setTitle("Attendance Marked")
                                                    .setMessage("You have marked your attendance successfully.")
                                                    .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                                                    .setIcon(R.drawable.makred)
                                                    .show();
                                        } else {
                                            new AlertDialog.Builder(MainActivity.this)
                                                    .setTitle("Absent Marked")
                                                    .setMessage("Attendance window closes at 10:00 AM.\n\nYou are marked as Absent.")
                                                    .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                                                    .setIcon(R.drawable.makred)
                                                    .show();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Failed to mark attendance", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error checking attendance status", Toast.LENGTH_SHORT).show();
            }
        });


    }



    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }



    private void uploadImageToFirebase() {
        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child("profile_images/" + UUID.randomUUID().toString());
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        databaseReference.child("profileImageUrl").setValue(imageUrl)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }))
                    .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }


}