package com.example.hairqueue.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairqueue.Adapters.AppointmentAdapter;
import com.example.hairqueue.Models.AppointmentModel;
import com.example.hairqueue.R;

import java.util.ArrayList;
import java.util.List;

public class occupiedAppointmentsFragment extends Fragment {

    private RecyclerView occupiedAppointmentsRecyclerView;
    private List<AppointmentModel> occupiedAppointments;
    private AppointmentAdapter appointmentAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_occupied_appointments, container, false);

        // Initialize the RecyclerView
        occupiedAppointmentsRecyclerView = view.findViewById(R.id.occupiedAppointmentsRecyclerView);
        occupiedAppointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the appointments list
        occupiedAppointments = new ArrayList<>();

        // Initialize AppointmentAdapter
        appointmentAdapter = new AppointmentAdapter(getContext(), occupiedAppointments);
        occupiedAppointmentsRecyclerView.setAdapter(appointmentAdapter);

        // Get the selected date from arguments
        Bundle args = getArguments();
        String selectedDate = args != null ? args.getString("selectedDate") : null;

        if (selectedDate != null) {
            loadOccupiedAppointments(selectedDate);
        } else {
            Toast.makeText(getContext(), "No date selected.", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void loadOccupiedAppointments(String date) {
        appointmentAdapter.getAppointmentsByDate(date, task -> {
            if (task.isSuccessful()) {
                List<AppointmentModel> allAppointments = task.getResult();
                if (allAppointments == null) {
                    allAppointments = new ArrayList<>();
                }

                occupiedAppointments.clear();
                for (AppointmentModel appointment : allAppointments) {
                    if (!"Available".equals(appointment.getStatus())) {
                        occupiedAppointments.add(appointment);
                    }
                }

                // Notify adapter of data changes
                appointmentAdapter.notifyDataSetChanged();
                if (occupiedAppointments.size() == 0) {
                    Toast.makeText(getContext(), "No occupied appointments for " + date, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Loaded occupied appointments for " + date, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Error loading appointments.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}