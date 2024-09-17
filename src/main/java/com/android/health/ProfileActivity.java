package com.android.health;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.health.data.Patient;
import com.android.health.security.Security;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        final TextView nameTextView = findViewById(R.id.name);
        final TextView ageTextView = findViewById(R.id.age);
        final TextView genderTextView = findViewById(R.id.gender);
        final TextView phoneNumberTextView = findViewById(R.id.phone_number);

        int type = MainActivity.typePreference.getInt("u_type",0);
        String type_name = "";

        if (type == 1){
            type_name = "patients";
        }else if(type == 2){
            type_name = "doctors";
        }else if(type == 3){
            type_name = "staffs";
        }

        DatabaseReference profileReference = FirebaseDatabase.getInstance().getReference().child("users").child(type_name);

        final Security security = new Security(FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0,10),1);

        profileReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    Patient patient = dataSnapshot.getValue(Patient.class);
                    nameTextView.setText(security.decrypt(patient.getName()));
                    ageTextView.setText(security.decrypt(patient.getAge()));
                    genderTextView.setText(security.decrypt(patient.getGender()));
                    phoneNumberTextView.setText(patient.getPhoneNumber());
                    progressDialog.dismiss();
                }
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

    }
}
