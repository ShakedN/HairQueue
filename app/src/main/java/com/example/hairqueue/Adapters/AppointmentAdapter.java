package com.example.hairqueue.Fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hairqueue.Data.Appointment;
import com.example.hairqueue.R;

import java.util.List;

public class AppointmentAdapter extends BaseAdapter {

    private Context context;
    private List<Appointment> appointments;

    public AppointmentAdapter(Context context, List<Appointment> appointments) {
        this.context = context;
        this.appointments = appointments;
    }

    @Override
    public int getCount() {
        return appointments.size();
    }

    @Override
    public Object getItem(int position) {
        return appointments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.appointment_item, parent, false);
        }

        TextView appointmentTimeTextView = convertView.findViewById(R.id.appointmentTime);
        TextView customerNameTextView = convertView.findViewById(R.id.customerName);

        Appointment appointment = appointments.get(position);
        appointmentTimeTextView.setText(appointment.getAppointmentTime());
        customerNameTextView.setText(appointment.getCustomerName());

        return convertView;
    }
}
