package com.example.hairqueue.Fragments;

import android.content.pm.CapabilityParams;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.hairqueue.Models.AppointmentModel;
import com.example.hairqueue.Models.DateModel;
import com.example.hairqueue.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminConstraintsFragment extends Fragment {

    private LinearLayout workDayOptionsLayout;
    private Button saveConstraintsButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_constraints, container, false);

        // Get the selected date from arguments
        Bundle args = getArguments();
        String selectedDate = args != null ? args.getString("selectedDate") : "No date selected";

        // Display the selected date
        TextView selectedDateTextView = view.findViewById(R.id.selectedDateTextView);
        selectedDateTextView.setText("Selected Date: " + selectedDate);


        // Initialize work day options
        workDayOptionsLayout = view.findViewById(R.id.workDayOptionsLayout);

        // Set up RadioGroup listener
        RadioGroup dayTypeRadioGroup = view.findViewById(R.id.dayTypeRadioGroup);
        dayTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.workDayRadioButton) {
                workDayOptionsLayout.setVisibility(View.VISIBLE);
            } else {
                workDayOptionsLayout.setVisibility(View.GONE);
            }
        });

        // Initialize NumberPickers
        NumberPicker startHourPicker = view.findViewById(R.id.startHourPicker);
        NumberPicker startMinutePicker = view.findViewById(R.id.startMinutePicker);
        NumberPicker endHourPicker = view.findViewById(R.id.endHourPicker);
        NumberPicker endMinutePicker = view.findViewById(R.id.endMinutePicker);
        NumberPicker lunchStartHourPicker = view.findViewById(R.id.lunchBreakStartPicker);
        NumberPicker lunchStartMinutePicker = view.findViewById(R.id.lunchBreakStartMinutePicker);
        NumberPicker lunchEndHourPicker = view.findViewById(R.id.lunchBreakEndPicker);
        NumberPicker lunchEndMinutePicker = view.findViewById(R.id.lunchBreakEndMinutePicker);
        NumberPicker constraintStartHourPicker = view.findViewById(R.id.constraintStartHourPicker);
        NumberPicker constraintStartMinutePicker = view.findViewById(R.id.constraintStartMinutePicker);
        NumberPicker constraintEndHourPicker = view.findViewById(R.id.constraintEndHourPicker);
        NumberPicker constraintEndMinutePicker = view.findViewById(R.id.constraintEndMinutePicker);

        // Setup NumberPickers
        setupHourPicker(startHourPicker);
        setupMinutePicker(startMinutePicker);
        setupHourPicker(endHourPicker);
        setupMinutePicker(endMinutePicker);
        setupHourPicker(lunchStartHourPicker);
        setupMinutePicker(lunchStartMinutePicker);
        setupHourPicker(lunchEndHourPicker);
        setupMinutePicker(lunchEndMinutePicker);
        setupHourPickerConstrains(constraintStartHourPicker);
        setupMinutePickerConstrains(constraintStartMinutePicker);
        setupHourPickerConstrains(constraintEndHourPicker);
        setupMinutePickerConstrains(constraintEndMinutePicker);

        // Save button
        saveConstraintsButton = view.findViewById(R.id.saveButton);
        saveConstraintsButton.setOnClickListener(v -> saveConstraints(
                startHourPicker, startMinutePicker, endHourPicker, endMinutePicker,
                lunchStartHourPicker, lunchStartMinutePicker, lunchEndHourPicker, lunchEndMinutePicker,
                constraintStartHourPicker, constraintStartMinutePicker, constraintEndHourPicker, constraintEndMinutePicker,
                selectedDateTextView
        ));

        return view;
    }

    private void setupHourPicker(NumberPicker hourPicker) {
        hourPicker.setMinValue(6);
        hourPicker.setMaxValue(23);
        hourPicker.setWrapSelectorWheel(true);
    }
    private void setupHourPickerConstrains(NumberPicker hourPicker) {
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(18);
        hourPicker.setDisplayedValues(new String[]{ "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "None"});
        hourPicker.setWrapSelectorWheel(true);
    }
    private void setupMinutePickerConstrains(NumberPicker minutePicker) {
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(2);
        minutePicker.setDisplayedValues(new String[]{"00", "30", "None"}); // "None" option for null constraints
        minutePicker.setWrapSelectorWheel(true);
    }


    private void setupMinutePicker(NumberPicker minutePicker) {
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(1);
        minutePicker.setDisplayedValues(new String[]{"00", "30"}); // "None" option for null constraints
        minutePicker.setWrapSelectorWheel(true);
    }


    private void saveConstraints(
            NumberPicker startHourPicker, NumberPicker startMinutePicker,
            NumberPicker endHourPicker, NumberPicker endMinutePicker,
            NumberPicker lunchStartHourPicker, NumberPicker lunchStartMinutePicker,
            NumberPicker lunchEndHourPicker, NumberPicker lunchEndMinutePicker,
            NumberPicker constraintStartHourPicker, NumberPicker constraintStartMinutePicker,
            NumberPicker constraintEndHourPicker, NumberPicker constraintEndMinutePicker,
            TextView selectedDateTextView) {

        // Get regular working hours
        int startHour = startHourPicker.getValue();
        int startMinute = startMinutePicker.getValue() * 30;
        int endHour = endHourPicker.getValue();
        int endMinute = endMinutePicker.getValue() * 30;

        // Get lunch break times
        int lunchStartHour = lunchStartHourPicker.getValue();
        int lunchStartMinute = lunchStartMinutePicker.getValue() * 30;
        int lunchEndHour = lunchEndHourPicker.getValue();
        int lunchEndMinute = lunchEndMinutePicker.getValue() * 30;

        int lunchStartTotalMinutes = (lunchStartHour * 60) + lunchStartMinute;
        int lunchEndTotalMinutes = (lunchEndHour * 60) + lunchEndMinute;

        // Get constraint times with proper conversion
        int constraintStartHour = getActualHourFromConstraintPicker(constraintStartHourPicker);
        int constraintEndHour = getActualHourFromConstraintPicker(constraintEndHourPicker);

        // Calculate total minutes for constraints
        int constraintStartTotalMinutes = -1;
        int constraintEndTotalMinutes = -1;

        if (constraintStartHour != -1 && constraintStartMinutePicker.getValue() != 2) {
            constraintStartTotalMinutes = (constraintStartHour * 60) +
                    (constraintStartMinutePicker.getValue() * 30);
        }

        if (constraintEndHour != -1 && constraintEndMinutePicker.getValue() != 2) {
            constraintEndTotalMinutes = (constraintEndHour * 60) +
                    (constraintEndMinutePicker.getValue() * 30);
        }

        // Debug logging
        Log.d("Time Debug", String.format("Working Hours: %02d:%02d - %02d:%02d",
                startHour, startMinute, endHour, endMinute));
        Log.d("Time Debug", String.format("Lunch Break: %02d:%02d - %02d:%02d",
                lunchStartHour, lunchStartMinute, lunchEndHour, lunchEndMinute));
        Log.d("Time Debug", String.format("Constraints: %02d:%02d - %02d:%02d",
                constraintStartHour, constraintStartMinutePicker.getValue() * 30,
                constraintEndHour, constraintEndMinutePicker.getValue() * 30));

        // Validate time selections
        if (startHour > endHour || (startHour == endHour && startMinute >= endMinute)) {
            Toast.makeText(getContext(), "Invalid working hours! Start time must be before end time.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (lunchStartTotalMinutes >= lunchEndTotalMinutes) {
            Toast.makeText(getContext(), "Invalid lunch break! Start must be before end time.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (constraintStartTotalMinutes != -1 && constraintEndTotalMinutes != -1 &&
                constraintStartTotalMinutes >= constraintEndTotalMinutes) {
            Toast.makeText(getContext(), "Invalid constraint times! Start must be before end time.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Extract selected date
        String selectedDate = selectedDateTextView.getText().toString()
                .replace("Selected Date: ", "").trim();

        // Firebase setup
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("dates");

        StringBuilder schedule = new StringBuilder();
        List<AppointmentModel> appointments = new ArrayList<>();

        // Convert working hours to total minutes
        int currentTotalMinutes = (startHour * 60) + startMinute;
        int totalEndMinutes = (endHour * 60) + endMinute;

        while (currentTotalMinutes < totalEndMinutes) {
            int nextTotalMinutes = currentTotalMinutes + 30;  // 30-minute appointments

            // Check for lunch break overlap
            boolean isLunchTime = false;
            if (lunchStartTotalMinutes != -1 && lunchEndTotalMinutes != -1) {
                isLunchTime = (currentTotalMinutes >= lunchStartTotalMinutes &&
                        currentTotalMinutes < lunchEndTotalMinutes) ||
                        (lunchStartTotalMinutes >= currentTotalMinutes &&
                                lunchStartTotalMinutes < nextTotalMinutes);
            }

            // Check for constraint time overlap
            boolean isConstrainedTime = false;
            if (constraintStartTotalMinutes != -1 && constraintEndTotalMinutes != -1) {
                isConstrainedTime = (currentTotalMinutes >= constraintStartTotalMinutes &&
                        currentTotalMinutes < constraintEndTotalMinutes) ||
                        (constraintStartTotalMinutes >= currentTotalMinutes &&
                                constraintStartTotalMinutes < nextTotalMinutes);
            }

            // Debug logging for time slot checking
            Log.d("Slot Debug", String.format("Checking slot %02d:%02d - %02d:%02d",
                    currentTotalMinutes / 60, currentTotalMinutes % 60,
                    nextTotalMinutes / 60, nextTotalMinutes % 60));
            Log.d("Slot Debug", "isLunchTime: " + isLunchTime + ", isConstrainedTime: " + isConstrainedTime);

            // Skip if time slot is during lunch or constrained time
            if (isLunchTime || isConstrainedTime) {
                currentTotalMinutes += 30;
                continue;
            }

            // Stop if next appointment would exceed working hours
            if (nextTotalMinutes > totalEndMinutes) {
                break;
            }

            // Convert current time slot to hour:minute format
            int currentHour = currentTotalMinutes / 60;
            int currentMinute = currentTotalMinutes % 60;
            int nextHour = nextTotalMinutes / 60;
            int nextMinute = nextTotalMinutes % 60;

            String startTime = String.format("%02d:%02d", currentHour, currentMinute);
            String endTime = String.format("%02d:%02d", nextHour, nextMinute);

            // Create and add appointment
            AppointmentModel appointment = new AppointmentModel(
                    UUID.randomUUID().toString(),
                    selectedDate,
                    30,
                    endTime,
                    startTime,
                    "Available"

            );
            appointments.add(appointment);

            // Add to schedule display
            schedule.append(startTime).append(" - ").append(endTime).append("\n");

            // Move to next slot
            currentTotalMinutes += 30;
        }

        // Create date model and save to Firebase
        DateModel dateModel = new DateModel(selectedDate, appointments);
        dbRef.child(selectedDate).setValue(dateModel)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Appointments saved for date: " + selectedDate);
                    Toast.makeText(getContext(), "Schedule saved successfully!", Toast.LENGTH_SHORT).show();
                    // Update the display
                    selectedDateTextView.setText("Selected Date: " + selectedDate + "\n\n" + schedule.toString());
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Error saving appointments", e);
                    Toast.makeText(getContext(), "Error saving schedule", Toast.LENGTH_SHORT).show();
                });
    }

    // Helper method to convert picker value to actual hour
    private int getActualHourFromConstraintPicker(NumberPicker hourPicker) {
        int value = hourPicker.getValue();
        if (value == 18) { // "None" selected
            return -1;
        }
        return value + 6; // Convert picker value to actual hour (0 -> 6, 1 -> 7, etc.)
    }



}
