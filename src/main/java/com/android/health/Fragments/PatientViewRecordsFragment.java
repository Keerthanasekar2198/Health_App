package com.android.health.Fragments;

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

import com.android.health.R;
import com.android.health.data.RecordFile;
import com.android.health.security.Security;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PatientViewRecordsFragment extends Fragment {

    ProgressBar progressBar;
    private TextView emptyTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_patient_view_records, container, false);

        final ListView listView = rootView.findViewById(R.id.records_list);
        progressBar = rootView.findViewById(R.id.progress_bar);
        emptyTextView = rootView.findViewById(R.id.no_records);

        DatabaseReference recordsReference = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("records");

        final ArrayAdapter<String> recordAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,new ArrayList<String>());

        recordsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    for (DataSnapshot ds:dataSnapshot.getChildren()){
                        progressBar.setVisibility(View.GONE);
                        RecordFile recordFile = ds.getValue(RecordFile.class);
                        Security security = new Security(
                                FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0,10),2);
                        recordAdapter.add(security.decrypt(recordFile.getFile_name()));
                        listView.setAdapter(recordAdapter);
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
