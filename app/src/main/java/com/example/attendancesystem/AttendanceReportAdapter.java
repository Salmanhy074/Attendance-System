package com.example.attendancesystem;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Attendance Options");
                builder.setMessage("Choose an action for " + report.getUsername() + "'s attendance:");

                builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadAttendance(v.getContext(), report);
                    }
                });

                builder.setNegativeButton("Share", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shareAttendance(v.getContext(), report);
                    }
                });

                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void downloadAttendance(Context context, AttendanceReportModel report) {
        String fileName = report.getUsername() + "_attendance.pdf";

        PdfDocument pdfDocument = new PdfDocument();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();

        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(16);

        int tableTop = 25;
        int rowHeight = 30;
        int columnWidth = 250;

        paint.setTextSize(18);
        canvas.drawText("Attendance Report", 20, tableTop, paint);
        canvas.drawLine(20, tableTop + 10, 575, tableTop + 10, paint);

        paint.setTextSize(16);
        int y = tableTop + 40;
        String[] headers = {"Username:", "Days Present:", "Days Absent:", "Grade:"};
        String[] values = {
                report.getUsername(),
                String.valueOf(report.getDaysPresent()),
                String.valueOf(report.getDaysAbsent()),
                report.getGrade()
        };

        for (int i = 0; i < headers.length; i++) {
            canvas.drawText(headers[i], 20, y, paint);
            canvas.drawText(values[i], 150, y, paint);
            y += rowHeight;
        }

        pdfDocument.finishPage(page);

        File pdfFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);

        try {
            FileOutputStream fos = new FileOutputStream(pdfFile);
            pdfDocument.writeTo(fos);
            fos.close();
            pdfDocument.close();

            Toast.makeText(context, "Attendance downloaded as PDF: " + fileName, Toast.LENGTH_SHORT).show();

            viewPdf(context, pdfFile);

        } catch (Exception e) {
            Toast.makeText(context, "Failed to download attendance", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void viewPdf(Context context, File pdfFile) {
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", pdfFile);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No application available to view PDF", Toast.LENGTH_SHORT).show();
        }
    }


    private void shareAttendance(Context context, AttendanceReportModel report) {
        String fileName = report.getUsername() + "_attendance.pdf";

        PdfDocument pdfDocument = new PdfDocument();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();

        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(16);

        int tableTop = 25;
        int rowHeight = 30;
        int columnWidth = 250;

        paint.setTextSize(18);
        canvas.drawText("Attendance Report", 20, tableTop, paint);
        canvas.drawLine(20, tableTop + 10, 575, tableTop + 10, paint);

        paint.setTextSize(16);
        int y = tableTop + 40;
        String[] headers = {"Username:", "Days Present:", "Days Absent:", "Grade:"};
        String[] values = {
                report.getUsername(),
                String.valueOf(report.getDaysPresent()),
                String.valueOf(report.getDaysAbsent()),
                report.getGrade()
        };

        for (int i = 0; i < headers.length; i++) {
            canvas.drawText(headers[i], 20, y, paint);
            canvas.drawText(values[i], 150, y, paint);
            y += rowHeight;
        }

        pdfDocument.finishPage(page);
        File pdfFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);

        try {
            FileOutputStream fos = new FileOutputStream(pdfFile);
            pdfDocument.writeTo(fos);
            fos.close();
            pdfDocument.close();

            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", pdfFile);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, report.getUsername() + "'s Attendance Report");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivity(Intent.createChooser(shareIntent, "Share via"));
        } catch (Exception e) {
            Toast.makeText(context, "Failed to share attendance", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
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

