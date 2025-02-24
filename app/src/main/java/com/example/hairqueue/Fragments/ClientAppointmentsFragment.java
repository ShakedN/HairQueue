package com.example.hairqueue.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ClientAppointmentsFragment extends Fragment {
    private RecyclerView recyclerPrevAppointments, recyclerUpcomingAppointments;
    private AppointmentAdapter prevAdapter, upcomingAdapter;
    private List<AppointmentModel> prevAppointments = new ArrayList<>();
    private List<AppointmentModel> upcomingAppointments = new ArrayList<>();
    private DatabaseReference appointmentsRef;
    private FirebaseUser currentUser;
    private String userId;
    private FirebaseAuth mAuth;
    private ImageButton buttonProfile;
    private ImageButton buttonList;
    private ImageButton buttonLogout;
    private TextView greetingTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_appointments, container, false);

        buttonLogout = view.findViewById(R.id.logoutButtonApp);
        buttonProfile = view.findViewById(R.id.profileButtonApp);
        buttonList = view.findViewById(R.id.appointmentListButtonApp);
        greetingTextView = view.findViewById(R.id.greetingTextViewApp);

        mAuth = FirebaseAuth.getInstance();
        getUserName();

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Profile", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).navigate(R.id.action_clientAppointmentsFragment_to_activityProfileFragment);
            }
        });

        buttonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Your'e in Appointments list", Toast.LENGTH_SHORT).show();
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Logout", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Navigation.findNavController(v).navigate(R.id.action_clientAppointmentsFragment_to_loginFragment);
            }
        });

        recyclerPrevAppointments = view.findViewById(R.id.rvPreviousAppointments);
        recyclerUpcomingAppointments = view.findViewById(R.id.rvUpcomingAppointments);

        recyclerPrevAppointments.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerUpcomingAppointments.setLayoutManager(new LinearLayoutManager(getContext()));

        prevAdapter = new AppointmentAdapter(getContext(), prevAppointments);
        upcomingAdapter = new AppointmentAdapter(getContext(), upcomingAppointments, appointment -> {
            if (appointment.getAppointmentId() == null || appointment.getAppointmentId().isEmpty()) {
                Log.e("ClientAppointments", "Appointment ID is invalid");
                return;
            }

            // יצירת הדיאלוג
            new AlertDialog.Builder(getContext())
                    .setTitle("Cancel Appointment")
                    .setMessage("Are you sure you want to cancel this appointment?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        cancelAppointment(appointment); //Cancle Appointment
                    })
                    .setNegativeButton("No", null)
                    .show();


        });

        recyclerPrevAppointments.setAdapter(prevAdapter);

        recyclerUpcomingAppointments.setAdapter(upcomingAdapter);



        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getEmail().split("@")[0]; //get userID from email
            fetchAppointments();
        } else {
            Toast.makeText(getContext(), "User not logged in!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void fetchAppointments() {
        appointmentsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("appointments");

        appointmentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                prevAppointments.clear();
                upcomingAppointments.clear();
                long currentTime = System.currentTimeMillis();
                //Toast.makeText(getContext(), "time:" + currentTime, Toast.LENGTH_LONG).show();

                for (DataSnapshot appointmentSnapshot : snapshot.getChildren()) {
                    AppointmentModel appointment = appointmentSnapshot.getValue(AppointmentModel.class);
                    if (appointment != null) {
                        //long appointmentTime = getAppointmentTimestamp(appointment.getDate(), appointment.getStartTime());
                        if (isPastDateTime(appointment.getDate(), appointment.getStartTime())) {
                            prevAppointments.add(appointment);
                        } else {
                            upcomingAppointments.add(appointment);
                        }
                    }
                }

                prevAdapter.notifyDataSetChanged();
                upcomingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch appointments", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //private long getAppointmentTimestamp(String date, String time) {
        //try {
            //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            //Date parsedDate = sdf.parse(date + " " + time);
           // return parsedDate != null ? parsedDate.getTime() : 0;
       // } catch (ParseException e) {
          //  e.printStackTrace();
          //  return 0;
        //}
    //}

    private void getUserName() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || user.getEmail() == null) {
            Toast.makeText(getContext(), "No user is logged in or email is missing.", Toast.LENGTH_LONG).show();
            return;
        }

        String email = user.getEmail();
        String userId = email.substring(0, email.indexOf("@"));

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("fullName");

        //Toast.makeText(getContext(), "🔎 Fetching name for user ID: " + userId, Toast.LENGTH_LONG).show();

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(getContext(), "fullName field does not exist for user: " + userId, Toast.LENGTH_LONG).show();
                    return;
                }

                String fullName = snapshot.getValue(String.class);
                if (fullName == null || fullName.isEmpty()) {
                    fullName = "User";
                }

                Toast.makeText(getContext(), "Retrieved full name: " + fullName, Toast.LENGTH_LONG).show();

                if (greetingTextView != null) {
                    greetingTextView.setText("Hello " + fullName + "!");
                } else {
                    Toast.makeText(getContext(), "greetingTextView is null!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch user name: " + error.getMessage(), Toast.LENGTH_LONG).show();
                if (greetingTextView != null) {
                    greetingTextView.setText("Hello User!");
                }
            }
        });
    }


    public boolean isPastDateTime(String date, String hour) {
        try {
            //Extract date
            String[] dateParts = date.split("-");
            Calendar appointmentDate = Calendar.getInstance();
            appointmentDate.set(
                    Integer.parseInt(dateParts[0]),
                    Integer.parseInt(dateParts[1]) - 1,
                    Integer.parseInt(dateParts[2])
            );

            //Extract hour
            String[] timeParts = hour.split(":");
            appointmentDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
            appointmentDate.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));

            //Current time
            Calendar now = Calendar.getInstance();

            //If the appointment has past
            return appointmentDate.before(now);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //Function to delete appointment from user's appointments list
    private void cancelAppointment(AppointmentModel appointment) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getEmail().split("@")[0]; //User's ID

        DatabaseReference userAppointmentsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("appointments");
        userAppointmentsRef.child(appointment.getAppointmentId()).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Appointment canceled successfully", Toast.LENGTH_SHORT).show();
                        //Update appointment's status to available on the date's appointments list
                        updateAppointmentStatusInDates(appointment);
                    } else {
                        Toast.makeText(getContext(), "Failed to cancel appointment", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Function to update appointment's status to available on the date's appointments list
    private void updateAppointmentStatusInDates(AppointmentModel appointment) {
        DatabaseReference datesRef = FirebaseDatabase.getInstance().getReference("dates");
        datesRef.child(appointment.getDate()).child("appointments").child(appointment.getAppointmentId())
                .child("status").setValue("Available")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("ClientAppointments", "Appointment status updated to Available");
                    } else {
                        Log.e("ClientAppointments", "Failed to update appointment status");
                    }
                });
    }


}
