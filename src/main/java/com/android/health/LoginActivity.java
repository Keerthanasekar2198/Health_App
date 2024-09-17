package com.android.health;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.health.data.Patient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private EditText phoneNumberEditText,passwordEditText;
    private ImageView toggle;

    private ProgressDialog progressDialog;

    String number,password;

    private DatabaseReference patientReference,doctorReference,staffReference;
    private FirebaseAuth mFirebaseAuth;

    ArrayList<String> patientList=new ArrayList<>(),doctorList = new ArrayList<>(),staffList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Button login = findViewById(R.id.log_in);
        TextView forgotPasswordView = findViewById(R.id.forgot_pass);
        TextView signUpView = findViewById(R.id.create);

        phoneNumberEditText = findViewById(R.id.number);
        passwordEditText = findViewById(R.id.password);
        toggle = findViewById(R.id.toggle);

        progressDialog = new ProgressDialog(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        patientReference = FirebaseDatabase.getInstance().getReference().child("users").child("patients");
        doctorReference = FirebaseDatabase.getInstance().getReference().child("users").child("doctors");
        staffReference = FirebaseDatabase.getInstance().getReference().child("users").child("staffs");

        patientReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Patient patient = dataSnapshot.getValue(Patient.class);
                patientList.add(patient.getPhoneNumber());
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

        doctorReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Patient patient = dataSnapshot.getValue(Patient.class);
                doctorList.add(patient.getPhoneNumber());
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

        staffReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Patient patient = dataSnapshot.getValue(Patient.class);
                staffList.add(patient.getPhoneNumber());
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

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (passwordEditText.getText().length() > 0){
                    toggle.setVisibility(View.VISIBLE);
                    passwordEditText.setTextSize(17);
                }else {
                    toggle.setVisibility(View.GONE);
                    passwordEditText.setTextSize(17);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number = phoneNumberEditText.getText().toString();
                password = passwordEditText.getText().toString();
                if (networkAccess()) {
                    if (verifyDetails()) {
                        progressDialog.setMessage("Verifying Details...");
                        progressDialog.show();
                        progressDialog.setCanceledOnTouchOutside(false);
                        login(number, password);
                    }else {
                        Toast.makeText(LoginActivity.this,"Create account to Login",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Snackbar snackbar = Snackbar.make(view,"Please Check your Network Connectivity!!!",Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

        signUpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        forgotPasswordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void login(final String number, String password){
        mFirebaseAuth.signInWithEmailAndPassword(number+"@healthplus.com",password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            int type = 0;
                            if (patientList.contains(number)){
                                type = 1;
                            }else if (doctorList.contains(number)){
                                type = 2;
                            }else if (staffList.contains(number)){
                                type = 3;
                            }
                            MainActivity.typePreference.edit().putInt("u_type",type).apply();
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            intent.putExtra("type",type);
                            startActivity(intent);
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });
    }

    private boolean verifyDetails(){
        if (TextUtils.isEmpty(number)){
            phoneNumberEditText.requestFocus();
            phoneNumberEditText.setError(getString(R.string.number_null_error));
            return false;
        }

        if (number.length()!=10){
            phoneNumberEditText.requestFocus();
            phoneNumberEditText.setError(getString(R.string.number_valid_error));
            return false;
        }

        if (TextUtils.isEmpty(password)){
            passwordEditText.requestFocus();
            passwordEditText.setError(getString(R.string.password_null_error));
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
        finish();
        super.onBackPressed();
    }

}
