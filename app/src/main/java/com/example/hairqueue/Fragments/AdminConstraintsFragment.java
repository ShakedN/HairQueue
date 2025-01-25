// AdminConstraintsFragment.java
package com.example.hairqueue.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import com.example.hairqueue.R;

public class AdminConstraintsFragment extends Fragment {

    private LinearLayout workDayOptionsLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_constraints, container, false);

        // Get the selected date from the arguments
        Bundle args = getArguments();
        String selectedDate = args != null ? args.getString("selectedDate") : "No date selected";

        // Display the selected date in the TextView
        TextView selectedDateTextView = view.findViewById(R.id.selectedDateTextView);
        selectedDateTextView.setText("Selected Date: " + selectedDate);

        // Initialize the work day options layout
        workDayOptionsLayout = view.findViewById(R.id.workDayOptionsLayout);

        // Set up the RadioGroup listener
        RadioGroup dayTypeRadioGroup = view.findViewById(R.id.dayTypeRadioGroup);
        dayTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.workDayRadioButton) {
                    workDayOptionsLayout.setVisibility(View.VISIBLE);
                } else {
                    workDayOptionsLayout.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }
}