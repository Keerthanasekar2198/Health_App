package com.android.health;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.health.data.Patient;
import com.android.health.security.Security;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {

    private String phoneNumber,phoneVerificationId,name,password,age;
    private int type;

    private EditText code;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference userReference;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        Intent receiveIntent = getIntent();
        phoneNumber = receiveIntent.getExtras().getString("phone");
        name = receiveIntent.getExtras().getString("name");
        password = receiveIntent.getExtras().getString("pass");
        age = receiveIntent.getExtras().getString("age");
        type = receiveIntent.getExtras().getInt("type");

        progressDialog = new ProgressDialog(PhoneAuthActivity.this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

        userReference = mFirebaseDatabase.getReference().child("users").child("patients");

        sendCode();

        Button verify = findViewById(R.id.verify);
        code = findViewById(R.id.otpnumber);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (networkAccess()){
                    if (TextUtils.isEmpty(code.getText().toString())){
                        code.requestFocus();
                        code.setError(getString(R.string.empty_otp));
                    }else {
                        verifyCode();
                    }
                }else {
                    Toast.makeText(PhoneAuthActivity.this,"Please Check your Network Connectivity!!!",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void sendCode(){

        progressDialog.setTitle("Please Wait!!!");
        progressDialog.setMessage("Sending OTP...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationStateChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(PhoneAuthActivity.this, "Invalid Request" + phoneNumber, Toast.LENGTH_LONG).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(PhoneAuthActivity.this, "sms Quota Exceed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                phoneVerificationId = s;
                Toast.makeText(PhoneAuthActivity.this, "OTP Successfully sent to " + phoneNumber, Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                verificationStateChangedCallbacks
        );

    }


    private void verifyCode() {
        String Code = code.getText().toString();

        progressDialog.setTitle("Please Wait!!!");
        progressDialog.setMessage("Verifying...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId, Code);

        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            user.delete().addOnCompleteListener(PhoneAuthActivity.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task1) {
                                    if (task1.isSuccessful()){
                                        if (type == 2){
                                            Intent changePasswordIntent = new Intent(PhoneAuthActivity.this,ChangePasswordActivity.class);
                                            changePasswordIntent.putExtra("phone",phoneNumber);
                                            changePasswordIntent.putExtra("pass",password);
                                            changePasswordIntent.putExtra("age",age);
                                            startActivity(changePasswordIntent);
                                        }else{
                                            createAccount();
                                        }
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(PhoneAuthActivity.this,"Error in User Creation",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    private void createAccount(){
        progressDialog.setMessage("Registering Account...");
        progressDialog.setCanceledOnTouchOutside(false);
        mFirebaseAuth.createUserWithEmailAndPassword(phoneNumber+"@healthplus.com",password)
                .addOnCompleteListener(PhoneAuthActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task2) {
                        if (task2.isSuccessful()){
                            FirebaseUser user1 = task2.getResult().getUser();
                            intent(user1);
                        }else {
                            Toast.makeText(PhoneAuthActivity.this,"Error in Account Creation",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    private void intent(FirebaseUser user1){
        progressDialog.setMessage("Creating Account...");
        progressDialog.setCanceledOnTouchOutside(false);
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        user1.updateProfile(changeRequest)
                .addOnCompleteListener(PhoneAuthActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task3) {
                        if (task3.isSuccessful()){
                            Toast.makeText(PhoneAuthActivity.this,"Account Created Successfully:)",Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(PhoneAuthActivity.this,"Error in Account Details",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
        final Security security = new Security(phoneNumber,0);
        Patient patient = new Patient(security.encrypt(name), phoneNumber,security.encrypt(age),security.encrypt("m"),security.encrypt(password));
        userReference.child(user1.getUid()).setValue(patient);
        progressDialog.dismiss();
        Intent intent = new Intent(PhoneAuthActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
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
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("Do you want to cancel current Process???\nNote:Canceling Process may lead to wait for 15 minutes for accessing OTP Service");
        dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(PhoneAuthActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
        dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null){
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
}
