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
    View view = getView(); // Get the root view of the fragment
    String endTime;
    if (view == null) {
        return;
    }

    TimePicker startTimePicker = view.findViewById(R.id.startHourPicker);
    TimePicker endTimePicker = view.findViewById(R.id.endHourPicker);
    TimePicker breakStartTimePicker = view.findViewById(R.id.lunchBreakStartPicker);
    TimePicker breakEndTimePicker = view.findViewById(R.id.lunchBreakEndPicker);
    TimePicker constraintStartHourPicker = view.findViewById(R.id.constraintStartHourPicker);
    TimePicker constraintEndHourPicker = view.findViewById(R.id.constraintEndHourPicker);
    TextView selectedDateTextView = view.findViewById(R.id.selectedDateTextView);

    if (startTimePicker == null || endTimePicker == null || breakStartTimePicker == null || breakEndTimePicker == null || constraintStartHourPicker == null || constraintEndHourPicker == null || selectedDateTextView == null) {
        Log.e("AdminConstraintsFragment", "One or more views are null");
        return;
    }

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

    StringBuilder schedule = new StringBuilder();
    schedule.append("Schedule:\n");

   for (int hour = startHour; hour <= endHour; hour++) {
    for (int minute = startMinute; minute < 60; minute += 30) {
        if (hour == endHour && minute >= endMinute) break;
        if ((hour > breakStartHour || (hour == breakStartHour && minute >= breakStartMinute)) &&
                (hour < breakEndHour || (hour == breakEndHour && minute < breakEndMinute))) {
            continue; // Skip lunch break time
        }
        if ((hour > constraintStartHour || (hour == constraintStartHour && minute >= constraintStartMinute)) &&
                (hour < constraintEndHour || (hour == constraintEndHour && minute < constraintEndMinute))) {
            continue; // Skip constraint time
        }

        String startTime = String.format("%02d:%02d", hour, minute);
        if ((minute + 30) >= 60) {
            endTime = String.format("%02d:%02d", hour + 1, (minute + 30) % 60);
        } else {
            endTime = String.format("%02d:%02d", hour, (minute + 30) % 60);
        }
        AppointmentModel appointment = new AppointmentModel(UUID.randomUUID().toString(), null, selectedDateTextView.getText().toString(), null, "Available", startTime, endTime, 30);
        schedule.append(startTime).append(" - ").append(endTime).append("\n");

        // Save appointment to Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("appointments").child(appointment.getAppointmentId());
        Log.d("AdminConstraintsFragment", "Saving appointment: " + appointment.toString());
        ref.setValue(appointment)
            .addOnSuccessListener(aVoid -> Log.d("Firebase", "Appointment successfully written!"))
            .addOnFailureListener(e -> Log.w("Firebase", "Error writing document", e));
    }
}
selectedDateTextView.setText(schedule.toString());
}}