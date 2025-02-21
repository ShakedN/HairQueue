package com.example.hairqueue.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairqueue.Adapters.AppointmentAdapter;
import com.example.hairqueue.Models.AppointmentModel;
import com.example.hairqueue.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdminHomeFragment extends Fragment {

    private RecyclerView appointmentsRecyclerView;
    private Button loadAppointmentsButton;
    private List<AppointmentModel> appointments;
    private AppointmentAdapter appointmentAdapter;
    private Button buttonScheduleManagement;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        // Initialize the RecyclerView and Button
        appointmentsRecyclerView = view.findViewById(R.id.availableAppointmentsRecyclerView);
        loadAppointmentsButton = view.findViewById(R.id.loadAppointmentsButton);
        buttonScheduleManagement = view.findViewById(R.id.buttonSchedule);

        // Initialize the appointments list
        appointments = new ArrayList<>();

        // Initialize AppointmentAdapter
        appointmentAdapter = new AppointmentAdapter(getContext(), appointments);
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        appointmentsRecyclerView.setAdapter(appointmentAdapter);

        // Handle button click to load appointments
        loadAppointmentsButton.setOnClickListener(v -> loadAppointments());
        buttonScheduleManagement.setOnClickListener(v -> {
            // Navigate to Schedule Management
            Navigation.findNavController(v).navigate(R.id.action_adminHomeFragment_to_scheduleManagement);
        });

        return view;
    }

    private void loadAppointments() {
        // Get current time
        Calendar now = Calendar.getInstance();

        // Fetch all appointments
        appointmentAdapter.getAllAppointments(task -> {
            if (task.isSuccessful()) {
                List<AppointmentModel> allAppointments = task.getResult();
                if (allAppointments == null) {
                    allAppointments = new ArrayList<>();
                }

                // Sort appointments by closest to current time
                allAppointments.sort((a1, a2) -> {
                    try {
                        Calendar dateTime1 = Calendar.getInstance();
                        Calendar dateTime2 = Calendar.getInstance();

                        String[] date1Parts = a1.getDate().split("-");
                        String[] time1Parts = a1.getStartTime().split(":");
                        String[] date2Parts = a2.getDate().split("-");
                        String[] time2Parts = a2.getStartTime().split(":");

                        dateTime1.set(Integer.parseInt(date1Parts[0]), Integer.parseInt(date1Parts[1]) - 1, Integer.parseInt(date1Parts[2]),
                                Integer.parseInt(time1Parts[0]), Integer.parseInt(time1Parts[1]));

                        dateTime2.set(Integer.parseInt(date2Parts[0]), Integer.parseInt(date2Parts[1]) - 1, Integer.parseInt(date2Parts[2]),
                                Integer.parseInt(time2Parts[0]), Integer.parseInt(time2Parts[1]));

                        // Compare dates and times
                        return Long.compare(Math.abs(dateTime1.getTimeInMillis() - now.getTimeInMillis()),
                                Math.abs(dateTime2.getTimeInMillis() - now.getTimeInMillis()));

                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }
                });

                // Take only the closest 5 appointments
                appointments.clear();
                appointments.addAll(allAppointments.subList(0, Math.min(5, allAppointments.size())));

                // Notify adapter of data changes
                appointmentAdapter.notifyDataSetChanged();

                Toast.makeText(getContext(), "Loaded 5 closest appointments.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Error loading appointments.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}