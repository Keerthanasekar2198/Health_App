package com.android.health;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.health.data.Patient;
import com.android.health.security.Security;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText newPass,rePass;
    private DatabaseReference reference;
    private FirebaseAuth mFirebaseAuth;
    private boolean logOut = true;

    private ProgressDialog progressDialog;

    private String phoneNumber,age;
    private String password;
    private String rp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        setTitle("Change Password");

        newPass = findViewById(R.id.new_password);
        rePass = findViewById(R.id.retype_new_password);

        Intent receiveIntent = getIntent();

        progressDialog = new ProgressDialog(this);

        if (receiveIntent.getExtras() != null){
            phoneNumber = receiveIntent.getExtras().getString("phone");
            password = receiveIntent.getExtras().getString("pass");
            age = receiveIntent.getExtras().getString("age");
        }

        mFirebaseAuth = FirebaseAuth.getInstance();

        Security security = new Security(phoneNumber,0);
        mFirebaseAuth.signInWithEmailAndPassword(phoneNumber+"@healthplus.com"
                ,security.decrypt(password))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChangePasswordActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

        reference = FirebaseDatabase.getInstance().getReference().child("users").child("patients");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.next_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.next){
            if (networkAccess()){
                if (verifyDetails()){
                    progressDialog.setTitle("Please Wait!!!");
                    progressDialog.setMessage("Updating Password");
                    progressDialog.show();
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.updatePassword(rp)
                            .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Security security = new Security(user.getEmail().substring(0,10),0);
                                        Patient users = new Patient(security.encrypt(user.getDisplayName())
                                                ,user.getEmail().substring(0,10)
                                                ,security.encrypt(age)
                                                ,security.encrypt("m")
                                                ,security.encrypt(rePass.getText().toString()));
                                        reference.child(user.getUid()).setValue(users);
                                        logOut = false;
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(ChangePasswordActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }).addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChangePasswordActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }else {
                Toast.makeText(ChangePasswordActivity.this,"Please Check your Network Connectivity!!!",Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        if (logOut) {
            AuthUI.getInstance().signOut(ChangePasswordActivity.this);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        Security security = new Security(phoneNumber,0);
        mFirebaseAuth.signInWithEmailAndPassword(phoneNumber+"@healthplus.com"
                ,security.decrypt(password))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChangePasswordActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
        super.onResume();
    }

    private boolean verifyDetails(){

        String p = newPass.getText().toString().trim();
        rp = rePass.getText().toString().trim();

        if (TextUtils.isEmpty(p)){
            newPass.requestFocus();
            newPass.setError(getString(R.string.empty_password));
            return false;
        }

        if (!(p.length()>8)) {
            newPass.requestFocus();
            newPass.setError(getString(R.string.missing_length));
            return false;
        }

        if (!(p.equals(rp))){
            rePass.requestFocus();
            rePass.setError(getString(R.string.mismatch));
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
        Intent intent = new Intent(ChangePasswordActivity.this,ForgotPasswordActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

}
