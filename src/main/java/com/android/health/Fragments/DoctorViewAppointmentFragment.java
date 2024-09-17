package com.android.health.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.health.Adapters.DoctorViewAppointmentAdapter;
import com.android.health.R;
import com.android.health.data.Appointment;
import com.android.health.data.RecordFile;
import com.android.health.security.Security;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DoctorViewAppointmentFragment extends Fragment {

    ProgressBar progressBar;
    private TextView emptyTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_doctor_view_appointment,container,false);


        final ListView listView = rootView.findViewById(R.id.appointment_list);
        progressBar = rootView.findViewById(R.id.progress_bar);
        emptyTextView = rootView.findViewById(R.id.no_appointment);

        DatabaseReference recordsReference = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("appointments");

        final DoctorViewAppointmentAdapter adapter = new DoctorViewAppointmentAdapter(getContext(),new ArrayList<Appointment>());

        recordsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    for (DataSnapshot ds:dataSnapshot.getChildren()){
                        progressBar.setVisibility(View.GONE);
                        Appointment appointment = ds.getValue(Appointment.class);
                        adapter.add(appointment);
                        listView.setAdapter(adapter);
                    }
                }else {
                    emptyTextView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return rootView;
    }

}
