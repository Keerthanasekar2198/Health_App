package com.android.health.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

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

import java.util.List;

public class DoctorViewAppointmentAdapter extends ArrayAdapter<Appointment> {

    public DoctorViewAppointmentAdapter(@NonNull Context context, @NonNull List<Appointment> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {

        final Appointment appointment = getItem(position);

        if (convertView == null){
            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.doctor_list_item,parent,false);
        }

        final Security security = new Security(FirebaseAuth.getInstance()
                .getCurrentUser().getEmail().substring(0,10),2);

        final TextView nameTextView = convertView.findViewById(R.id.name);
        TextView numberTextView = convertView.findViewById(R.id.number);
        final Button button = convertView.findViewById(R.id.appointment_button);

        Log.e(FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0,10)+"mm"+appointment.getP_name(),"dd" +appointment.getP_number());

        nameTextView.setText(security.decrypt(appointment.getP_name()));
        numberTextView.setText(security.decrypt(appointment.getA_date())+"\t"+security.decrypt(appointment.getA_time()));

        if (security.decrypt(appointment.getA_status()).equals("0")){
            button.setText("Accept Appointment");
        }else if (security.decrypt(appointment.getA_status()).equals("1")){
            button.setText("Confirmed");
            button.setEnabled(false);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (button.getText().equals("Accept Appointment")){
                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    reference.child("users").child("doctors").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds:dataSnapshot.getChildren()){
                                if (ds.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    Patient patient = ds.getValue(Patient.class);
                                    Security security1 = new Security(appointment.getP_number(),0);
                                    Appointment appointment1 = new Appointment(
                                            security1.encrypt(security.decrypt(patient.getName()))
                                            ,FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0,10)
                                            ,FirebaseAuth.getInstance().getCurrentUser().getUid()
                                            ,security1.encrypt(security.decrypt(appointment.getA_date()))
                                            ,security1.encrypt(security.decrypt(appointment.getA_time()))
                                            ,security1.encrypt("1"));

                                    reference.child(appointment.getP_id()).child("appointments")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(appointment1);

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    Appointment appoint = new Appointment(appointment.getP_name()
                            ,appointment.getP_number()
                            ,appointment.getP_id()
                            ,appointment.getA_date()
                            ,appointment.getA_time()
                            ,security.encrypt("1"));
                    reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("appointments")
                            .child(appointment.getP_id()).setValue(appoint);

                    button.setText("Confirmed");
                    button.setEnabled(false);
                }
            }
        });




        return convertView;

    }

}
