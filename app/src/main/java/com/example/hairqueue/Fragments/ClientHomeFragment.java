package com.example.hairqueue.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ClientHomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    private TextView greetingTextView;
    private RecyclerView recyclerView;
    private AppointmentAdapter appointmentAdapter;
    private List<AppointmentModel> appointmentList = new ArrayList<>();
    private DatabaseReference dbRef;
    private Button buttonBook;

    public ClientHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_home, container, false);
        buttonBook = view.findViewById(R.id.btnBookAppointment);
        greetingTextView = view.findViewById(R.id.greetingTextView);
        recyclerView = view.findViewById(R.id.rvAvailableAppointments);
        appointmentAdapter = new AppointmentAdapter(getContext(),appointmentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(appointmentAdapter);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("dates");


        buttonBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the button click event here
                Toast.makeText(getContext(), "Book Appointment", Toast.LENGTH_SHORT).show();



                //להוסיף קישור לדף קביעת התורים
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
            DatabaseReference usersRef = dbRef.child("users");
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String fullName = userSnapshot.child("fullName").getValue(String.class);
                        String userEmail = userSnapshot.child("email").getValue(String.class);
                        if (email != null && email.equals(userEmail)) {
                            greetingTextView.setText("Hello, " + fullName);
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("Database Error", "Failed to read user data.", error.toException());
                }
            });
        }
    }

 private void loadClosestAvailableAppointments() {
     dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {
             appointmentList.clear();
             String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

             for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                 String date = dateSnapshot.getKey();
                 if (date != null && date.compareTo(currentDate) >= 0) {
                     for (DataSnapshot appointmentSnapshot : dateSnapshot.child("appointments").getChildren()) {
                         AppointmentModel appointment = appointmentSnapshot.getValue(AppointmentModel.class);
                         if (appointment != null && appointmentList.size() < 3) {
                             appointmentList.add(appointment);
                         }
                     }
                 }
                 if (appointmentList.size() >= 3) {
                     break;
                 }
             }
             appointmentAdapter.notifyDataSetChanged();
         }

         @Override
         public void onCancelled(DatabaseError databaseError) {
             Log.w("TAG", "Failed to read value.", databaseError.toException());
         }
     });
 }
}
