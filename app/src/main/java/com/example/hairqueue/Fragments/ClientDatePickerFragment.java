package com.example.hairqueue.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.hairqueue.R;

public class ClientDatePickerFragment extends Fragment {

    private CalendarView calendarView;
    private TextView selectedDateTextView;
    private String selectedService;
    private Button btnAvailableAppointments;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_date_picker, container, false);
        selectedService = "";

        // Initialize the button and hide it initially
        btnAvailableAppointments = view.findViewById(R.id.btnAvailableAppointments);
        btnAvailableAppointments.setVisibility(View.GONE); // Hide the button initially

        // Initialize calendar and hide it initially
        calendarView = view.findViewById(R.id.calendarView);
        calendarView.setVisibility(View.GONE); // Hide the calendar initially

        selectedDateTextView = view.findViewById(R.id.selectedDateTextView);

        // Set up RadioGroup listener
        RadioGroup serviceTypeRadioGroup = view.findViewById(R.id.serviceTypeRadioGroup);
        serviceTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.HaircutRadioButton) {
                selectedService = "Haircut";
            } else if (checkedId == R.id.BeardRadioButton) {
                selectedService = "Beard Trim";
            } else if (checkedId == R.id.BothRadioButton) {
                selectedService = "Haircut + Beard";
            }

            // Show the calendar once a service is selected
            if (!selectedService.isEmpty()) {
                calendarView.setVisibility(View.VISIBLE); // Show the calendar
            }

            // Check if both service and date are selected, and show button if true
            checkAndShowButton();
        });

        // Set the OnDateChangeListener to handle date selection
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            // Format the selected date
            String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;

            // Display the selected date in the TextView
            selectedDateTextView.setText("Selected Date: " + selectedDate);
            selectedDateTextView.setVisibility(View.VISIBLE);

            // Check if both service and date are selected, and show button if true
            checkAndShowButton();

            // Set click listener for the button
            btnAvailableAppointments.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("selectedService", selectedService);
                bundle.putString("selectedDate", selectedDate);
                Navigation.findNavController(v).navigate(R.id.action_clientDatePickerFragment_to_clientSetAppointmentFragment, bundle);
            });
        });

        return view;
    }

    // Function to check if both service and date are selected
    private void checkAndShowButton() {
        if (!selectedService.isEmpty() && selectedDateTextView.getVisibility() == View.VISIBLE) {
            btnAvailableAppointments.setVisibility(View.VISIBLE); // Show the button
        } else {
            btnAvailableAppointments.setVisibility(View.GONE); // Hide the button
        }
    }
}



