package com.example.hairqueue.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.Navigation;

import com.example.hairqueue.Data.User;
import com.example.hairqueue.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
    }
    public void login(View view) {

        String email = ((EditText)findViewById(R.id.inputUserName)).getText().toString();
        String password = ((EditText)findViewById(R.id.inputPass)).getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_LONG).show();

                            // Create a Bundle to pass the email
                            Bundle bundle = new Bundle();
                            bundle.putString("email", email);  // Store the email in the Bundle


                            // Navigate and pass the Bundle to fragmentthree
                            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_adminHomeFragment, bundle);

                        } else {
                            Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }





    public void reg(View view) {
        String email = ((EditText)findViewById(R.id.email)).getText().toString();
        String password = ((EditText)findViewById(R.id.password1)).getText().toString();
        Log.d("result",email+" "+password);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,"reg ok",Toast.LENGTH_LONG).show();
                            view.post(() -> Navigation.findNavController(view).navigate(R.id.action_regFregment_to_loginFregment));

                        } else {
                            Toast.makeText(MainActivity.this,"reg fail",Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }
    public void addDATA() {
        // Get the phone and email from input fields
        String phone = ((EditText) findViewById(R.id.phone)).getText().toString();
        String email = ((EditText) findViewById(R.id.email)).getText().toString();

        // Get instance of Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Get the current user's UID (to store the data under that user's phone number)
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();  // Use UID if needed for other purposes but not as the key for the phone

            // Reference to the user's data in the database, using phone as the key
            DatabaseReference userRef = database.getReference("users").child(phone);  // Save by phone number

            // Create a User object with the email and phone number
            User user = new User(email, phone);

            // Store the user data under the phone number key
            userRef.setValue(user)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "User data added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to add user data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}