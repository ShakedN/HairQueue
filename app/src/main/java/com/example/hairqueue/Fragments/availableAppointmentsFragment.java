package com.example.hairqueue.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.hairqueue.Adapters.AppointmentListAdapter;
import com.example.hairqueue.Models.AppointmentModel;
import com.example.hairqueue.R;
import com.example.hairqueue.Adapters.AppointmentAdapter;

import java.util.ArrayList;
import java.util.List;

public class availableAppointmentsFragment extends Fragment {

    private ListView availableAppointmentsListView;
    private List<AppointmentModel> availableAppointments;
    private AppointmentAdapter appointmentAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_available_appointments, container, false);

        // Initialize the ListView
        availableAppointmentsListView = view.findViewById(R.id.availableAppointmentsListView);
        if (availableAppointmentsListView == null) {
            Toast.makeText(getContext(), "ListView not found", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Initialize the appointments list
        availableAppointments = new ArrayList<>();

        // Initialize AppointmentAdapter
       // appointmentAdapter = new AppointmentAdapter();

        // Get the selected date from arguments
        Bundle args = getArguments();
        String selectedDate = args != null ? args.getString("selectedDate") : null;

        if (selectedDate != null) {
            loadAvailableAppointments(selectedDate);
        } else {
            Toast.makeText(getContext(), "No date selected.", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void loadAvailableAppointments(String date) {
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

                // Update ListView with the custom adapter
                AppointmentListAdapter adapter = new AppointmentListAdapter(getContext(), availableAppointments);
                availableAppointmentsListView.setAdapter(adapter);
                if(availableAppointments.size() == 0) {
                    Toast.makeText(getContext(), "No available appointments for " + date, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Loaded available appointments for " + date, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Error loading appointments.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}