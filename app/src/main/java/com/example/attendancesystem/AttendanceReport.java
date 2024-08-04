package com.example.attendancesystem;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AttendanceReport extends AppCompatActivity {

    TextView fromDateTextView, toDateTextView, reportTextView;
    Button generateReportButton;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    RecyclerView attendanceRecyclerView;
    AttendanceReportAdapter attendanceReportAdapter;
    List<AttendanceReportModel> attendanceList;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendance_report);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        fromDateTextView = findViewById(R.id.dateFrom);
        toDateTextView = findViewById(R.id.toDate);
        reportTextView = findViewById(R.id.report);
        generateReportButton = findViewById(R.id.report);

        attendanceRecyclerView = findViewById(R.id.attendanceRecyclerView);
        attendanceRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        attendanceList = new ArrayList<>();
        attendanceReportAdapter = new AttendanceReportAdapter(this, attendanceList);
        attendanceRecyclerView.setAdapter(attendanceReportAdapter);



        fromDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(fromDateTextView);
            }
        });

        toDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(toDateTextView);
            }
        });

        generateReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fromDate = fromDateTextView.getText().toString();
                String toDate = toDateTextView.getText().toString();
                if (!fromDate.isEmpty() && !toDate.isEmpty()) {
                    generateAttendanceReport(fromDate, toDate);
                } else {
                    Toast.makeText(AttendanceReport.this, "Please select both dates", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }




    private void showDatePickerDialog(final TextView dateTextView) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AttendanceReport.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        dateTextView.setText(dateFormat.format(calendar.getTime()));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void generateAttendanceReport(String fromDate, String toDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        try {
            Date from = dateFormat.parse(fromDate);
            Date to = dateFormat.parse(toDate);

            DatabaseReference attendanceRef = mDatabase.child("public").child("attendance");
            attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String, Integer> userAttendanceCount = new HashMap<>();

                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userId = userSnapshot.getKey();
                        if (userId != null) {
                            int count = 0;
                            for (DataSnapshot dateSnapshot : userSnapshot.getChildren()) {
                                try {
                                    Date date = dateFormat.parse(dateSnapshot.getKey());
                                    if (date != null && !date.before(from) && !date.after(to)) {
                                        AttendanceModel attendance = dateSnapshot.getValue(AttendanceModel.class);
                                        if (attendance != null && "Present".equals(attendance.getStatus())) {
                                            count++;
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            userAttendanceCount.put(userId, count);
                        }
                    }

                    displayReport(userAttendanceCount);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
        }
    }

    private static final int TOTAL_WORKING_DAYS = 30; // Example: total working days in a month

    /*private void displayReport(Map<String, Integer> userAttendanceCount) {
        LinearLayout reportContainer = findViewById(R.id.reportCo   ntainer);
        reportContainer.removeAllViews(); // Clear previous report

        for (Map.Entry<String, Integer> entry : userAttendanceCount.entrySet()) {
            String userId = entry.getKey();
            int daysPresent = entry.getValue();
            int daysAbsent = TOTAL_WORKING_DAYS - daysPresent;
            String grade = getGrade(daysPresent);

            mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserModel user = dataSnapshot.getValue(UserModel.class);
                    if (user != null) {
                        String username = user.getUsername();

                        // Create a view for each user report entry
                        LinearLayout reportEntry = new LinearLayout(AttendanceReport.this);
                        reportEntry.setOrientation(LinearLayout.VERTICAL);
                        reportEntry.setPadding(0, 0, 0, 16); // Padding between entries
                        reportEntry.setBackgroundTintList(ContextCompat.getColorStateList(AttendanceReport.this, R.color.light_gray));

                        // TextView for Username
                        TextView usernameText = new TextView(AttendanceReport.this);
                        usernameText.setText("Username: " + username);
                        usernameText.setTextSize(20); // Text size 20sp

                        // TextView for Days Present
                        TextView daysPresentText = new TextView(AttendanceReport.this);
                        daysPresentText.setText("Days Present: " + daysPresent);
                        daysPresentText.setTextSize(20); // Text size 20sp

                        // TextView for Days Absent
                        TextView daysAbsentText = new TextView(AttendanceReport.this);
                        daysAbsentText.setText("Days Absent: " + daysAbsent);
                        daysAbsentText.setTextSize(20); // Text size 20sp

                        // TextView for Grade
                        TextView gradeText = new TextView(AttendanceReport.this);
                        gradeText.setText("Grade: " + grade);
                        gradeText.setTextSize(20); // Text size 20sp

                        // Add the text views to the report entry layout
                        reportEntry.addView(usernameText);
                        reportEntry.addView(daysPresentText);
                        reportEntry.addView(daysAbsentText);
                        reportEntry.addView(gradeText);

                        // Add the report entry layout to the report container
                        reportContainer.addView(reportEntry);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(AttendanceReport.this, "Error fetching username", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }*/

    private void displayReport(Map<String, Integer> userAttendanceCount) {
        attendanceList.clear(); // Clear previous report

        for (Map.Entry<String, Integer> entry : userAttendanceCount.entrySet()) {
            String userId = entry.getKey();
            int daysPresent = entry.getValue();
            int daysAbsent = TOTAL_WORKING_DAYS - daysPresent;
            String grade = getGrade(daysPresent);

            mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserModel user = dataSnapshot.getValue(UserModel.class);
                    if (user != null) {
                        String username = user.getUsername();
                        AttendanceReportModel report = new AttendanceReportModel(username, daysPresent, daysAbsent, grade);
                        attendanceList.add(report);
                        attendanceReportAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(AttendanceReport.this, "Error fetching username", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private String getGrade(int daysPresent) {
        if (daysPresent >= 26) {
            return "A";
        } else if (daysPresent >= 20) {
            return "B";
        } else if (daysPresent >= 15) {
            return "C";
        } else if (daysPresent >= 10) {
            return "D";
        } else {
            return "E";
        }
    }




}