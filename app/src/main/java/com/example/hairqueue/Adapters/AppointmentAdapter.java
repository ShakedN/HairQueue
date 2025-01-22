package com.example.hairqueue.Adapters;

import com.example.hairqueue.Models.AppointmentModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import  com.google.android.gms.tasks.Task;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class AppointmentAdapter {
    private List<AppointmentModel> appointments;
    private FirebaseFirestore db;

    public AppointmentAdapter() {
        this.appointments = new ArrayList<>();
        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
    }

    // Add an appointment to Firebase
    public void addAppointment(AppointmentModel appointment) {
        db.collection("appointments").document(appointment.getAppointmentId())
                .set(appointment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("AppointmentAdapter", "Appointment successfully added to Firebase!");
                        // Add to local list as well
                        appointments.add(appointment);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("AppointmentAdapter", "Error adding appointment", e);
                    }
                });
    }

    // Remove an appointment from Firebase
    public void removeAppointment(String appointmentId) {
        db.collection("appointments").document(appointmentId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("AppointmentAdapter", "Appointment successfully deleted from Firebase!");
                        // Remove from local list as well
                        for (int i = 0; i < appointments.size(); i++) {
                            if (appointments.get(i).getAppointmentId().equals(appointmentId)) {
                                appointments.remove(i);
                                break;
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("AppointmentAdapter", "Error deleting appointment", e);
                    }
                });
    }

    // Change the status of an appointment in Firebase
    public void changeStatus(String appointmentId, String newStatus) {
        db.collection("appointments").document(appointmentId)
                .update("status", newStatus)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("AppointmentAdapter", "Appointment status successfully updated in Firebase!");
                        // Update status in local list as well
                        for (AppointmentModel appointment : appointments) {
                            if (appointment.getAppointmentId().equals(appointmentId)) {
                                appointment.setStatus(newStatus);
                                break;
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("AppointmentAdapter", "Error updating appointment status", e);
                    }
                });
    }

    // Get all appointments from Firebase
    public void getAppointments() {
        db.collection("appointments")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        appointments.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            AppointmentModel appointment = document.toObject(AppointmentModel.class);
                            appointments.add(appointment);
                        }
                        Log.d("AppointmentAdapter", "Appointments successfully fetched from Firebase!");
                    } else {
                        Log.w("AppointmentAdapter", "Error getting appointments.", task.getException());
                    }
                });
    }

    // Get all appointments (local)
    public void getAppointmentsByDate(String date, final OnCompleteListener<List<AppointmentModel>> listener) {
        List<AppointmentModel> dateAppointments = new ArrayList<>();
        db.collection("appointments")
                .whereEqualTo("date", date)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            AppointmentModel appointment = document.toObject(AppointmentModel.class);
                            dateAppointments.add(appointment);
                        }
                        listener.onComplete(Tasks.forResult(dateAppointments));
                        Log.d("AppointmentAdapter", "Appointments for the date successfully fetched from Firebase!");
                    } else {
                        listener.onComplete(Tasks.forException(task.getException()));
                        Log.w("AppointmentAdapter", "Error getting appointments for the date.", task.getException());
                    }
                });
    }

    public void notifyDataSetChanged() {
    }
}
