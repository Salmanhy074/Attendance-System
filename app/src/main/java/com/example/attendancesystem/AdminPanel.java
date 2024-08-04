package com.example.attendancesystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminPanel extends AppCompatActivity {

    RecyclerView attendanceRecyclerView;
    AttendanceAdapter attendanceAdapter;
    List<AttendanceModel> attendanceList;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    TextView txtAttendanceReport;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_panel);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        attendanceRecyclerView = findViewById(R.id.recyclerViewAdmin);
        txtAttendanceReport = findViewById(R.id.attendanceRecord);

        attendanceRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        attendanceList = new ArrayList<>();
        attendanceAdapter = new AttendanceAdapter(this, attendanceList);


        loadAttendanceData();



        txtAttendanceReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPanel.this, AttendanceReport.class);
                startActivity(intent);
            }
        });



    }

    private void loadAttendanceData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {


            DatabaseReference attendanceRef = mDatabase.child("public").child("attendance");

            attendanceRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    attendanceList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String newId = snapshot.getKey();
                        if (newId != null) {
                            attendanceRef.child(newId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot data : snapshot.getChildren()) {
                                        AttendanceModel attendance = data.getValue(AttendanceModel.class);
                                        attendanceList.add(attendance);
                                        attendanceRecyclerView.setAdapter(attendanceAdapter);
                                        attendanceAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            Toast.makeText(AdminPanel.this, "USER ID NOT FOUND", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }



}