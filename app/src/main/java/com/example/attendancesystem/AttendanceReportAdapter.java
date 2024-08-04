package com.example.attendancesystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AttendanceReportAdapter extends RecyclerView.Adapter<AttendanceReportAdapter.ViewHolder> {
    private Context context;
    private List<AttendanceReportModel> reports;

    public AttendanceReportAdapter(Context context, List<AttendanceReportModel> reports) {
        this.context = context;
        this.reports = reports;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.report_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttendanceReportModel report = reports.get(position);
        holder.usernameText.setText("Username: " + report.getUsername());
        holder.daysPresentText.setText("Days Present: " + report.getDaysPresent());
        holder.daysAbsentText.setText("Days Absent: " + report.getDaysAbsent());
        holder.gradeText.setText("Grade: " + report.getGrade());
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText, daysPresentText, daysAbsentText, gradeText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.usernameText);
            daysPresentText = itemView.findViewById(R.id.daysPresentText);
            daysAbsentText = itemView.findViewById(R.id.daysAbsentText);
            gradeText = itemView.findViewById(R.id.gradeText);
        }
    }
}

