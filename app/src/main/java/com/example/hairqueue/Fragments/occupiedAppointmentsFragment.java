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

public class occupiedAppointmentsFragment extends Fragment {

    private ListView occupiedAppointmentsListView;
    private List<AppointmentModel> occupiedAppointments;
    private AppointmentAdapter appointmentAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_occupied_appointments, container, false);

        // Initialize the ListView
        occupiedAppointmentsListView = view.findViewById(R.id.occupiedAppointmentsFragmentListView);
        if (occupiedAppointmentsListView == null) {
            Toast.makeText(getContext(), "ListView not found", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Initialize the appointments list
        occupiedAppointments = new ArrayList<>();

        // Initialize AppointmentAdapter
        appointmentAdapter = new AppointmentAdapter();

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

                // Update ListView with the custom adapter
                AppointmentListAdapter adapter = new AppointmentListAdapter(getContext(), occupiedAppointments);
                occupiedAppointmentsListView.setAdapter(adapter);
                if(occupiedAppointments.size() == 0) {
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