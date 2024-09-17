package com.android.health;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.health.data.Patient;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {

    private EditText nameEditText,numberEditText
            ,passwordEditText,rePasswordEditText,ageEditText;

    private ImageView toggle,toggle1;

    private String names;
    private String phone;
    private String p;

    private ArrayList<String> patientsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameEditText = findViewById(R.id.name);
        numberEditText = findViewById(R.id.number);
        ageEditText = findViewById(R.id.age);
        passwordEditText = findViewById(R.id.password);
        rePasswordEditText = findViewById(R.id.repassword);

        toggle = findViewById(R.id.toggle);
        toggle1 = findViewById(R.id.toggle1);

        Button signUp = findViewById(R.id.submit);

        FirebaseDatabase socialMediaDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userReference = socialMediaDatabase.getReference().child("users").child("patients");

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (String.valueOf(toggle.getTag()).equals("visible_on")){
                    toggle.setImageResource(R.drawable.invisible);
                    passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordEditText.setSelection(passwordEditText.length());
                    passwordEditText.setTextSize(17);
                    toggle.setTag("visible_off");
                }else {
                    toggle.setImageResource(R.drawable.visible);
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordEditText.setSelection(passwordEditText.length());
                    passwordEditText.setTextSize(17);
                    toggle.setTag("visible_on");
                }
            }
        });


        toggle1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (String.valueOf(toggle1.getTag()).equals("visible_on")){
                    toggle1.setImageResource(R.drawable.invisible);
                    rePasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    rePasswordEditText.setSelection(rePasswordEditText.length());
                    rePasswordEditText.setTextSize(17);
                    toggle1.setTag("visible_off");
                }else {
                    toggle1.setImageResource(R.drawable.visible);
                    rePasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    rePasswordEditText.setSelection(rePasswordEditText.length());
                    rePasswordEditText.setTextSize(17);
                    toggle1.setTag("visible_on");
                }
            }
        });



        userReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Patient patient = dataSnapshot.getValue(Patient.class);
                patientsList.add(patient.getPhoneNumber());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(networkAccess()){
                    if (verifyDetails()){
                        Intent intent = new Intent(SignUpActivity.this,PhoneAuthActivity.class);
                        intent.putExtra("phone",phone);
                        intent.putExtra("name",names);
                        intent.putExtra("pass",p);
                        intent.putExtra("age",ageEditText.getText().toString());
                        intent.putExtra("type",1);
                        startActivity(intent);
                        nameEditText.setText("");
                        passwordEditText.setText("");
                        numberEditText.setText("");
                        rePasswordEditText.setText("");
                        finish();
                    }
                }else {
                    Toast.makeText(SignUpActivity.this,"Please Check your Network Connectivity!!!",Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private boolean verifyDetails(){

        names = nameEditText.getText().toString().trim();
        phone = numberEditText.getText().toString().trim();
        p = passwordEditText.getText().toString().trim();
        String rp = rePasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(names)){
            nameEditText.requestFocus();
            nameEditText.setError(getString(R.string.empty_user));
            return false;
        }

        if (TextUtils.isEmpty(phone)){
            numberEditText.requestFocus();
            numberEditText.setError(getString(R.string.empty_phone));
            return false;
        }

        if (phone.length()!=10){
            numberEditText.requestFocus();
            numberEditText.setError(getString(R.string.invalid_phone));
            return false;
        }

        if (patientsList.contains(phone)){
            numberEditText.requestFocus();
            numberEditText.setError(getString(R.string.acc_exists));
            return false;
        }

        if (TextUtils.isEmpty(p)){
            passwordEditText.requestFocus();
            passwordEditText.setError(getString(R.string.empty_password));
            return false;
        }

        if (!(p.length()>8)) {
            passwordEditText.requestFocus();
            passwordEditText.setError(getString(R.string.missing_length));
            return false;
        }

        if (!(p.equals(rp))){
            rePasswordEditText.requestFocus();
            rePasswordEditText.setError(getString(R.string.mismatch));
            return false;
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
        Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

}
