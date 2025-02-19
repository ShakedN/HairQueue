package com.example.hairqueue.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import com.example.hairqueue.R;

public class ScheduleManagement extends Fragment {

    private CalendarView calendarView;
    private TextView selectedDateTextView;
    private Button btnShowConstraints;
    private Button btnShowAvailableAppointments;
    private Button btnShowOccupiedAppointments;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule_management, container, false);

        // Initialize the CalendarView and TextView
        calendarView = view.findViewById(R.id.calendarView);
        selectedDateTextView = view.findViewById(R.id.selectedDateTextView);
        btnShowConstraints = view.findViewById(R.id.btnShowConstraints);
        btnShowAvailableAppointments = view.findViewById(R.id.btnShowAvailableAppointments);
        btnShowOccupiedAppointments = view.findViewById(R.id.btnShowOccupiedAppointments);

        // Set the OnDateChangeListener to handle date selection
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            // Format the selected date
            String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;

            // Display the selected date in the TextView
            selectedDateTextView.setText("Selected Date: " + selectedDate);
            selectedDateTextView.setVisibility(View.VISIBLE);

            // Show the buttons
            btnShowConstraints.setVisibility(View.VISIBLE);
            btnShowAvailableAppointments.setVisibility(View.VISIBLE);
            btnShowOccupiedAppointments.setVisibility(View.VISIBLE);

            // Set click listeners for the buttons
            btnShowConstraints.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("selectedDate", selectedDate);
                Navigation.findNavController(v).navigate(R.id.action_scheduleManagement_to_adminFragmentConstraints, bundle);
            });

            btnShowAvailableAppointments.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("selectedDate", selectedDate);

                Navigation.findNavController(v).navigate(R.id.action_scheduleManagement_to_availableAppointmentsFragment, bundle);
            });

            btnShowOccupiedAppointments.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("selectedDate", selectedDate);
                Navigation.findNavController(v).navigate(R.id.action_scheduleManagement_to_occupiedAppointmentsFragment, bundle);
            });
        });

        return view;
    }
}