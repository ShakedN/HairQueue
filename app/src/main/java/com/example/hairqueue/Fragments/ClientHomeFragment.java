package com.example.hairqueue.Fragments;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.hairqueue.Adapters.AppointmentAdapter;
import com.example.hairqueue.Models.AppointmentModel;
import com.example.hairqueue.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ClientHomeFragment extends Fragment  {

    private FirebaseAuth mAuth;
    private TextView greetingTextView;
    private RecyclerView recyclerView;
    private AppointmentAdapter appointmentAdapter;
    private List<AppointmentModel> appointmentList;
    private DatabaseReference dbRef;
    private Button buttonBook;
    private ImageButton buttonProfile;
    private ImageButton buttonList;
    private ImageButton buttonLogout;
    TextView tvNoAppointments;
    ImageView greetingIcon;
    private List<AppointmentModel> appointments;


    public ClientHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_client_home, container, false);

        // Initialize the GreetingTextView and RecyclerView and Button
        buttonBook = view.findViewById(R.id.btnBookAppointment);
        buttonLogout = view.findViewById(R.id.logoutButton);
        buttonProfile = view.findViewById(R.id.profileButton);
        buttonList = view.findViewById(R.id.appointmentListButton);
        greetingTextView = view.findViewById(R.id.greetingTextView);
        recyclerView = view.findViewById(R.id.rvAvailableAppointments);
        greetingIcon = view.findViewById(R.id.greetingIcon);
        tvNoAppointments = view.findViewById(R.id.tvNoAppointments);
        View view2 = inflater.inflate(R.layout.custom_alert_dialog, container, false);


        // Initialize the appointments list
        appointmentList = new ArrayList<>();

        // Initialize AppointmentAdapter
        appointmentAdapter = new AppointmentAdapter(getContext(), appointmentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(appointmentAdapter);


        mAuth = FirebaseAuth.getInstance();


        buttonBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the button click event here
                Toast.makeText(getContext(), "Book Appointment", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).navigate(R.id.action_clientHomeFragment_to_clientDatePickerFragment);
            }
        });


        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Profile", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).navigate(R.id.action_clientHomeFragment_to_activityProfileFragment);
            }
        });

        buttonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Appointments list", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).navigate(R.id.action_clientHomeFragment_to_clientAppointmentsFragment);
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(getContext(), "Logging out...", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).navigate(R.id.action_clientHomeFragment_to_loginFragment);
            }
        });

        getUserName();
        updateCanceledAppointments();

        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        loadAvailableTodayAppointments();
        return view;
    }



    private void getUserName() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            dbRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference usersRef = dbRef.child("users");

            // Add a listener to fetch user data from the database
            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String fullName = userSnapshot.child("fullName").getValue(String.class);
                        String userEmail = userSnapshot.child("email").getValue(String.class);

                        // Check if the current user's email matches the one in the database
                        if (email != null && email.equals(userEmail)) {
                            // Set greeting message based on the time of day
                            String greeting = getGreetingMessage();

                            // Get the appropriate icon based on the greeting
                            int iconResId = getGreetingIcon(greeting);

                            // Update the ImageView with the correct icon

                            greetingIcon.setImageResource(iconResId);

                            // Create the greeting text
                            String fullGreeting = greeting + ", " + fullName + "!";
                            greetingTextView.setText(fullGreeting);
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Log an error if the data retrieval fails
                    Log.w("Database Error", "Failed to read user data.", error.toException());
                }
            });
        }
    }


    private String getGreetingMessage() {
        // Get the current time
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        // Determine the greeting message based on the time of day
        if (hourOfDay >= 5 && hourOfDay < 12) {
            return "Good Morning"; // 5:00 - 11:59
        } else if (hourOfDay >= 12 && hourOfDay < 18) {
            return "Good Afternoon"; // 12:00 - 17:59
        } else {
            return "Good Evening"; // 18:00 - 4:59
        }
    }

    private int getGreetingIcon(String greeting) {
        // Return the corresponding icon resource ID based on the greeting message
        switch (greeting) {
            case "Good Morning":
                return R.drawable.morning; // Icon for Good Morning
            case "Good Afternoon":
                return R.drawable.afternoon; // Icon for Good Afternoon
            case "Good Evening":
                return R.drawable.evening; // Icon for Good Evening
            default:
                return R.drawable.logo; // Default icon if no match
        }
    }

    private void updateCanceledAppointments( ) {
        Log.d("DEBUG", "updateCanceledAppointments called");
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        String userEmail = user.getEmail();
        String userName = userEmail.split("@")[0];

        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("users").child(userName).child("appointments");

        appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot appointmentSnapshot : snapshot.getChildren()) {
                    AppointmentModel appointment = appointmentSnapshot.getValue(AppointmentModel.class);
                    if (appointment != null && "Canceled".equals(appointment.getStatus())) {
                        showCancellationDialog(appointment);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error checking canceled appointments", error.toException());
            }
        });
    }
    private void showCancellationDialog(AppointmentModel appointment) {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view2 = inflater.inflate(R.layout.custom_alert_dialog, null); // Ensure the correct layout file

        // Find views in the inflated layout
        TextView alertTitle = view2.findViewById(R.id.alertTitle);
        TextView alertMessage = view2.findViewById(R.id.alertMessage);
        ImageView alertIcon = view2.findViewById(R.id.alertIcon);
        Button okButton = view2.findViewById(R.id.btnConfirm);
        Button cancelButton = view2.findViewById(R.id.btnCancel);

        // Set up AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view2);
        AlertDialog alertDialog = builder.create();

        // Set background transparent if needed
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        Toast.makeText(getContext(),"Appointment Canceled",Toast.LENGTH_SHORT).show();
        // Show dialog
        alertDialog.show();

        // Set dialog content
        alertTitle.setText("Appointment Canceled");
        alertMessage.setText("Your appointment on " + appointment.getDate() + " at " + appointment.getStartTime() + " has been canceled.");
        alertIcon.setImageResource(android.R.drawable.ic_dialog_alert);
        cancelButton.setVisibility(View.GONE);

        // Set button click listener
        okButton.setOnClickListener(v -> {
            cancelAppointment(appointment); // Call your function
            alertDialog.dismiss();
        });
    }

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
                        //Delete appointment on the date's appointments list
                        DatabaseReference dateAppointmentsRef = FirebaseDatabase.getInstance().getReference("dates").child(appointment.getDate()).child("appointments");
                        dateAppointmentsRef.child(appointment.getAppointmentId()).removeValue();
                    } else {
                        Toast.makeText(getContext(), "Failed to cancel appointment", Toast.LENGTH_SHORT).show();
                    }
                });
    }





    private void loadAvailableTodayAppointments() {
        // Use the same list used by the adapter
        if (appointmentList == null) {
            appointmentList = new ArrayList<>();
        }

        // Use a format without zero-padding to match Firebase (e.g., "2025-2-25")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());
        String todayDate = sdf.format(new Date());
        Log.d("DEBUG", "Today's normalized date: " + todayDate);

        // Get current time in minutes (e.g., if it's 14:30, then 14*60+30 = 870 minutes)
        Calendar now = Calendar.getInstance();
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);
        int currentTimeInMinutes = currentHour * 60 + currentMinute;
        Log.d("DEBUG", "Current time in minutes: " + currentTimeInMinutes);

        // Fetch all appointments from Firebase using the adapter's method
        appointmentAdapter.getAllAppointments(task -> {
            if (task.isSuccessful()) {
                List<AppointmentModel> allAppointments = task.getResult();
                if (allAppointments == null) {
                    allAppointments = new ArrayList<>();
                }
                Log.d("DEBUG", "Total appointments fetched: " + allAppointments.size());

                // Filter only today's available appointments that start after now
                List<AppointmentModel> filteredAppointments = new ArrayList<>();
                for (AppointmentModel appointment : allAppointments) {
                    try {
                        // Debug log for each appointment
                        Log.d("DEBUG", "Appointment retrieved - Date: " + appointment.getDate() +
                                ", Status: " + appointment.getStatus() +
                                ", Start Time: " + appointment.getStartTime());

                        // Ensure appointment date, status, and start time are not null
                        if (appointment.getDate() != null && appointment.getStatus() != null && appointment.getStartTime() != null) {
                            // Normalize the appointment date (assuming it's stored as "yyyy-M-d", e.g. "2025-2-25")
                            String appointmentDate = appointment.getDate().trim();
                            // Check if the appointment is for today and status is "Available"
                            if (appointmentDate.equals(todayDate) &&
                                    appointment.getStatus().equalsIgnoreCase("Available")) {

                                // Parse the appointment's start time (expected format "HH:mm")
                                String[] timeParts = appointment.getStartTime().split(":");
                                int appHour = Integer.parseInt(timeParts[0]);
                                int appMinute = Integer.parseInt(timeParts[1]);
                                int appTimeInMinutes = appHour * 60 + appMinute;

                                // Only add the appointment if it starts at or after the current time
                                if (appTimeInMinutes >= currentTimeInMinutes) {
                                    filteredAppointments.add(appointment);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Log.d("DEBUG", "Total filtered (today's available starting after now): " + filteredAppointments.size());

                // Sort the filtered appointments by start time (earliest first)
                filteredAppointments.sort((a1, a2) -> {
                    try {
                        String[] time1Parts = a1.getStartTime().split(":");
                        String[] time2Parts = a2.getStartTime().split(":");

                        int hour1 = Integer.parseInt(time1Parts[0]);
                        int minute1 = Integer.parseInt(time1Parts[1]);
                        int hour2 = Integer.parseInt(time2Parts[0]);
                        int minute2 = Integer.parseInt(time2Parts[1]);

                        return Integer.compare(hour1 * 60 + minute1, hour2 * 60 + minute2);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }
                });

                // Update the adapter's list and UI
                appointmentList.clear();
                int count = Math.min(5, filteredAppointments.size());
                if (count > 0) {
                    appointmentList.addAll(filteredAppointments.subList(0, count));
                    recyclerView.setVisibility(View.VISIBLE);
                    tvNoAppointments.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    tvNoAppointments.setVisibility(View.VISIBLE);
                }
                appointmentAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Error loading appointments.", Toast.LENGTH_SHORT).show();
                Log.e("DEBUG", "Error fetching appointments: ", task.getException());
            }
        });
    }












}
