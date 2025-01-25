// ScheduleManagement.java
package com.example.hairqueue.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule_management, container, false);

        // Initialize the CalendarView and TextView
        calendarView = view.findViewById(R.id.calendarView);
        selectedDateTextView = view.findViewById(R.id.selectedDateTextView);

        // Set the OnDateChangeListener to handle date selection
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Format the selected date
                String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;

                // Display the selected date in the TextView
                selectedDateTextView.setText("Selected Date: " + selectedDate);
                selectedDateTextView.setVisibility(View.VISIBLE);

                // Save the selected date (you can customize this part to save the date as needed)
                saveSelectedDate(selectedDate);

                // Navigate to AdminFragmentConstraints with the selected date
                Bundle bundle = new Bundle();
                bundle.putString("selectedDate", selectedDate);
                NavHostFragment.findNavController(ScheduleManagement.this)
                        .navigate(R.id.action_scheduleManagement_to_adminFragmentConstraints, bundle);
            }
        });

        return view;
    }

    private void saveSelectedDate(String selectedDate) {
        // Implement your logic to save the selected date
        // For example, you can save it to SharedPreferences, a database, or send it to a server
        Toast.makeText(getContext(), "Selected date saved: " + selectedDate, Toast.LENGTH_SHORT).show();
    }
}