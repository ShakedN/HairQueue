package com.example.hairqueue.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.hairqueue.R;
import com.example.hairqueue.activities.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_EMAIL = "email";
    private static final String ARG_Full_Name="full_name";
    private String mParam1;
    private String mParam2;
    private String getArgEmail;
    private String getFull_Name;
    private String emailAdmin = "admin@gmail.com";
    private String passwordAdmin = "admin123";

    public LoginFragment() {
        // Required empty public constructor
    }
    public static LoginFragment newInstance(String param1, String param2,String email,String full_Name) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_EMAIL, email);
        args.putString(ARG_Full_Name,full_Name);
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
            getFull_Name=getArguments().getString(ARG_Full_Name);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        Button button1 = view.findViewById(R.id.buttonRegister);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_loginFregment_to_regFregment);

            }
        });

        Button button2 = view.findViewById(R.id.buttonLogin);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity =(MainActivity) getActivity();
                mainActivity.login(v,emailAdmin,passwordAdmin);
            }


        });
        return view;
    }
}