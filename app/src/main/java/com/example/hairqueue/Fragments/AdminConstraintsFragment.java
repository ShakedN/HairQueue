// AdminConstraintsFragment.java
package com.example.hairqueue.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;

import com.example.hairqueue.Models.AppointmentModel;
import com.example.hairqueue.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class AdminConstraintsFragment extends Fragment {

    private LinearLayout workDayOptionsLayout;
    private Button saveConstraintsButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_constraints, container, false);

        // Get the selected date from the arguments
        Bundle args = getArguments();
        String selectedDate = args != null ? args.getString("selectedDate") : "No date selected";

        // Display the selected date in the TextView
        TextView selectedDateTextView = view.findViewById(R.id.selectedDateTextView);
        selectedDateTextView.setText("Selected Date: " + selectedDate);

        // Initialize the work day options layout
        workDayOptionsLayout = view.findViewById(R.id.workDayOptionsLayout);

        // Set up the RadioGroup listener
        RadioGroup dayTypeRadioGroup = view.findViewById(R.id.dayTypeRadioGroup);
        dayTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.workDayRadioButton) {
                    workDayOptionsLayout.setVisibility(View.VISIBLE);
                } else {
                    workDayOptionsLayout.setVisibility(View.GONE);
                }
            }
        });
        saveConstraintsButton = view.findViewById(R.id.saveButton);
        saveConstraintsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveConstraints(v);
            }
        });

        return view;
    }
    private void saveConstraints(View v) {
        View view = getView();
        if (view == null) return;

        // TimePickers and TextView
        TimePicker startTimePicker = view.findViewById(R.id.startHourPicker);
        TimePicker endTimePicker = view.findViewById(R.id.endHourPicker);
        TimePicker breakStartTimePicker = view.findViewById(R.id.lunchBreakStartPicker);
        TimePicker breakEndTimePicker = view.findViewById(R.id.lunchBreakEndPicker);
        TimePicker constraintStartHourPicker = view.findViewById(R.id.constraintStartHourPicker);
        TimePicker constraintEndHourPicker = view.findViewById(R.id.constraintEndHourPicker);
        TextView selectedDateTextView = view.findViewById(R.id.selectedDateTextView);

        if (startTimePicker == null || endTimePicker == null || breakStartTimePicker == null ||
                breakEndTimePicker == null || constraintStartHourPicker == null ||
                constraintEndHourPicker == null || selectedDateTextView == null) {
            Log.e("AdminConstraintsFragment", "One or more views are null");
            return;
        }

        // Get values from TimePickers
        int startHour = startTimePicker.getHour();
        int startMinute = startTimePicker.getMinute();
        int endHour = endTimePicker.getHour();
        int endMinute = endTimePicker.getMinute();
        int breakStartHour = breakStartTimePicker.getHour();
        int breakStartMinute = breakStartTimePicker.getMinute();
        int breakEndHour = breakEndTimePicker.getHour();
        int breakEndMinute = breakEndTimePicker.getMinute();
        int constraintStartHour = constraintStartHourPicker.getHour();
        int constraintStartMinute = constraintStartHourPicker.getMinute();
        int constraintEndHour = constraintEndHourPicker.getHour();
        int constraintEndMinute = constraintEndHourPicker.getMinute();

        // Extract and clean the selected date
        String selectedDate = selectedDateTextView.getText().toString().replace("Selected Date: ", "").trim();

        // Firebase setup
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("appointments");

        // Generate appointments
        StringBuilder schedule = new StringBuilder();

        int currentHour = startHour;
        int currentMinute = startMinute;

        // Convert times to "minutes from midnight" for easier comparison
        int breakStartTotalMinutes = breakStartHour * 60 + breakStartMinute;
        int breakEndTotalMinutes = breakEndHour * 60 + breakEndMinute;
        int constraintStartTotalMinutes = constraintStartHour * 60 + constraintStartMinute;
        int constraintEndTotalMinutes = constraintEndHour * 60 + constraintEndMinute;

        while (currentHour < endHour || (currentHour == endHour && currentMinute < endMinute)) {
            // Convert current time to "minutes from midnight"
            int currentTotalMinutes = currentHour * 60 + currentMinute;
            int nextTotalMinutes = currentTotalMinutes + 30; // Adding 30 minutes for the appointment

            // Check if the current time overlaps with the break period
            if (currentTotalMinutes >= breakStartTotalMinutes && currentTotalMinutes < breakEndTotalMinutes) {
                currentMinute += 30;
                if (currentMinute >= 60) {
                    currentMinute %= 60;
                    currentHour++;
                }
                continue; // Skip the break period
            }

            // Check if the current time overlaps with the constraint period
            if (currentTotalMinutes >= constraintStartTotalMinutes && currentTotalMinutes < constraintEndTotalMinutes) {
                currentMinute += 30;
                if (currentMinute >= 60) {
                    currentMinute %= 60;
                    currentHour++;
                }
                continue; // Skip the constraint period
            }

            // Generate appointment times
            String startTime = String.format("%02d:%02d", currentHour, currentMinute);
            int nextHour = nextTotalMinutes / 60;
            int nextMinute = nextTotalMinutes % 60;
            String endTime = String.format("%02d:%02d", nextHour, nextMinute);

            // Create and save appointment
            AppointmentModel appointment = new AppointmentModel(UUID.randomUUID().toString(), null, selectedDate, null, "Available", startTime, endTime, 30);
            ref.child(appointment.getAppointmentId()).setValue(appointment)
                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "Appointment saved: " + startTime + " - " + endTime))
                    .addOnFailureListener(e -> Log.e("Firebase", "Error saving appointment", e));

            // Update schedule for display
            schedule.append(startTime).append(" - ").append(endTime).append("\n");

            // Increment time by 30 minutes
            currentMinute += 30;
            if (currentMinute >= 60) {
                currentMinute %= 60;
                currentHour++;
            }
        }

        // Update the TextView with the schedule (for display only)
        selectedDateTextView.setText(schedule.toString());
    }

}