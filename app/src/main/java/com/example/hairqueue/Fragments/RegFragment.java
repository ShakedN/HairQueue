package com.example.hairqueue.Fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.hairqueue.Data.User;
import com.example.hairqueue.R;
import com.example.hairqueue.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class RegFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_EMAIL = "email";
    private String mParam1;
    private String mParam2;
    private String getArgEmail;

    public RegFragment() {
        // Required empty public constructor
    }
    public static RegFragment newInstance(String param1, String param2,String email) {
        RegFragment fragment = new RegFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            getArgEmail=getArguments().getString(ARG_EMAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.reg_fragment, container, false);

        Button button_reg = view.findViewById(R.id.reg_button);
        button_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Navigation.findNavController(v).navigate(R.id.action_fragmentTwo_to_fragmentOne);
                EditText password1 = view.findViewById(R.id.password1);
                EditText password2 = view.findViewById(R.id.password2);

                String pass1 = password1.getText().toString();
                String pass2 = password2.getText().toString();

                // בדיקה אם הסיסמאות תואמות
                if (pass1.equals(pass2)) {
                    if(pass1.length()<6) {
                        Toast.makeText(getContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();

                    }
                    else{
                        // הסיסמאות תואמות - אפשר להמשיך עם הרישום
                        MainActivity mainActivity = (MainActivity) getActivity();
                        mainActivity.reg(v); // הרישום ב-activity
                        mainActivity.addDATA(); // הוספת הנתונים למערכת
                    }
                }
                else {
                    // הסיסמאות לא תואמות - הצגת הודעת שגיאה
                    Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }




        });
        return view;}
}

