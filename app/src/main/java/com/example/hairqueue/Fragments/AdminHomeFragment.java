package com.example.hairqueue.Fragments;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.hairqueue.Adapters.AppointmentListAdapter;
import com.example.hairqueue.Models.AppointmentModel;
import com.example.hairqueue.R;
import com.example.hairqueue.Adapters.AppointmentAdapter;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private Button buttonScheduleManagement;

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
        buttonScheduleManagement=view.findViewById(R.id.buttonSchedule);
        buttonScheduleManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleMangement(v);
            }
        });
        return view;

    }

    private void loadAppointments() {
        // Get current time
        Calendar now = Calendar.getInstance();

        // Fetch all appointments
        appointmentAdapter.getAllAppointments(task -> {
            if (task.isSuccessful()) {
                List<AppointmentModel> allAppointments = task.getResult();
                if (allAppointments == null) {
                    allAppointments = new ArrayList<>();
                }

                // Sort appointments by date and time (closest first)
                allAppointments.sort((a1, a2) -> {
                    try {
                        // Parse dates and times from strings
                        Calendar dateTime1 = Calendar.getInstance();
                        Calendar dateTime2 = Calendar.getInstance();

                        String[] date1Parts = a1.getDate().split("-");
                        String[] time1Parts = a1.getStartTime().split(":");
                        String[] date2Parts = a2.getDate().split("-");
                        String[] time2Parts = a2.getStartTime().split(":");

                        dateTime1.set(Integer.parseInt(date1Parts[0]), Integer.parseInt(date1Parts[1]) - 1, Integer.parseInt(date1Parts[2]),
                                Integer.parseInt(time1Parts[0]), Integer.parseInt(time1Parts[1]));

                        dateTime2.set(Integer.parseInt(date2Parts[0]), Integer.parseInt(date2Parts[1]) - 1, Integer.parseInt(date2Parts[2]),
                                Integer.parseInt(time2Parts[0]), Integer.parseInt(time2Parts[1]));

                        // Compare dates and times
                        return Long.compare(Math.abs(dateTime1.getTimeInMillis() - now.getTimeInMillis()),
                                Math.abs(dateTime2.getTimeInMillis() - now.getTimeInMillis()));

                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }
                });

                // Take only the closest 5 appointments
                appointments.clear();
                appointments.addAll(allAppointments.subList(0, Math.min(5, allAppointments.size())));

                // Update ListView with the custom adapter
                AppointmentListAdapter adapter = new AppointmentListAdapter(getContext(), appointments);
                appointmentsListView.setAdapter(adapter);

                Toast.makeText(getContext(), "Loaded 5 closest appointments.", Toast.LENGTH_SHORT).show();
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
    public Task<Boolean> isWorkDay(String date) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("dates").child(date);

        return databaseReference.get().continueWith(task -> {
            if (!task.isSuccessful() || !task.getResult().exists())
                return false;

            // Extract `dateStatus` from Firebase
            String status = task.getResult().child("dateStatus").getValue(String.class);
            if ("Work day".equals(status)) {
                Log.d("DEBUG", "Workday found for date: " + date);
                return true;
            }

            Log.d("DEBUG", "Not a workday: " + date);
            return false;
        });
    }
}
