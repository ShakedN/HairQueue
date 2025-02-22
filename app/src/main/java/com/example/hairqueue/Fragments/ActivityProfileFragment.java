package com.example.hairqueue.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.hairqueue.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivityProfileFragment extends Fragment {

    private EditText fullNameEditText, emailEditText, phoneEditText;
    private Button btnLogOut;
    private Button editProfileButton;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);

        // Link XML views to Java variables
        fullNameEditText = view.findViewById(R.id.fullNameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        btnLogOut = view.findViewById(R.id.btnLogOut);
        editProfileButton = view.findViewById(R.id.editProfileButton);

        // Disable edit text fields initially (set them to be not editable)
        fullNameEditText.setEnabled(false);
        emailEditText.setEnabled(false);
        phoneEditText.setEnabled(false);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        if (currentUser != null) {
            String userEmail = currentUser.getEmail(); // Get the current user's email

            if (userEmail != null) {
                usersRef = FirebaseDatabase.getInstance().getReference("users");

                // Query Firebase to find the user by email
                usersRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                // Retrieve user data from Firebase
                                String fullName = userSnapshot.child("fullName").getValue(String.class);
                                String email = userSnapshot.child("email").getValue(String.class);
                                String phone = userSnapshot.child("phone").getValue(String.class);

                                // Update UI elements with user data
                                fullNameEditText.setText(fullName != null ? fullName : "No Name");
                                emailEditText.setText(email != null ? email : "No Email");
                                phoneEditText.setText(phone != null ? phone : "No Phone");
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
        btnLogOut.setOnClickListener(v -> {
            mAuth.signOut(); // Sign out the user
            Toast.makeText(getContext(), "Logged Out", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigate(R.id.action_activityProfileFragment_to_loginFragment); // Navigate to login
        });


        editProfileButton.setOnClickListener(v -> {
            boolean isEditable = phoneEditText.isEnabled(); // בדוק אם השדות ניתנים לעריכה

            if (isEditable) {
                // במצב שמירה
                saveUserData(); // שמירה ב-Firebase
                fullNameEditText.setEnabled(false);  // לא ניתן לערוך אחרי שמירה
                emailEditText.setEnabled(false);
                phoneEditText.setEnabled(false);
                editProfileButton.setText("Edit");  // עדכון הכפתור ל-"Edit"
            } else {
                // במצב עריכה
                fullNameEditText.setEnabled(false);  // הפוך את השדות לניתנים לעריכה
                emailEditText.setEnabled(true);
                phoneEditText.setEnabled(true);
                editProfileButton.setText("Save");  // עדכון הכפתור ל-"Save"
            }
        });


        return view;
    }

    // פונקציה לשמירה ל-Firebase
    private void saveUserData() {
        // קבלת הנתונים מהשדות (שלא יהיו ריקים)
        String fullName = fullNameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        if (fullName.isEmpty() || phone.isEmpty()) {
            Toast.makeText(getContext(), "Full Name and Phone cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // קבלת מזהה משתמש (UID)

            // שמירה של הנתונים תחת ה-UID
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.child("fullName").setValue(fullName); // שמירה של שם מלא
            userRef.child("phone").setValue(phone); // שמירה של טלפון

            // אם המייל לא משתנה, אל תעדכן אותו.

            Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
        }
    }

}
