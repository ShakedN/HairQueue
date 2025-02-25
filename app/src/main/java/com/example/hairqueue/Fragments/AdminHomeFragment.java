package com.example.hairqueue.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
    private List<AppointmentModel> appointments;
    private AppointmentAdapter appointmentAdapter;
    private Button buttonScheduleManagement;
    private ImageButton profileButton;
    private ImageButton logoutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        // Initialize the RecyclerView and Button
        appointmentsRecyclerView = view.findViewById(R.id.availableAppointmentsRecyclerView);
        buttonScheduleManagement = view.findViewById(R.id.buttonSchedule);
        profileButton = view.findViewById(R.id.profileButton);
        logoutButton = view.findViewById(R.id.logoutButton);

        // Initialize the appointments list
        appointments = new ArrayList<>();

        // Initialize AppointmentAdapter
        appointmentAdapter = new AppointmentAdapter(getContext(), appointments);
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        appointmentsRecyclerView.setAdapter(appointmentAdapter);

        // Handle button click to load appointments
        loadAppointments();
        buttonScheduleManagement.setOnClickListener(v -> {
            // Navigate to Schedule Management
            Navigation.findNavController(v).navigate(R.id.action_adminHomeFragment_to_scheduleManagement);
        });

        profileButton.setOnClickListener(v -> {
             //Navigate to Profile
            Navigation.findNavController(v).navigate(R.id.action_adminHomeFragment_to_adminProfileFragment);
            Toast.makeText(getContext(), "Profile", Toast.LENGTH_SHORT).show();
        });

        logoutButton.setOnClickListener(v -> {
            // Navigate to Login
            Navigation.findNavController(v).navigate(R.id.action_adminHomeFragment_to_loginFragment);
            Toast.makeText(getContext(), "Logout", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void loadAppointments() {
        // Get current date and time
        Calendar now = Calendar.getInstance();
        long currentTimeMillis = now.getTimeInMillis();

        // Fetch all appointments from Firebase
        appointmentAdapter.getAllAppointments(task -> {
            if (task.isSuccessful()) {
                List<AppointmentModel> allAppointments = task.getResult();
                if (allAppointments == null) {
                    allAppointments = new ArrayList<>();
                }

                // Filter upcoming appointments based on status
                List<AppointmentModel> filteredAppointments = new ArrayList<>();
                for (AppointmentModel appointment : allAppointments) {
                    try {
                        // Parse appointment date and time
                        Calendar appointmentTime = Calendar.getInstance();
                        String[] dateParts = appointment.getDate().split("-"); // YYYY-MM-DD
                        String[] timeParts = appointment.getStartTime().split(":"); // HH:MM

                        appointmentTime.set(
                                Integer.parseInt(dateParts[0]),
                                Integer.parseInt(dateParts[1]) - 1,
                                Integer.parseInt(dateParts[2]),
                                Integer.parseInt(timeParts[0]),
                                Integer.parseInt(timeParts[1])
                        );

                        // Check if appointment is in the future AND has the correct status
                        if (appointmentTime.getTimeInMillis() >= currentTimeMillis &&
                                (appointment.getStatus().equalsIgnoreCase("Available") ||
                                        appointment.getStatus().equalsIgnoreCase("Occupied"))) {
                            filteredAppointments.add(appointment);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // Sort appointments by date & time (nearest first)
                filteredAppointments.sort((a1, a2) -> {
                    try {
                        Calendar time1 = Calendar.getInstance();
                        Calendar time2 = Calendar.getInstance();

                        String[] date1Parts = a1.getDate().split("-");
                        String[] time1Parts = a1.getStartTime().split(":");
                        String[] date2Parts = a2.getDate().split("-");
                        String[] time2Parts = a2.getStartTime().split(":");

                        time1.set(
                                Integer.parseInt(date1Parts[0]),
                                Integer.parseInt(date1Parts[1]) - 1,
                                Integer.parseInt(date1Parts[2]),
                                Integer.parseInt(time1Parts[0]),
                                Integer.parseInt(time1Parts[1])
                        );

                        time2.set(
                                Integer.parseInt(date2Parts[0]),
                                Integer.parseInt(date2Parts[1]) - 1,
                                Integer.parseInt(date2Parts[2]),
                                Integer.parseInt(time2Parts[0]),
                                Integer.parseInt(time2Parts[1])
                        );

                        return Long.compare(time1.getTimeInMillis(), time2.getTimeInMillis());

                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }
                });

                // Take only the nearest 5 appointments
                appointments.clear();
                appointments.addAll(filteredAppointments.subList(0, Math.min(5, filteredAppointments.size())));

                // Notify the adapter to update UI
                appointmentAdapter.notifyDataSetChanged();

                //Toast.makeText(getContext(), "Loaded 5 closest available/occupied appointments.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Error loading appointments.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}