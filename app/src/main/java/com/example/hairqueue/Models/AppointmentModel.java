package com.example.hairqueue.Models;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AppointmentModel {
    private String adminAddress = "";  // Static variable to store the admin address
    String appointmentId;
    String email;
    String date;
    String service;
    String status;
    String startTime;
    String endTime;

    int duration;

    public AppointmentModel() {
    }
    public AppointmentModel(String appointmentId, String email, String date, String service, String status, String startTime, String endTime, int duration) {
        this.appointmentId = appointmentId;
        this.email = email;
        this.date = date;
        this.service = service;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
    }
    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }


    public void loadAdminAddress(final Runnable onComplete) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference addressRef = usersRef.child("admin").child("address");

        addressRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String address = task.getResult().getValue(String.class);
                if (address != null) {
                    adminAddress = address;  // save the address
                } else {
                    adminAddress = "";  // if there is no address, set it to empty
                }
            } else {
                adminAddress = "";  // if there is an error, set it to empty
            }

            // after the address is loaded, start the onComplete callback
            onComplete.run();
        });
    }

    public String getAddress() {
        return adminAddress;  // return the admin address
    }



}
