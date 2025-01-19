package com.example.hairqueue.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.hairqueue.R;
import com.example.hairqueue.Fragments.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.EdgeToEdge;
import androidx.drawerlayout.widget.DrawerLayout; // Import the DrawerLayout
import androidx.navigation.fragment.NavHostFragment;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout; // DrawerLayout reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Reference to the DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerView);
        NavController navController = navHostFragment.getNavController();

        // עדכון padding לפי שולי המסך
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        // בדיקה אם המשתמש כבר מחובר
        boolean isLoggedIn = getSharedPreferences("AppPrefs", MODE_PRIVATE).getBoolean("isLoggedIn", false);
        String message = "User is logged in: " + isLoggedIn;

        // הצג את הודעת ה-Toast
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

        if (isLoggedIn) {
            // אם מחובר, הראה את ה-HomeFragment
            // כאן נווט ל-HomeFragment והסתר את ה-Drawer וה-Toolbar
            navController.navigate(R.id.action_loginFragment_to_clientHomeFragment);
            findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
            findViewById(R.id.nav_view).setVisibility(View.VISIBLE);
        } else {
            // אם לא מחובר, הסתר את ה-Toolbar וה-Drawer
            //navController.navigate(R.id.action_global_loginFragment);
            findViewById(R.id.toolbar).setVisibility(View.GONE);
            findViewById(R.id.nav_view).setVisibility(View.GONE);
        }
    }


    // הצגת ה-LoginFragment
    private void showLoginFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, new LoginFragment()) // להניח שלמשתמש יש Fragment עבור Login
                .commit();

        // הוסתרת ה-Toolbar ו-Drawer עד להתחברות
        findViewById(R.id.toolbar).setVisibility(View.GONE);
        findViewById(R.id.nav_view).setVisibility(View.GONE); // הסתרת ה-Drawer

        // מניעת גישה ל-Drawer
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    // הצגת ה-HomeFragment (או כל Fragment אחר שברצונך להציג אחרי התחברות)
    private void showHomeFragment() {
        // הצגת ה-Toolbar וה-Drawer
        findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        findViewById(R.id.nav_view).setVisibility(View.VISIBLE); // הצגת ה-Drawer לאחר התחברות

        // אפשר גישה ל-Drawer אחרי התחברות
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, new ClientHomeFragment()) // להניח שאתה רוצה להציג את ה-HomeFragment
                .commit();
    }

    // פעולה להתחברות
    public void login(View view) {
        String email = ((EditText)findViewById(R.id.inputUserName)).getText().toString();
        String password = ((EditText)findViewById(R.id.inputPass)).getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_LONG).show();

                            // עדכון SharedPreferences למצב התחברות
                            getSharedPreferences("AppPrefs", MODE_PRIVATE).edit().putBoolean("isLoggedIn", true).apply();

                            // שליחת email ל-HomeFragment
                            Bundle bundle = new Bundle();
                            bundle.putString("email", email);
                            // במקום קריאה ל-Navigation.findNavController(view)
                            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                                    .findFragmentById(R.id.fragmentContainerView);
                            NavController navController = navHostFragment.getNavController();

                            // ניווט לאחר ההתחברות
                            navController.navigate(R.id.action_loginFragment_to_clientHomeFragment);
                        } else {
                            Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // פעולה לרישום
    public void reg(View view) {
        String email = ((EditText)findViewById(R.id.email)).getText().toString();
        String password = ((EditText)findViewById(R.id.password1)).getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,"Registration successful",Toast.LENGTH_LONG).show();
                            view.post(() -> Navigation.findNavController(view).navigate(R.id.action_regFregment_to_loginFregment));
                        } else {
                            Toast.makeText(MainActivity.this,"Registration failed",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // אם המשתמש יוצא מהאפליקציה, נשנה את הערך של isLoggedIn ל- false
        getSharedPreferences("AppPrefs", MODE_PRIVATE).edit().putBoolean("isLoggedIn", false).apply();
    }
}