package com.example.attendancesystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {
    private List<AttendanceModel> attendanceList;
    Context context;

    public AttendanceAdapter(Context context, List<AttendanceModel> attendanceList) {
        this.context = context;
        this.attendanceList = attendanceList;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.reccyclerviewdesign, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        AttendanceModel attendance = attendanceList.get(position);
        holder.usernameTextView.setText(attendance.getUsername());
        holder.emailTextView.setText(attendance.getEmail());
        holder.dateTextView.setText(attendance.getDate());
        holder.statusTextView.setText(attendance.getStatus());
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }

    public static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, emailTextView, dateTextView, statusTextView;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username);
            emailTextView = itemView.findViewById(R.id.email);
            dateTextView = itemView.findViewById(R.id.date);
            statusTextView = itemView.findViewById(R.id.status);
        }
    }
}

