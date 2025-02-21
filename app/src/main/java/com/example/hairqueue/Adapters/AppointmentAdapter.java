package com.example.hairqueue.Adapters;

import com.example.hairqueue.Models.AppointmentModel;
import com.example.hairqueue.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {
    private List<AppointmentModel> appointments;
    private Context context;
    private DatabaseReference dbRef;

    public AppointmentAdapter(Context context, List<AppointmentModel> appointments) {
        this.context = context;
        this.appointments = appointments;
        // Initialize Firebase Realtime Database
        dbRef = FirebaseDatabase.getInstance().getReference("dates");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.appointment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppointmentModel appointment = appointments.get(position);

        holder.tvService.setText("Haircut"); // אם יש שירותים שונים, אפשר לשנות בהתאם
        holder.tvDate.setText("Date: " + appointment.getDate());
        holder.tvStartTime.setText("Start: " + appointment.getStartTime());
        holder.tvEndTime.setText("End: " + appointment.getEndTime());
        holder.tvDuration.setText("Duration: " + appointment.getDuration() + " mins");
        holder.tvStatus.setText("Status: " + appointment.getStatus());

        if ("Available".equals(appointment.getStatus())) {
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
        } else {
            holder.tvStatus.setTextColor(ContextCompat.getColor(context,android.R.color.holo_red_dark ));
        }

        holder.tvAppointmentId.setText(appointment.getAppointmentId());
        holder.tvEmail.setText("email@example.com"); // אם יש לך אימייל רלוונטי
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvService, tvDate, tvStartTime, tvEndTime, tvDuration, tvStatus, tvAppointmentId, tvEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvService = itemView.findViewById(R.id.tvService);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStartTime = itemView.findViewById(R.id.tvStartTime);
            tvEndTime = itemView.findViewById(R.id.tvEndTime);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvAppointmentId = itemView.findViewById(R.id.tvAppointmentId);
            tvEmail = itemView.findViewById(R.id.tvEmail);
        }
    }

    public void updateAppointments(List<AppointmentModel> newAppointments) {
        this.appointments.clear();
        this.appointments.addAll(newAppointments);
        notifyDataSetChanged();
    }

    // Get all appointments for a specific date from Firebase
    public void getAppointmentsByDate(String date, final OnCompleteListener<List<AppointmentModel>> listener) {
        List<AppointmentModel> dateAppointments = new ArrayList<>();
        dbRef.child(date).child("appointments")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot appointmentSnapshot : snapshot.getChildren()) {
                            AppointmentModel appointment = appointmentSnapshot.getValue(AppointmentModel.class);
                            dateAppointments.add(appointment);
                        }
                        listener.onComplete(Tasks.forResult(dateAppointments));
                        Log.d("AppointmentAdapter", "Appointments for the date successfully fetched from Firebase!");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onComplete(Tasks.forException(error.toException()));
                        Log.w("AppointmentAdapter", "Error getting appointments for the date.", error.toException());
                    }
                });
    }

    public void getAllAppointments(final OnCompleteListener<List<AppointmentModel>> listener) {
        List<AppointmentModel> allAppointments = new ArrayList<>();
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot appointmentSnapshot : dateSnapshot.child("appointments").getChildren()) {
                        AppointmentModel appointment = appointmentSnapshot.getValue(AppointmentModel.class);
                        allAppointments.add(appointment);
                    }
                }
                listener.onComplete(Tasks.forResult(allAppointments));
                Log.d("AppointmentAdapter", "All appointments successfully fetched from Firebase!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onComplete(Tasks.forException(error.toException()));
                Log.w("AppointmentAdapter", "Error getting all appointments.", error.toException());
            }
        });
    }
}