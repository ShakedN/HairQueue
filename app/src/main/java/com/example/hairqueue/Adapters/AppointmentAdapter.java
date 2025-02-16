package com.example.hairqueue.Adapters;

import com.example.hairqueue.Models.AppointmentModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class AppointmentAdapter {
    private List<AppointmentModel> appointments;
    private DatabaseReference dbRef;

    public AppointmentAdapter() {
        this.appointments = new ArrayList<>();
        // Initialize Firebase Realtime Database
        dbRef = FirebaseDatabase.getInstance().getReference("dates");
    }

    // Add an appointment to Firebase
    public void addAppointment(String date, AppointmentModel appointment) {
        dbRef.child(date).child("appointments").child(appointment.getAppointmentId())
                .setValue(appointment)
                .addOnSuccessListener(aVoid -> {
                    Log.d("AppointmentAdapter", "Appointment successfully added to Firebase!");
                    appointments.add(appointment); // Add to local list
                })
                .addOnFailureListener(e -> Log.w("AppointmentAdapter", "Error adding appointment", e));
    }

    // Remove an appointment from Firebase
    public void removeAppointment(String date, String appointmentId) {
        dbRef.child(date).child("appointments").child(appointmentId)
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d("AppointmentAdapter", "Appointment successfully deleted from Firebase!");
                    // Remove from local list as well
                    for (int i = 0; i < appointments.size(); i++) {
                        if (appointments.get(i).getAppointmentId().equals(appointmentId)) {
                            appointments.remove(i);
                            break;
                        }
                    }
                })
                .addOnFailureListener(e -> Log.w("AppointmentAdapter", "Error deleting appointment", e));
    }

    // Change the status of an appointment in Firebase
    public void changeStatus(String date, String appointmentId, String newStatus) {
        dbRef.child(date).child("appointments").child(appointmentId)
                .child("status").setValue(newStatus)
                .addOnSuccessListener(aVoid -> {
                    Log.d("AppointmentAdapter", "Appointment status successfully updated in Firebase!");
                    // Update status in local list as well
                    for (AppointmentModel appointment : appointments) {
                        if (appointment.getAppointmentId().equals(appointmentId)) {
                            appointment.setStatus(newStatus);
                            break;
                        }
                    }
                })
                .addOnFailureListener(e -> Log.w("AppointmentAdapter", "Error updating appointment status", e));
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

    public void notifyDataSetChanged() {
        // Implement this method to notify the adapter of data changes
    }
}