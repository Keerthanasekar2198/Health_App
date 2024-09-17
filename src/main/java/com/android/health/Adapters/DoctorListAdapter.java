package com.android.health.Adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.health.Fragments.DoctorListFragment;
import com.android.health.R;
import com.android.health.data.Appointment;
import com.android.health.data.Patient;
import com.android.health.security.Security;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DoctorListAdapter extends ArrayAdapter<Patient> {

    public DoctorListAdapter(@NonNull Context context, @NonNull List<Patient> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Patient patient = getItem(position);

        if (convertView == null){
            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.doctor_list_item,parent,false);
        }

        final Security doctorSecurity = new Security(patient.getPhoneNumber(),1);
        final Security security = new Security(FirebaseAuth.getInstance()
                .getCurrentUser().getEmail().substring(0,10),2);


        final TextView nameTextView = convertView.findViewById(R.id.name);
        TextView numberTextView = convertView.findViewById(R.id.number);
        final Button button = convertView.findViewById(R.id.appointment_button);

        nameTextView.setText(doctorSecurity.decrypt(patient.getName()));
        numberTextView.setText(patient.getPhoneNumber());

        button.setEnabled(true);

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("appointments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                if (ds.hasChild(DoctorListFragment.userId.get(position))){
                    for (DataSnapshot ds1:ds.getChildren()){
                        if (ds1.getKey().equals(DoctorListFragment.userId.get(position))){
                            Appointment appointment1 = ds1.getValue(Appointment.class);
                            final Security security1 = new Security(FirebaseAuth.getInstance()
                                    .getCurrentUser().getEmail().substring(0,10),2);
                            if (security1.decrypt(appointment1.getA_status()).equals("1")){
                                button.setText("Confirmed");
                                button.setEnabled(false);
                            }else {
                                button.setText("Appointment in Process");
                                button.setEnabled(false);
                            }
                        }
                    }
                }else {
                    button.setEnabled(true);
                    button.setText("Choose an Appointment");
                    final int[] date = new int[3];
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final String[] dateString = new String[2];
                            date[0] = Calendar.getInstance().get(Calendar.YEAR);
                            date[1] = Calendar.getInstance().get(Calendar.MONTH);
                            date[2] = Calendar.getInstance().get(Calendar.DATE);
                            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                                    date[0] = selectedYear;
                                    date[1] = selectedMonth;
                                    date[2] = selectedDay;

                                    dateString[0] = String.valueOf((date[1]+1)+"/"+date[2]+"/"+date[0]);

                                    date[0] = Calendar.getInstance().get(Calendar.HOUR);
                                    date[1] = Calendar.getInstance().get(Calendar.MINUTE);

                                    TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker timePicker, int i, int i1) {

                                            date[0] = i;
                                            date[1] = i1;

                                            dateString[1] = String.valueOf(date[0]+":"+date[1]);

                                            Security dSecurity = new Security(patient.getPhoneNumber(),0);

                                            Appointment appointment = new Appointment(security.encrypt(dSecurity.decrypt(patient.getName())),
                                                    patient.getPhoneNumber(),
                                                    DoctorListFragment.userId.get(position),
                                                    security.encrypt(dateString[0]),
                                                    security.encrypt(dateString[1]),
                                                    security.encrypt("0"));

                                            Appointment dAppointment = new Appointment(dSecurity.encrypt(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()),
                                                    FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0,10),
                                                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                                    dSecurity.encrypt(dateString[0]),
                                                    dSecurity.encrypt(dateString[1]),
                                                    dSecurity.encrypt("0"));

                                            reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .child("appointments")
                                                    .child(DoctorListFragment.userId.get(position)).setValue(appointment);

                                            reference.child(DoctorListFragment.userId.get(position))
                                                    .child("appointments")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(dAppointment);

                                            button.setText("Appointment in Process");
                                            button.setEnabled(false);

                                        }
                                    },date[0],date[1],true);
                                    timePickerDialog.setTitle("Choose Time:");
                                    timePickerDialog.show();
                                }
                            },date[0],date[1],date[2]);
                            datePickerDialog.setTitle("Choose Date:");
                            datePickerDialog.show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return convertView;
    }
}
