package com.example.hairqueue.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.hairqueue.R;
import java.util.Calendar;

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

        // Initialize the CalendarView, TextView, and Buttons
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

            // Create a Calendar instance for the selected date and reset time to midnight
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year, month, dayOfMonth, 0, 0, 0);
            selectedCalendar.set(Calendar.MILLISECOND, 0);

            // Get current date as a Calendar instance and reset time to midnight
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            // Check if the selected date is in the past
            if (selectedCalendar.before(today)) {
                // For past dates, hide btnShowConstraints
                btnShowConstraints.setVisibility(View.GONE);
            } else {
                // For today or future dates, show btnShowConstraints
                btnShowConstraints.setVisibility(View.VISIBLE);
            }
            // Always show available and occupied appointment buttons
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
