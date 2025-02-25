package com.example.hairqueue.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairqueue.Adapters.AppointmentAdapter;
import com.example.hairqueue.Models.AppointmentModel;
import com.example.hairqueue.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ClientSetAppointmentFragment extends Fragment {

    private RecyclerView recyclerView;
    private AppointmentAdapter appointmentAdapter;
    private List<AppointmentModel> appointmentList = new ArrayList<>();

    private List<AppointmentModel> availableFutureAppointmentList = new ArrayList<>();

    private String selectedDate;
    private String selectedService;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_set_appointment, container, false);

        //get selected date and service from the bundle
        Bundle args = getArguments();
        if (args != null) {
            selectedDate = args.getString("selectedDate");
            selectedService = args.getString("selectedService"); // Retrieve the service
        }

        //RecyclerView connection
        recyclerView = view.findViewById(R.id.availableAppointmentsPickerRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Addapter creation
        appointmentAdapter = new AppointmentAdapter(getContext(), appointmentList, appointment -> {
            if (appointment.getAppointmentId() == null || appointment.getAppointmentId().isEmpty()) {
                Log.e("ClientSetAppointment", "Appointment ID is invalid");
                return;
            }

            bookAppointment(appointment);
            Navigation.findNavController(view).navigate(R.id.action_clientSetAppointmentFragment_to_clientAppointmentsFragment);
        });
        recyclerView.setAdapter(appointmentAdapter);

        TextView tvNoAppointments = view.findViewById(R.id.noAppointmentsTV);

        //load available appiontments from Firebase
        if (selectedDate != null) {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("dates").child(selectedDate);

            //check if the selected date is a work day
            dbRef.child("dateStatus").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String dayStatus = snapshot.getValue(String.class);
                    //if the selected date isn't a work day show a message

                    if (dayStatus == null || !"Work day".equals(dayStatus)) {
                        tvNoAppointments.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                    else {
                        //if the selected date is a work day show the available appointments
                        AppointmentAdapter adapter = new AppointmentAdapter();
                        adapter.getAvailableAppointmentsByDate(selectedDate, task -> {
                            if (task.isSuccessful()) {
                                List<AppointmentModel> availableAppointments = task.getResult();
                                if (availableAppointments != null && !availableAppointments.isEmpty()) {
                                    availableFutureAppointmentList = filterFutureAppointments(availableAppointments, selectedDate);
                                    if (availableFutureAppointmentList != null && !availableFutureAppointmentList.isEmpty()){
                                        appointmentAdapter.updateAppointments(availableFutureAppointmentList);

                                    }
                                    else {
                                        tvNoAppointments.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.GONE);
                                    }
                                }
                            }
                             else {
                                Log.e("ClientSetAppointment", "Failed to fetch appointments", task.getException());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ClientSetAppointment", "Error fetching day status", error.toException());
                }
            });
        }
        return view;
    }
    // update appointment on firebase
    private void bookAppointment(AppointmentModel appointment) {
        //Validate the user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getEmail() == null) {
            Toast.makeText(getContext(), "User is not logged in or email is missing!", Toast.LENGTH_LONG).show();
           return;
       }
        String userEmail = user.getEmail();
        String userId = userEmail.substring(0, userEmail.indexOf("@"));

        if (userId.isEmpty()) {
            Toast.makeText(getContext(), "Extracted User ID is empty!", Toast.LENGTH_LONG).show();
            return;
        }
        //Validate the appointment
        if (appointment == null) {
            Toast.makeText(getContext(), "Appointment object is null!", Toast.LENGTH_LONG).show();
            return;
        }
        String appointmentId = appointment.getAppointmentId();
        if (appointmentId == null || appointmentId.isEmpty()) {
            Toast.makeText(getContext(), "Appointment ID is empty or invalid!", Toast.LENGTH_LONG).show();
            return;
        }
        if (appointment.getDate() == null || appointment.getStartTime() == null) {
            Toast.makeText(getContext(), "Date or time is missing!", Toast.LENGTH_LONG).show();
            return;
        }

        //Update appointment details
        appointment.setStatus("Occupied");
        appointment.setEmail(userEmail);
        appointment.setService(selectedService);

        //Create database references
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("appointments").child(appointmentId);
        DatabaseReference appointmentRef = FirebaseDatabase.getInstance().getReference("dates").child(appointment.getDate()).child("appointments").child(appointmentId);

        //Show a toast with appointment details before saving
        String appointmentDetails = "Appointment booked!\nID: " + appointmentId +
                "\nDate: " + appointment.getDate() +
                "\nTime: " + appointment.getStartTime() +
                "\nService: " + selectedService;

        //Save the appointment in Firebase (in both locations)
        userRef.setValue(appointment)
                .addOnSuccessListener(aVoid -> {
                    // Save the service in the appointmentRef
                    appointmentRef.child("status").setValue("Occupied")
                            .addOnSuccessListener(aVoid1 -> appointmentRef.child("email").setValue(userEmail)
                                    .addOnSuccessListener(aVoid2 -> appointmentRef.child("service").setValue(selectedService)
                                            .addOnSuccessListener(aVoid3 -> {
                                                Toast.makeText(getContext(), "✅ Appointment successfully booked!\nService: " + selectedService, Toast.LENGTH_LONG).show();
                                                appointmentAdapter.notifyDataSetChanged();
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update service!", Toast.LENGTH_LONG).show())
                                    )
                                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update email!", Toast.LENGTH_LONG).show())
                            )
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update status!", Toast.LENGTH_LONG).show());
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to save appointment in Firebase!", Toast.LENGTH_LONG).show());
    }


    public List<AppointmentModel> filterFutureAppointments(List<AppointmentModel> availableAppointments, String date) {
        List<AppointmentModel> filteredAppointments = new ArrayList<>();
        Calendar now = Calendar.getInstance();

        try {
            //Extract date
            String[] dateParts = date.split("-");
            Calendar appointmentDate = Calendar.getInstance();
            appointmentDate.set(
                    Integer.parseInt(dateParts[0]),
                    Integer.parseInt(dateParts[1]) - 1,
                    Integer.parseInt(dateParts[2])
            );

            //If the date didn't past - return all date's available appointments
            if (appointmentDate.after(now)) {
                return availableAppointments;
            }

            //If the date is the today - return only future available appointments of the date
            if (isToday(appointmentDate)) {
                for (AppointmentModel appointment : availableAppointments) {
                    if (isTimeInFuture(appointment.getStartTime(), now)) {
                        filteredAppointments.add(appointment);
                    }
                }
                return filteredAppointments;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return filteredAppointments; //If the date has past return an empty list
    }

    private boolean isToday(Calendar appointmentDate) {
        Calendar now = Calendar.getInstance();
        return (now.get(Calendar.YEAR) == appointmentDate.get(Calendar.YEAR) &&
                now.get(Calendar.MONTH) == appointmentDate.get(Calendar.MONTH) &&
                now.get(Calendar.DAY_OF_MONTH) == appointmentDate.get(Calendar.DAY_OF_MONTH));
    }

    private boolean isTimeInFuture(String startTime, Calendar now) {
        String[] timeParts = startTime.split(":");
        int appointmentHour = Integer.parseInt(timeParts[0]);
        int appointmentMinute = Integer.parseInt(timeParts[1]);

        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);

        return (appointmentHour > currentHour || (appointmentHour == currentHour && appointmentMinute > currentMinute));
    }
}