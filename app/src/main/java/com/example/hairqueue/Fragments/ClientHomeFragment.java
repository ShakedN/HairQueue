package com.example.hairqueue.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hairqueue.Adapters.AppointmentAdapter;
import com.example.hairqueue.Models.AppointmentModel;
import com.example.hairqueue.Models.DateModel;
import com.example.hairqueue.Models.UserModel;
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
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClientHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClientHomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseAuth mAuth;
    private TextView greetingTextView;
    private RecyclerView recyclerView;
    private AppointmentAdapter appointmentAdapter;
    private List<AppointmentModel> upcomingAppointments;
    private DatabaseReference dbRef;

    public ClientHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClientHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClientHomeFragment newInstance(String param1, String param2) {
        ClientHomeFragment fragment = new ClientHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_client_home, container, false);
        greetingTextView = view.findViewById(R.id.greetingTextView);
        if (greetingTextView != null) {
            mAuth = FirebaseAuth.getInstance();
            getUserName();
        } else {
            Log.e("Fragment Error", "GreetingTextView is null");
        }

        recyclerView = view.findViewById(R.id.rvAvailableAppointments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        upcomingAppointments = new ArrayList<>();
        appointmentAdapter = new AppointmentAdapter(upcomingAppointments);
        recyclerView.setAdapter(appointmentAdapter);

        loadAvailableAppointments();

        return view;
    }

    public void getUserName() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        // Get the currently logged-in user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // If the user is logged in
        if (user != null) {
            // Retrieve the email of the logged-in user (to identify them)
            String email = user.getEmail();

            // Access all users in Firebase Database
            DatabaseReference usersRef = database.child("users");

            // Listen for changes
            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Loop through each user in the database
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Retrieve the full name and email of the user
                        String fullName = userSnapshot.child("fullName").getValue(String.class);
                        String userEmail = userSnapshot.child("email").getValue(String.class);

                        // If the full name and email match the logged-in user’s details
                        if (email != null && email.equals(userEmail)) {
                            // Found the user, now retrieve their full name
                            Log.d("User Info", "Hello, " + fullName);

                            // Update the TextView on the screen
                            if (greetingTextView != null) {
                                greetingTextView.setText("Hello, " + fullName); // Update the text
                            }

                            break; // User found, no need to continue searching
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors if something went wrong
                    Log.w("Database Error", "Failed to read value.", databaseError.toException());
                }
            });
        } else {
            // If no user is logged in
            Log.d("User Error", "No user is logged in");
        }
    }


    private void loadAvailableAppointments(String date) {
        appointmentAdapter.getAppointmentsByDate(date, task -> {
            if (task.isSuccessful()) {
                List<AppointmentModel> allAppointments = task.getResult();
                if (allAppointments == null) {
                    allAppointments = new ArrayList<>();
                }

                availableAppointments.clear();
                for (AppointmentModel appointment : allAppointments) {
                    if ("Available".equals(appointment.getStatus())) {
                        availableAppointments.add(appointment);
                    }
                }

                // Update ListView with the custom adapter
                AppointmentListAdapter adapter = new AppointmentListAdapter(getContext(), availableAppointments);
                availableAppointmentsListView.setAdapter(adapter);
                if(availableAppointments.size() == 0) {
                    Toast.makeText(getContext(), "No available appointments for " + date, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Loaded available appointments for " + date, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Error loading appointments.", Toast.LENGTH_SHORT).show();
            }
        });
    }





}
