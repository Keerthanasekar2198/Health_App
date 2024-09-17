package com.android.health;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.health.data.Patient;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText phoneNumberEditText;
    private ArrayList<String> ph = new ArrayList<>();
    private ArrayList<String> pass = new ArrayList<>();
    private ArrayList<String> age = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        phoneNumberEditText = findViewById(R.id.phone_number);

        setTitle("Forgot Password");

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference patientReference = mFirebaseDatabase.getReference().child("users").child("patients");

        patientReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Patient patient = dataSnapshot.getValue(Patient.class);
                ph.add(patient.getPhoneNumber());
                pass.add(patient.getPassword());
                age.add(patient.getAge());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.next_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.next){
            if (networkAccess()){
                if (!(TextUtils.isEmpty(phoneNumberEditText.getText().toString()))
                        &&(phoneNumberEditText.getText().toString().length()==10)){
                    if(ph.contains(phoneNumberEditText.getText().toString())){
                        Intent intent = new Intent(ForgotPasswordActivity.this,PhoneAuthActivity.class);
                        intent.putExtra("type",2);
                        intent.putExtra("phone",phoneNumberEditText.getText().toString());
                        intent.putExtra("pass",pass.get(ph.indexOf(phoneNumberEditText.getText().toString())));
                        intent.putExtra("age",age.get(ph.indexOf(phoneNumberEditText.getText().toString())));
                        startActivity(intent);
                        finish();
                    }else {
                        phoneNumberEditText.requestFocus();
                        phoneNumberEditText.setError("No Account to this PhoneNumber");
                    }
                }else {
                    phoneNumberEditText.requestFocus();
                    phoneNumberEditText.setError("Invalid PhoneNumber");
                }
            }else {
                Toast.makeText(ForgotPasswordActivity.this,"Please Check your Network Connectivity!!!",Toast.LENGTH_LONG).show();
            }
        }
        return true;
    }

    private boolean networkAccess() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ForgotPasswordActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

}
