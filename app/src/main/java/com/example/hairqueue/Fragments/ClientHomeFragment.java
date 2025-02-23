package com.example.hairqueue.Fragments;

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

public class ClientHomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    private TextView greetingTextView;
    private RecyclerView recyclerView;
    private AppointmentAdapter appointmentAdapter;
    private List<AppointmentModel> appointmentList;
    private DatabaseReference dbRef;
    private Button buttonBook;
    private ImageButton buttonProfile;
    private ImageButton buttonLogout;
    ImageView greetingIcon;

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
        greetingTextView = view.findViewById(R.id.greetingTextView);
        recyclerView = view.findViewById(R.id.rvAvailableAppointments);
        greetingIcon = view.findViewById(R.id.greetingIcon);


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


        //!!!צריך להוריד שנעשה את הסליידר

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the button click event here
                Toast.makeText(getContext(), "Profile", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).navigate(R.id.action_clientHomeFragment_to_activityProfileFragment);
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the button click event here
                Toast.makeText(getContext(), "Logout", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Navigation.findNavController(v).navigate(R.id.action_clientHomeFragment_to_loginFragment);
            }
        });

        getUserName();
        loadClosestAvailableAppointments();

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




    private void loadClosestAvailableAppointments() {
        // Get current date and time
        Calendar now = Calendar.getInstance();
        long currentTimeMillis = now.getTimeInMillis();

        // Fetch all appointments from Firebase
        appointmentAdapter.getAllAppointments(task -> {
            if (task.isSuccessful()) {
                List<AppointmentModel> allAppointments = task.getResult();
                if (allAppointments == null) {
                    allAppointments = new ArrayList<>();
                }

                // Filter out past appointments
                List<AppointmentModel> upcomingAppointments = new ArrayList<>();
                for (AppointmentModel appointment : allAppointments) {
                    try {
                        // Parse appointment date and time
                        Calendar appointmentTime = Calendar.getInstance();
                        String[] dateParts = appointment.getDate().split("-"); // YYYY-MM-DD
                        String[] timeParts = appointment.getStartTime().split(":"); // HH:MM

                        appointmentTime.set(
                                Integer.parseInt(dateParts[0]),
                                Integer.parseInt(dateParts[1]) - 1,
                                Integer.parseInt(dateParts[2]),
                                Integer.parseInt(timeParts[0]),
                                Integer.parseInt(timeParts[1])
                        );

                        // Add only future appointments
                        if (appointmentTime.getTimeInMillis() >= currentTimeMillis) {
                            upcomingAppointments.add(appointment);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // Sort upcoming appointments by date & time (nearest first)
                upcomingAppointments.sort((a1, a2) -> {
                    try {
                        Calendar time1 = Calendar.getInstance();
                        Calendar time2 = Calendar.getInstance();

                        String[] date1Parts = a1.getDate().split("-");
                        String[] time1Parts = a1.getStartTime().split(":");
                        String[] date2Parts = a2.getDate().split("-");
                        String[] time2Parts = a2.getStartTime().split(":");

                        time1.set(
                                Integer.parseInt(date1Parts[0]),
                                Integer.parseInt(date1Parts[1]) - 1,
                                Integer.parseInt(date1Parts[2]),
                                Integer.parseInt(time1Parts[0]),
                                Integer.parseInt(time1Parts[1])
                        );

                        time2.set(
                                Integer.parseInt(date2Parts[0]),
                                Integer.parseInt(date2Parts[1]) - 1,
                                Integer.parseInt(date2Parts[2]),
                                Integer.parseInt(time2Parts[0]),
                                Integer.parseInt(time2Parts[1])
                        );

                        return Long.compare(time1.getTimeInMillis(), time2.getTimeInMillis());

                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }
                });

                // Take only the nearest 5 appointments
                appointmentList.clear();
                appointmentList.addAll(upcomingAppointments.subList(0, Math.min(5, upcomingAppointments.size())));

                // Notify the adapter to update UI
                appointmentAdapter.notifyDataSetChanged();

                Toast.makeText(getContext(), "Loaded 5 closest appointments.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Error loading appointments.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
