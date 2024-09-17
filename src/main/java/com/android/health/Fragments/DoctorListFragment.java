package com.android.health.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.health.Adapters.DoctorListAdapter;
import com.android.health.Adapters.PatientListAdapter;
import com.android.health.PatientUploadRecordsActivity;
import com.android.health.R;
import com.android.health.data.Appointment;
import com.android.health.data.Patient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DoctorListFragment extends Fragment {

    public static ArrayList<String> userId = new ArrayList<>();
    ArrayList<Patient> doctorArrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_doctor_list, container, false);

        final DoctorListAdapter listAdapter = new DoctorListAdapter(getContext(),new ArrayList<Patient>());

        final ListView listView = rootView.findViewById(R.id.patient_list);

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        final ProgressBar progressBar = rootView.findViewById(R.id.progress_bar);

        reference.child("users").child("doctors").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                final Patient patient = dataSnapshot.getValue(Patient.class);
                userId.add(dataSnapshot.getKey());
                listAdapter.add(patient);
                listView.setAdapter(listAdapter);
                doctorArrayList.add(patient);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent = new Intent(getActivity(), PatientUploadRecordsActivity.class);
  //              intent.putExtra("u_id",userId.get(i));
    //            intent.putExtra("ph_no",doctorArrayList.get(i).getPhoneNumber());
      //          startActivity(intent);
            }
        });


        return rootView;
    }

}
