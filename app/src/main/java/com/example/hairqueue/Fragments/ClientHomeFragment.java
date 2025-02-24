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
        loadAvailableTodayAppointments(todayDate);
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

    private void updateCanceledAppointments() {
        Log.d("DEBUG", "updateCanceledAppointments called");
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        String userEmail = user.getEmail();
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");

        appointmentsRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Appointment Canceled")
                .setMessage("Your appointment on " + appointment.getDate() + " at " + appointment.getStartTime() + " has been canceled.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false) // מונע סגירה בלחיצה מחוץ לדיאלוג
                .show();
    }




    private void loadAvailableTodayAppointments(String date) {
        appointmentAdapter.getAvailableAppointmentsByDate(date, task -> {
            if (task.isSuccessful()) {
                // אם יש תורים זמינים
                List<AppointmentModel> availableAppointments = task.getResult();
                if (availableAppointments != null && !availableAppointments.isEmpty()) {
                    // אם יש תורים פנויים, נסיר את הודעת "אין תורים פנויים"
                    tvNoAppointments.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    // עדכון הרשימה והפונקציה של ה-Adapter
                    appointmentList.clear();
                    appointmentList.addAll(availableAppointments);
                    appointmentAdapter.notifyDataSetChanged();
                } else {
                    // אם אין תורים פנויים, נציג את הודעת "אין תורים פנויים"
                    tvNoAppointments.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            } else {
                // אם קרה שגיאה, טיפול בשגיאה
                tvNoAppointments.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                // הצגת הודעת שגיאה
                Toast.makeText(getContext(), "Error loading appointments", Toast.LENGTH_SHORT).show();
            }
        });
    }




}
