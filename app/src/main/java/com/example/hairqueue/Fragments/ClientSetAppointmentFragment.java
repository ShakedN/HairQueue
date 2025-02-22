package com.example.hairqueue.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairqueue.Adapters.AppointmentAdapter;
import com.example.hairqueue.Models.AppointmentModel;
import com.example.hairqueue.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ClientSetAppointmentFragment extends Fragment {

    private RecyclerView recyclerView;
    private AppointmentAdapter appointmentAdapter;
    private List<AppointmentModel> appointmentList = new ArrayList<>();
    private TextView noAvailableAppointments;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_set_appointment, container, false);

        //get selected date from the bundle
        Bundle args = getArguments();
        String selectedDate = args != null ? args.getString("selectedDate") : null;

        //show the selected date
        Toast.makeText(getContext(), "Selected Date: " + selectedDate, Toast.LENGTH_SHORT).show();

        //RecyclerView connection
        RecyclerView recyclerView = view.findViewById(R.id.availableAppointmentsPickerRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Addapter creation
        List<AppointmentModel> appointmentList = new ArrayList<>();
        AppointmentAdapter appointmentAdapter = new AppointmentAdapter(getContext(), appointmentList);
        recyclerView.setAdapter(appointmentAdapter);

        TextView tvNoAppointments = view.findViewById(R.id.noAppointmentsTV);

        //load available appiontments from Firebase
        if (selectedDate != null) {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("dates").child(selectedDate);

            //check if the selected date is a work day
            dbRef.child("dateStatus").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String dayStatus = snapshot.getValue(String.class);

                    //if the selected date isn't a work day show a message
                    if (dayStatus == null || !"Work day".equals(dayStatus)) {
                        tvNoAppointments.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        tvNoAppointments.setText("No available appointments on selected date");
                    } else {
                        //if the selected date is a work day show the available appointments
                        AppointmentAdapter adapter = new AppointmentAdapter();
                        adapter.getAvailableAppointmentsByDate(selectedDate, task -> {
                            if (task.isSuccessful()) {
                                List<AppointmentModel> availableAppointments = task.getResult();
                                if (availableAppointments != null && !availableAppointments.isEmpty()) {
                                    appointmentAdapter.updateAppointments(availableAppointments);
                                    tvNoAppointments.setVisibility(View.GONE); //hide message
                                    recyclerView.setVisibility(View.VISIBLE); //show message
                                } else {
                                    tvNoAppointments.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                                    tvNoAppointments.setText("No available appointments on selected date");
                                }
                            } else {
                                Log.e("ClientSetAppointment", "Failed to fetch appointments", task.getException());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ClientSetAppointment", "Error fetching day status", error.toException());
                }
            });
        }

        return view;
    }

}