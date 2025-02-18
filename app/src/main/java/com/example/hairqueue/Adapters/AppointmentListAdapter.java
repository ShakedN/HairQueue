package com.example.hairqueue.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hairqueue.Models.AppointmentModel;
import com.example.hairqueue.R;

import java.util.List;

public class AppointmentListAdapter extends BaseAdapter {
    private Context context;
    private List<AppointmentModel> appointments;

    public AppointmentListAdapter(Context context, List<AppointmentModel> appointments) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.appointment_item, parent, false);
        }

        AppointmentModel appointment = appointments.get(position);

        TextView tvAppointmentId = convertView.findViewById(R.id.tvAppointmentId);
        TextView tvEmail = convertView.findViewById(R.id.tvEmail);
        TextView tvDate = convertView.findViewById(R.id.tvDate);
        TextView tvService = convertView.findViewById(R.id.tvService);
        TextView tvStatus = convertView.findViewById(R.id.tvStatus);
        TextView tvStartTime = convertView.findViewById(R.id.tvStartTime);
        TextView tvEndTime = convertView.findViewById(R.id.tvEndTime);
        TextView tvDuration = convertView.findViewById(R.id.tvDuration);
        String duration = "Duration:"+String.valueOf(appointment.getDuration());

        tvAppointmentId.setText(appointment.getAppointmentId());
        tvEmail.setText(appointment.getEmail());
        tvDate.setText("Date:"+appointment.getDate());
        tvService.setText("Service:"+appointment.getService());
        tvStatus.setText("Status:"+appointment.getStatus());
        tvStartTime.setText("Time start:"+appointment.getStartTime());
        tvEndTime.setText("Time end:"+appointment.getEndTime());
        tvDuration.setText(duration);

        return convertView;
    }
}