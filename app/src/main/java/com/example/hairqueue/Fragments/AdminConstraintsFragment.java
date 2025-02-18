package com.example.hairqueue.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.hairqueue.Models.AppointmentModel;
import com.example.hairqueue.Models.DateModel;
import com.example.hairqueue.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("dates");


        // Initialize work day options
        workDayOptionsLayout = view.findViewById(R.id.workDayOptionsLayout);
        // Save button
        saveConstraintsButton = view.findViewById(R.id.saveButton);
        // Set up RadioGroup listener
        RadioGroup dayTypeRadioGroup = view.findViewById(R.id.dayTypeRadioGroup);
        dayTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            DatabaseReference selectedDateRef = dbRef.child(selectedDate);
            selectedDateRef.child("dateStatus").get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    String currentStatus = task.getResult().getValue(String.class);

                    // Inflate the custom alert dialog layout
                    View view2 = inflater.inflate(R.layout.custom_alert_dialog, container, false);
                    TextView alertTitle = view2.findViewById(R.id.alertTitle);
                    TextView alertMessage = view2.findViewById(R.id.alertMessage);
                    ImageView alertIcon = view2.findViewById(R.id.alertIcon);
                    Button okButton = view2.findViewById(R.id.btnConfirm);
                    Button cancelButton = view2.findViewById(R.id.btnCancel);

                    // Show your custom AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setView(view2);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    alertDialog.show();

                    // Cancel Button Action
                    cancelButton.setOnClickListener(v -> {
                        alertDialog.dismiss();
                        Navigation.findNavController(view).navigate(R.id.action_adminFragmentConstraints_to_adminHomeFragment);
                    });

                    // Handle Work Day Selection
                    if (checkedId == R.id.workDayRadioButton) {
                        if ("Day off".equals(currentStatus) || "Sick day".equals(currentStatus)) {
                            alertTitle.setText("Work Day Confirmation");
                            alertMessage.setText("This day is marked as a " + currentStatus.toLowerCase() + ". Are you sure you want to change it?");
                            alertIcon.setImageResource(android.R.drawable.ic_dialog_alert);
                            alertDialog.show();

                            okButton.setOnClickListener(v -> {
                                dbRef.child(selectedDate).setValue(new DateModel(selectedDate, "Work day", null));
                                disableRadioGroup(dayTypeRadioGroup); // Lock selection
                                saveConstraintsButton.setVisibility(View.VISIBLE);
                                workDayOptionsLayout.setVisibility(View.VISIBLE);
                                alertDialog.dismiss();
                            });
                        }

                        else {
                            dbRef.child(selectedDate).setValue(new DateModel(selectedDate, "Work day", null));
                            saveConstraintsButton.setVisibility(View.VISIBLE);
                            workDayOptionsLayout.setVisibility(View.VISIBLE);
                            alertDialog.dismiss();
                           disableRadioGroup(dayTypeRadioGroup);

                        }
                    }

                    // Handle Day Off Selection
                    if (checkedId == R.id.dayOffRadioButton) {
                        if ("Sick day".equals(currentStatus) || "Work day".equals(currentStatus)) {
                            alertTitle.setText("Day Off Confirmation");
                            alertMessage.setText("This day is marked as a " + currentStatus.toLowerCase() + ". Are you sure you want to change it?");
                            alertIcon.setImageResource(android.R.drawable.ic_dialog_alert);
                            alertDialog.show();

                            okButton.setOnClickListener(v -> {
                                dbRef.child(selectedDate).setValue(new DateModel(selectedDate, "Day off", null));
                                disableRadioGroup(dayTypeRadioGroup);
                                alertDialog.dismiss();
                            });

                        } else {
                            if ("Day off".equals(currentStatus)) {
                                alertDialog.dismiss();
                                Toast.makeText(getContext(), "It is already a day off", Toast.LENGTH_LONG).show();
                                enableRadioGroup(dayTypeRadioGroup);
                            } else {
                                alertDialog.dismiss();
                            }
                        }
                    }

                    // Handle Sick Day Selection
                    if (checkedId == R.id.sickDayRadioButton) {
                        if ("Day off".equals(currentStatus) || "Work day".equals(currentStatus)) {
                            alertTitle.setText("Sick Day Confirmation");
                            alertMessage.setText("This day is marked as a " + currentStatus.toLowerCase() + ". Are you sure you want to change it?");
                            alertIcon.setImageResource(android.R.drawable.ic_dialog_alert);
                            alertDialog.show();

                            okButton.setOnClickListener(v -> {
                                dbRef.child(selectedDate).setValue(new DateModel(selectedDate, "Sick day", null));
                                disableRadioGroup(dayTypeRadioGroup);
                                alertDialog.dismiss();
                            });

                        } else {
                            alertDialog.dismiss();
                            Toast.makeText(getContext(), "It is already a sick day", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    if(checkedId == R.id.sickDayRadioButton)
                    {
                        dbRef.child(selectedDate).setValue(new DateModel(selectedDate, "Sick day", null));
                        Toast.makeText(getContext(), "Sick day saved successfully!", Toast.LENGTH_SHORT).show();
                        disableRadioGroup(dayTypeRadioGroup);
                    }
                    if(checkedId==R.id.workDayRadioButton){
                        dbRef.child(selectedDate).setValue(new DateModel(selectedDate, "Work day", null));
                        saveConstraintsButton.setVisibility(View.VISIBLE);
                        workDayOptionsLayout.setVisibility(View.VISIBLE);
                        Log.d("Firebase", "Work day open options");
                        disableRadioGroup(dayTypeRadioGroup);


                    }
                    if(checkedId==R.id.dayOffRadioButton){
                        dbRef.child(selectedDate).setValue(new DateModel(selectedDate, "Day off", null));
                        Toast.makeText(getContext(),"Day off saved successfully!",Toast.LENGTH_SHORT).show();
                        disableRadioGroup(dayTypeRadioGroup);

                    }
                }
            });
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

        saveConstraintsButton.setOnClickListener(v ->

                saveConstraints(
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
        hourPicker.setDisplayedValues(new String[]{"06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "None"});
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

    private void saveConstraints(NumberPicker startHourPicker, NumberPicker startMinutePicker,
                                 NumberPicker endHourPicker, NumberPicker endMinutePicker,
                                 NumberPicker lunchStartHourPicker, NumberPicker lunchStartMinutePicker,
                                 NumberPicker lunchEndHourPicker, NumberPicker lunchEndMinutePicker,
                                 NumberPicker constraintStartHourPicker, NumberPicker constraintStartMinutePicker,
                                 NumberPicker constraintEndHourPicker, NumberPicker constraintEndMinutePicker,
                                 TextView selectedDateTextView) {
        // Extract selected date

        // Get values from NumberPickers
        int startHour = startHourPicker.getValue();
        int startMinute = startMinutePicker.getValue() * 30; // 00 or 30
        int endHour = endHourPicker.getValue();
        int endMinute = endMinutePicker.getValue() * 30;
        int lunchStartTotalMinutes = (lunchStartHourPicker.getValue() * 60) + (lunchStartMinutePicker.getValue() * 30);
        int lunchEndTotalMinutes = (lunchEndHourPicker.getValue() * 60) + (lunchEndMinutePicker.getValue() * 30);

        // Check if either constraint time is "None"
        boolean isStartNone = constraintStartMinutePicker.getValue() == 2 || constraintStartHourPicker.getValue() == 18;
        boolean isEndNone = constraintEndMinutePicker.getValue() == 2 || constraintEndHourPicker.getValue() == 18;

        // Calculate constraint times only if they're not "None"
        int constraintStartTotalMinutes = isStartNone ? -1 :
                (getActualHourFromConstraintPicker(constraintStartHourPicker) * 60) +
                        (constraintStartMinutePicker.getValue() * 30);

        int constraintEndTotalMinutes = isEndNone ? -1 :
                (getActualHourFromConstraintPicker(constraintEndHourPicker) * 60) +
                        (constraintEndMinutePicker.getValue() * 30);

        // Validate working hours
        if (startHour > endHour || (startHour == endHour && startMinute >= endMinute)) {
            Toast.makeText(getContext(), "Invalid working hours! Start time must be before end time.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Validate lunch break
        if (lunchStartTotalMinutes >= lunchEndTotalMinutes) {
            Toast.makeText(getContext(), "Invalid lunch break! Start time must be before end time.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Validate constraints - both must be set or both must be "None"
        if ((isStartNone && !isEndNone) || (!isStartNone && isEndNone)) {
            Toast.makeText(getContext(),
                    "Both constraint times must be set, or both must be 'None'",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // If both constraints are set, validate their order
        if (!isStartNone && !isEndNone && constraintStartTotalMinutes >= constraintEndTotalMinutes) {
            Toast.makeText(getContext(),
                    "Invalid constraint times! Start must be before end time.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Extract selected date
        String selectedDate = selectedDateTextView.getText().toString().replace("Selected Date: ", "").trim();

        // Firebase setup
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("dates");

        StringBuilder schedule = new StringBuilder();
        int currentHour = startHour;
        int currentMinute = startMinute;
        List<AppointmentModel> appointments = new ArrayList<>();

        while (currentHour < endHour || (currentHour == endHour && currentMinute < endMinute)) {
            int currentTotalMinutes = (currentHour * 60) + currentMinute;
            int nextTotalMinutes = currentTotalMinutes + 30;

            // Skip appointment if it falls within lunch break
            if (currentTotalMinutes >= lunchStartTotalMinutes && currentTotalMinutes < lunchEndTotalMinutes) {
                currentMinute += 30;
                if (currentMinute >= 60) {
                    currentMinute = 0;
                    currentHour++;
                }
                continue;
            }

            // Skip appointment if constraints are set (both must be set at this point) and time falls within constraint period
            if (!isStartNone && !isEndNone &&
                    currentTotalMinutes >= constraintStartTotalMinutes &&
                    currentTotalMinutes < constraintEndTotalMinutes) {
                currentMinute += 30;
                if (currentMinute >= 60) {
                    currentMinute = 0;
                    currentHour++;
                }
                continue;
            }

            // Generate appointment times
            String startTime = String.format("%02d:%02d", currentHour, currentMinute);
            int nextHour = nextTotalMinutes / 60;
            int nextMinute = nextTotalMinutes % 60;
            String endTime = String.format("%02d:%02d", nextHour, nextMinute);

            // Create appointment
            AppointmentModel appointment = new AppointmentModel(
                    UUID.randomUUID().toString(),
                    null,
                    selectedDate,
                    null,
                    "Available",
                    startTime,
                    endTime,
                    30
            );
            appointments.add(appointment);

            schedule.append(startTime).append(" - ").append(endTime).append("\n");

            currentMinute += 30;
            if (currentMinute >= 60) {
                currentMinute = 0;
                currentHour++;
            }
        }

        // Save appointments to Realtime Database
        DateModel dateModel = new DateModel(selectedDate, "Work day", appointments);
        dbRef.child(selectedDate).setValue(dateModel)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Appointments saved for date: " + selectedDate);
                    Toast.makeText(getContext(), "Schedule saved successfully!", Toast.LENGTH_SHORT).show();
                    saveConstraintsButton.setVisibility(View.GONE);
                    workDayOptionsLayout.setVisibility(View.GONE);
                   // selectedDateTextView.setText("Selected Date: " + selectedDate + "\n\n" + schedule.toString());
                    selectedDateTextView.setText("Selected Date: " + selectedDate);

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

    private void disableRadioGroup(RadioGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            group.getChildAt(i).setEnabled(false);
        }
    }

    private void enableRadioGroup(RadioGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            group.getChildAt(i).setEnabled(true);
        }
    }
}