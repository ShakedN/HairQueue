package com.example.hairqueue.Fragments;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.hairqueue.Models.AppointmentModel;
import com.example.hairqueue.R;
import com.example.hairqueue.Adapters.AppointmentAdapter;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdminHomeFragment extends Fragment {

    private ListView appointmentsListView;
    private Button loadAppointmentsButton;
    private List<AppointmentModel> appointments;
    private AppointmentAdapter appointmentAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        // Initialize the ListView and Button
        appointmentsListView = view.findViewById(R.id.appointmentsListView);
        loadAppointmentsButton = view.findViewById(R.id.loadAppointmentsButton);

        // Initialize the appointments list
        appointments = new ArrayList<>();

        // Initialize AppointmentAdapter
        appointmentAdapter = new AppointmentAdapter(); // No context needed for this implementation

        // Set the empty view for the ListView
        TextView emptyView = view.findViewById(android.R.id.empty); // Reference the empty state message
        appointmentsListView.setEmptyView(emptyView);  // Assign the empty view

        // Handle button click to load appointments
        loadAppointmentsButton.setOnClickListener(v -> loadAppointments());

        return view;
    }

    private void loadAppointments() {
        // Get today's date
        Calendar calendar = Calendar.getInstance();
        String todayDate = DateFormat.format("yyyy-MM-dd", calendar).toString();

        // Check if today is a day off
        boolean isDayOff = checkIfDayOff(calendar); // You will need to implement this check

        // Load appointments for today or tomorrow (if day off)
        if (isDayOff) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);  // Move to tomorrow
            todayDate = DateFormat.format("yyyy-MM-dd", calendar).toString();
        }

        // Fetch appointments from Firebase for the specified date
        appointmentAdapter.getAppointmentsByDate(todayDate, task -> {
            if (task.isSuccessful()) {
                appointments.clear();
                appointments.addAll(task.getResult());

                // Update the ListView with new data
                appointmentAdapter.notifyDataSetChanged();

                // Show a message indicating which day's appointments were loaded
                String message = isDayOff ? "Appointments for tomorrow have been loaded." : "Appointments for today have been loaded.";
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Error loading appointments.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Example method to check if it's a day off (you can modify this based on your logic)
    private boolean checkIfDayOff(Calendar calendar) {
        // You can customize this logic, e.g., check if it's a weekend or a holiday.
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;  // Example: Day off on weekends
    }

    public void scheduleMangement(View view) {
         Navigation.findNavController(view).navigate(R.id.action_adminHomeFragment_to_scheduleManagement);
    }
}
