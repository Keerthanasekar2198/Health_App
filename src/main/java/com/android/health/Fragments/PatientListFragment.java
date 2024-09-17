package com.android.health.Fragments;

import android.content.Intent;
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

import com.android.health.Adapters.PatientListAdapter;
import com.android.health.MainActivity;
import com.android.health.PatientUploadRecordsActivity;
import com.android.health.R;
import com.android.health.data.Patient;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PatientListFragment extends Fragment {

    ArrayList<String> userId = new ArrayList<>();
    ArrayList<Patient> patientArrayList = new ArrayList<>();

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_patient_list, container, false);

        final ListView listView = rootView.findViewById(R.id.patient_list);

        final PatientListAdapter listAdapter = new PatientListAdapter(getContext(),new ArrayList<Patient>());

        DatabaseReference patientReference = FirebaseDatabase.getInstance().getReference().child("users").child("patients");

        final ProgressBar progressBar = rootView.findViewById(R.id.progress_bar);

        patientReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Patient patient = dataSnapshot.getValue(Patient.class);
                listAdapter.add(patient);
                listView.setAdapter(listAdapter);
                patientArrayList.add(patient);
                userId.add(dataSnapshot.getKey());
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
                Intent intent = new Intent(getActivity(), PatientUploadRecordsActivity.class);
                intent.putExtra("u_id",userId.get(i));
                intent.putExtra("ph_no",patientArrayList.get(i).getPhoneNumber());
                startActivity(intent);
            }
        });

        return rootView;
    }

}
