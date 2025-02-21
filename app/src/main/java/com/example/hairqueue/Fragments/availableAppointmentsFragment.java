package com.example.hairqueue.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairqueue.Adapters.AppointmentAdapter;
import com.example.hairqueue.Models.AppointmentModel;
import com.example.hairqueue.R;

import java.util.ArrayList;
import java.util.List;

public class availableAppointmentsFragment extends Fragment {

    private RecyclerView availableAppointmentsRecyclerView;
    private List<AppointmentModel> availableAppointments;
    private AppointmentAdapter appointmentAdapter;
    private TextView noAvailableAppointments;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_available_appointments, container, false);

        // Initialize the RecyclerView
        availableAppointmentsRecyclerView = view.findViewById(R.id.availableAppointmentsRecyclerView);
        availableAppointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the appointments list
        availableAppointments = new ArrayList<>();
        noAvailableAppointments= view.findViewById(R.id.emptyView);
        // Initialize AppointmentAdapter
        appointmentAdapter = new AppointmentAdapter(getContext(), availableAppointments);
        availableAppointmentsRecyclerView.setAdapter(appointmentAdapter);

        // Get the selected date from arguments
        Bundle args = getArguments();
        String selectedDate = args != null ? args.getString("selectedDate") : null;

        if (selectedDate != null) {
            loadAvailableAppointments(selectedDate, noAvailableAppointments);
        } else {
            Toast.makeText(getContext(), "No date selected.", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void loadAvailableAppointments(String date, View emptyView) {
        appointmentAdapter.getAppointmentsByDate(date, task -> {
            if (task.isSuccessful()) {
                List<AppointmentModel> allAppointments = task.getResult();
                if (allAppointments == null) {
                    allAppointments = new ArrayList<>();
                }

                availableAppointments.clear();
                for (AppointmentModel appointment : allAppointments) {
                    if ("Available".equals(appointment.getStatus())) {
                        availableAppointments.add(appointment);
                    }
                }

                // Notify adapter of data changes
                appointmentAdapter.notifyDataSetChanged();
                if (availableAppointments.size() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "No available appointments for " + date, Toast.LENGTH_SHORT).show();
                    noAvailableAppointments.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(getContext(), "Loaded available appointments for " + date, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Error loading appointments.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}