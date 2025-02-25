package com.example.hairqueue.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hairqueue.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AdminProfileFragment extends Fragment {

    private EditText fullNameAdminEditText, emailAdminEditText, phoneAdminEditText, addressAdminEditText;
    private Button logoutAdminButton;
    private Button editProfileAdminButton;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;


    public AdminProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_profile, container, false);
        // Link XML views to Java variables
        fullNameAdminEditText = view.findViewById(R.id.fullNameAdminEditText);
        emailAdminEditText = view.findViewById(R.id.emailAdminEditText);
        phoneAdminEditText = view.findViewById(R.id.phoneAdminEditText);
        addressAdminEditText = view.findViewById(R.id.addressAdminEditText);
        logoutAdminButton = view.findViewById(R.id.LogoutButton);
        editProfileAdminButton = view.findViewById(R.id.editProfileAdminButton);

        // Disable edit text fields initially (set them to be not editable)
        fullNameAdminEditText.setEnabled(false);
        emailAdminEditText.setEnabled(false);
        phoneAdminEditText.setEnabled(false);
        addressAdminEditText.setEnabled(false);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userEmail = currentUser.getEmail(); // Get the current user's email

            if (userEmail != null) {
                usersRef = FirebaseDatabase.getInstance().getReference("users");

                // Query Firebase to find the user by email
                usersRef.orderByChild("email").equalTo(userEmail).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                // Retrieve user data from Firebase
                                String fullName = userSnapshot.child("fullName").getValue(String.class);
                                String email = userSnapshot.child("email").getValue(String.class);
                                String phone = userSnapshot.child("phone").getValue(String.class);
                                String address = userSnapshot.child("address").getValue(String.class);

                                // Update UI elements with user data
                                fullNameAdminEditText.setText(fullName != null ? fullName : "No Name");
                                emailAdminEditText.setText(email != null ? email : "No Email");
                                phoneAdminEditText.setText(phone != null ? phone : "No Phone");
                                addressAdminEditText.setText(address != null ? address : "No Address");
                            }
                        } else {
                            // Show a toast message if user data is not found
                            Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle errors when fetching data
                        Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Error: " + error.getMessage());
                    }
                });
            } else {
                Toast.makeText(getContext(), "User email not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Show an error message if no user is logged in
            Toast.makeText(getContext(), "No user is logged in", Toast.LENGTH_SHORT).show();
        }

        // Logout button functionality
        logoutAdminButton.setOnClickListener(v -> {
            mAuth.signOut(); // Sign out the user
            Toast.makeText(getContext(), "Logged Out", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigate(R.id.action_adminProfileFragment_to_loginFragment); // Navigate to login
        });


        editProfileAdminButton.setOnClickListener(v -> {
                boolean isEditable = phoneAdminEditText.isEnabled(); //one of the edit text fields that can change

            if (isEditable) {
                // save mode -  check if the fields are full
                String fullName = fullNameAdminEditText.getText().toString().trim();
                String email = emailAdminEditText.getText().toString().trim();
                String phone = phoneAdminEditText.getText().toString().trim();
                String address = addressAdminEditText.getText().toString().trim();

                if (fullName.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                    Toast.makeText(getContext(), "Full Name, Phone and Address cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                saveAdminData(fullName, phone, address);
                fullNameAdminEditText.setEnabled(false);
                emailAdminEditText.setEnabled(false); //not editable - email only for display
                phoneAdminEditText.setEnabled(false);
                addressAdminEditText.setEnabled(false);
                editProfileAdminButton.setText("Edit");
                Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
            } else {
                // edit mode
                fullNameAdminEditText.setEnabled(true); // לא מאפשרים לשנות שם
                phoneAdminEditText.setEnabled(true);
                addressAdminEditText.setEnabled(true);
                editProfileAdminButton.setText("Save");
            }
        });


        return view;

    }

    private void saveAdminData(String fullName, String phone, String address) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getEmail().split("@")[0];; //create userId from email

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.child("fullName").setValue(fullName);
            userRef.child("phone").setValue(phone);
            userRef.child("address").setValue(address);

            Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
        }
    }
}