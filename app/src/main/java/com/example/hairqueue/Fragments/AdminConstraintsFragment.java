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

import com.example.hairqueue.Adapters.AppointmentAdapter;
import com.example.hairqueue.Models.AppointmentModel;
import com.example.hairqueue.Models.DateModel;
import com.example.hairqueue.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminConstraintsFragment extends Fragment {

    private LinearLayout workDayOptionsLayout;
    private Button saveConstraintsButton;
    private AppointmentAdapter appointmentAdapter;
    private List<AppointmentModel> occupiedAppointments;
    private List<AppointmentModel> appointments;



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
        //DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("dates");
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("dates");
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");

        appointments = new ArrayList<>();
        occupiedAppointments = new ArrayList<>();
        // Initialize AppointmentAdapter
        appointmentAdapter = new AppointmentAdapter(getContext(), appointments);
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
                                dbRef.child(selectedDate).setValue(new DateModel(selectedDate, "Work day"));
                                disableRadioGroup(dayTypeRadioGroup); // Lock selection
                                saveConstraintsButton.setVisibility(View.VISIBLE);
                                workDayOptionsLayout.setVisibility(View.VISIBLE);
                                alertDialog.dismiss();
                            });
                        }


                        else {

                            saveConstraintsButton.setVisibility(View.VISIBLE);
                            workDayOptionsLayout.setVisibility(View.VISIBLE);
                            alertDialog.dismiss();
                           disableRadioGroup(dayTypeRadioGroup);

                        }
                    }

                    // Handle Day Off Selection
                    if (checkedId == R.id.dayOffRadioButton) {
                        if ("Sick day".equals(currentStatus) ) {
                            alertTitle.setText("Day Off Confirmation");
                            alertMessage.setText("This day is marked as a " + currentStatus.toLowerCase() + ". Are you sure you want to change it?");
                            alertIcon.setImageResource(android.R.drawable.ic_dialog_alert);
                            alertDialog.show();

                            okButton.setOnClickListener(v -> {
                                dbRef.child(selectedDate).setValue(new DateModel(selectedDate, "Day off", null));
                                disableRadioGroup(dayTypeRadioGroup);
                                alertDialog.dismiss();
                            });

                            if("Work day".equals(currentStatus)){
                                alertTitle.setText("Work day Confirmation");
                                alertMessage.setText("This day is marked as a " + currentStatus.toLowerCase() + ". Are you sure you want to change it?");
                                alertIcon.setImageResource(android.R.drawable.ic_dialog_alert);
                                alertDialog.show();

                                okButton.setOnClickListener(v -> {
                                    dbRef.child(selectedDate).child("appointments").get().addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful() && task2.getResult().exists()) {
                                            for (DataSnapshot appointmentSnapshot : task.getResult().getChildren()) {
                                                AppointmentModel appointment = appointmentSnapshot.getValue(AppointmentModel.class);
                                                if ("Available".equals(appointment.getStatus())) {
                                                    dbRef.child(selectedDate).setValue(new DateModel(selectedDate, "Day off"));
                                                }

                                            }
                                        }
                                    });
                                    disableRadioGroup(dayTypeRadioGroup);
                                    alertDialog.dismiss();
                                });
                            }
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
        // Extract values from NumberPickers (סימון כ-final כדי שניתן להשתמש בהם בתוך ה־callback)
        final int startHour = startHourPicker.getValue();
        final int startMinute = startMinutePicker.getValue() * 30; // 00 or 30
        final int endHour = endHourPicker.getValue();
        final int endMinute = endMinutePicker.getValue() * 30;
        final int lunchStartTotalMinutes = (lunchStartHourPicker.getValue() * 60) + (lunchStartMinutePicker.getValue() * 30);
        final int lunchEndTotalMinutes = (lunchEndHourPicker.getValue() * 60) + (lunchEndMinutePicker.getValue() * 30);

        final boolean isStartNone = constraintStartMinutePicker.getValue() == 2 || constraintStartHourPicker.getValue() == 18;
        final boolean isEndNone = constraintEndMinutePicker.getValue() == 2 || constraintEndHourPicker.getValue() == 18;

        final int constraintStartTotalMinutes = isStartNone ? -1 :
                (getActualHourFromConstraintPicker(constraintStartHourPicker) * 60) +
                        (constraintStartMinutePicker.getValue() * 30);
        final int constraintEndTotalMinutes = isEndNone ? -1 :
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
        if (!isStartNone && !isEndNone && constraintStartTotalMinutes >= constraintEndTotalMinutes) {
            Toast.makeText(getContext(),
                    "Invalid constraint times! Start must be before end time.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Extract selected date
        final String selectedDate = selectedDateTextView.getText().toString().replace("Selected Date: ", "").trim();
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("dates");

        // טוענים את התורים הקיימים עבור התאריך הנבחר
        appointmentAdapter.getAppointmentsByDate(selectedDate, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<AppointmentModel> allAppointments = task.getResult();
                if (allAppointments == null) {
                    allAppointments = new ArrayList<>();
                }
                // נעדכן את רשימת התורים התפוסים
                occupiedAppointments.clear();
                for (AppointmentModel appointment : allAppointments) {
                    if ("Occupied".equals(appointment.getStatus())) {
                        occupiedAppointments.add(appointment);
                    }
                }
            } else {
                Log.w("AppointmentAdapter", "There is no passed appointments.", task.getException());
            }

            // כעת, אחרי שטענו את התורים הקיימים, ניצור את לוח הזמנים החדש
            appointments.clear();
            StringBuilder schedule = new StringBuilder();
            int currentHourLocal = startHour;
            int currentMinuteLocal = startMinute;

            while (currentHourLocal < endHour || (currentHourLocal == endHour && currentMinuteLocal < endMinute)) {
                int currentTotalMinutes = (currentHourLocal * 60) + currentMinuteLocal;
                int nextTotalMinutes = currentTotalMinutes + 30;

                // דילוג על זמנים בתוך הפסקת צהריים
                if (currentTotalMinutes >= lunchStartTotalMinutes && currentTotalMinutes < lunchEndTotalMinutes) {
                    currentMinuteLocal += 30;
                    if (currentMinuteLocal >= 60) {
                        currentMinuteLocal = 0;
                        currentHourLocal++;
                    }
                    continue;
                }

                // דילוג על זמנים בתוך תקופת האילוצים, אם הוגדרו
                if (!isStartNone && !isEndNone &&
                        currentTotalMinutes >= constraintStartTotalMinutes &&
                        currentTotalMinutes < constraintEndTotalMinutes) {
                    currentMinuteLocal += 30;
                    if (currentMinuteLocal >= 60) {
                        currentMinuteLocal = 0;
                        currentHourLocal++;
                    }
                    continue;
                }

                // יצירת זמני התחלה וסיום
                String startTime = String.format("%02d:%02d", currentHourLocal, currentMinuteLocal);
                int nextHour = nextTotalMinutes / 60;
                int nextMinute = nextTotalMinutes % 60;
                String endTime = String.format("%02d:%02d", nextHour, nextMinute);

                // יצירת תור במצב "Available"
                AppointmentModel appointment = new AppointmentModel(
                        UUID.randomUUID().toString(),
                        "",
                        selectedDate,
                        "",
                        "Available",
                        startTime,
                        endTime,
                        30
                );
                appointments.add(appointment);
                schedule.append(startTime).append(" - ").append(endTime).append("\n");

                currentMinuteLocal += 30;
                if (currentMinuteLocal >= 60) {
                    currentMinuteLocal = 0;
                    currentHourLocal++;
                }
            }

            // עדכון תורים תפוסים: אם קיים תור תפוס באותה שעת התחלה, מעדכנים את הפרטים שלו
            if (occupiedAppointments != null) {
                for (AppointmentModel occupiedAppointment : occupiedAppointments) {
                    boolean exists = false;
                    for (AppointmentModel appointment : appointments) {
                        if (occupiedAppointment.getStartTime().equals(appointment.getStartTime())) {
                            // מעדכנים את פרטי התור עם המידע מהתור התפוס
                            appointment.setAppointmentId(occupiedAppointment.getAppointmentId());
                            appointment.setEmail(occupiedAppointment.getEmail());
                            appointment.setDate(occupiedAppointment.getDate());
                            appointment.setService(occupiedAppointment.getService());
                            appointment.setStatus(occupiedAppointment.getStatus());
                            appointment.setEndTime(occupiedAppointment.getEndTime());
                            appointment.setDuration(occupiedAppointment.getDuration());
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        // Mark as canceled and send email in background thread
                        occupiedAppointment.setStatus("Canceled");
                        appointments.add(occupiedAppointment);
                    }

                }
            } else {
                Log.e("AdminConstraintsFragment", "occupiedAppointments is null.");
            }

            // שמירת לוח הזמנים למסד הנתונים
            DateModel dateModel = new DateModel(selectedDate, "Work day", null);
            dbRef.child(selectedDate).setValue(dateModel)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firebase", "Appointments saved for date: " + selectedDate);
                        Toast.makeText(getContext(), "Schedule saved successfully!", Toast.LENGTH_SHORT).show();
                        saveConstraintsButton.setVisibility(View.GONE);
                        workDayOptionsLayout.setVisibility(View.GONE);
                        selectedDateTextView.setText("Selected Date: " + selectedDate);

                        // שמירה של כל התורים עם מזהה ייחודי
                        for (AppointmentModel appointment : appointments) {
                            String appointmentId = dbRef.child(selectedDate).child("appointments").push().getKey();
                            appointment.setAppointmentId(appointmentId);
                            dbRef.child(selectedDate).child("appointments").child(appointmentId).setValue(appointment)
                                    .addOnSuccessListener(aVoid1 -> {
                                        Log.d("Firebase", "Appointment ID updated: " + appointmentId);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firebase", "Error updating appointment ID", e);
                                    });
                        }
                        Navigation.findNavController(saveConstraintsButton)
                                .navigate(R.id.action_adminFragmentConstraints_to_adminHomeFragment);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firebase", "Error saving appointments", e);
                        Toast.makeText(getContext(), "Error saving schedule", Toast.LENGTH_SHORT).show();
                    });
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