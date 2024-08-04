package com.example.attendancesystem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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

import java.util.UUID;

public class AdminProfile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ImageView profileImageView;
    TextView txtUsername, txtUseremail, logout;
    MaterialButton attendanceReportButton, viewAttendanceButton;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_profile);


        txtUsername = findViewById(R.id.userName);
        txtUseremail = findViewById(R.id.ueserEmail);
        attendanceReportButton = findViewById(R.id.attendanceReportButton);
        viewAttendanceButton = findViewById(R.id.viewAttendanceButton);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        profileImageView = findViewById(R.id.profileImageView);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        attendanceReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminProfile.this, AttendanceReport.class);
                startActivity(intent);
            }
        });

        viewAttendanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminProfile.this, AdminPanel.class);
                startActivity(intent);
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
                uploadImageToFirebase();
            }
        });


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            loadUserProfile(userId);
        }



    }



    private void loadUserProfile(String userId) {
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AdminModel user = dataSnapshot.getValue(AdminModel.class);
                if (user != null) {
                    txtUsername.setText(user.getUsername());
                    txtUseremail.setText(user.getEmail());
                    Glide.with(AdminProfile.this)
                            .load(user.getProfileImage())
                            .placeholder(R.drawable.profileimage)
                            .error(R.drawable.profile1)
                            .into(profileImageView);
                } else {
                    Toast.makeText(AdminProfile.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminProfile.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
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
                    .addOnSuccessListener(taskSnapshot -> {
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                    String imageUrl = uri.toString();

                                    Log.d("UploadSuccess", "Image URL: " + imageUrl);

                                    databaseReference.child("profileImage").setValue(imageUrl)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(AdminProfile.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(AdminProfile.this, "Database update failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(AdminProfile.this, "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("UploadFailure", "Failed to get download URL", e);
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AdminProfile.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("UploadFailure", "Upload failed", e);
                    });
        } else {
            Toast.makeText(AdminProfile.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }



}