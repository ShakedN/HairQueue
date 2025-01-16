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

import com.example.hairqueue.Data.Appointment;
import com.example.hairqueue.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdminHomeFragment extends Fragment {

    private ListView appointmentsListView;
    private Button loadAppointmentsButton;
    private List<Appointment> appointments;
    private com.example.hairqueue.Fragments.AppointmentAdapter appointmentAdapter;

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

        // Set up the adapter for the ListView
        appointmentAdapter = new com.example.hairqueue.Fragments.AppointmentAdapter(getContext(), appointments);
        appointmentsListView.setAdapter(appointmentAdapter);

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

        // Here, you would fetch the appointments from your database (Firestore, Realtime Database, etc.)
        // For now, we simulate adding appointments to the list for demonstration
        // Example: Simulate adding appointments for the specific date

        appointments.clear();  // Clear any previous appointments
        appointments.add(new Appointment(todayDate + " 10:00 AM", "John Doe"));
        appointments.add(new Appointment(todayDate + " 02:00 PM", "Jane Smith"));

        // Update the ListView with new data
        appointmentAdapter.notifyDataSetChanged();

        // Show a message indicating which day's appointments were loaded
        String message = isDayOff ? "Appointments for tomorrow have been loaded." : "Appointments for today have been loaded.";
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    // Example method to check if it's a day off (you can modify this based on your logic)
    private boolean checkIfDayOff(Calendar calendar) {
        // You can customize this logic, e.g., check if it's a weekend or a holiday.
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.MONDAY;  // Example: Day off on weekends
    }
}
