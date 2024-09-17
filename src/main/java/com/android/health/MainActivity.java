package com.android.health;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.health.Fragments.DashboardFragment;
import com.android.health.Fragments.DoctorListFragment;
import com.android.health.Fragments.DoctorViewAppointmentFragment;
import com.android.health.Fragments.PatientListFragment;
import com.android.health.Fragments.PatientViewRecordsFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    public static SharedPreferences securityPreferences,typePreference;

    private static DrawerLayout mDrawer;
    private static FragmentManager fragmentManager;
    private ActionBarDrawerToggle mToggle;
    public static NavigationView mNavigationView;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser user;

    public static int menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawer = findViewById(R.id.drawer);
        mNavigationView = findViewById(R.id.nav_view);

        fragmentManager = getSupportFragmentManager();

        mToggle = new ActionBarDrawerToggle(this,mDrawer,R.string.open,R.string.close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNavigationView.bringToFront();

        mFirebaseAuth = FirebaseAuth.getInstance();

        typePreference = getSharedPreferences("u_type",Context.MODE_PRIVATE);

        securityPreferences = getSharedPreferences("sec_key", Context.MODE_PRIVATE);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mNavigationView.getMenu().findItem(menuItem).setChecked(false);
                mNavigationView.getMenu().findItem(item.getItemId()).setChecked(true);
                Fragment fragment = null;
                Class fragmentClass;
                switch (item.getItemId()){
                    case R.id.staff_upload_records:
                        fragmentClass = PatientListFragment.class;
                        break;
                    case R.id.patient_records:
                        fragmentClass = PatientViewRecordsFragment.class;
                        break;
                    case R.id.patient_appointment:
                        fragmentClass = DoctorListFragment.class;
                        break;
                    case R.id.doctor_appointment:
                        fragmentClass = DoctorViewAppointmentFragment.class;
                        break;
                    case R.id.patient_payment:
                        Uri uri = Uri.parse("http://keerthana62tourism.s3-website.ap-south-1.amazonaws.com/");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    default:
                        fragmentClass = DashboardFragment.class;
                }
                menuItem = item.getItemId();

                try{
                    fragment = (Fragment)fragmentClass.newInstance();
                }catch (Exception e){
                    e.printStackTrace();
                }

                applyFragment(fragment);

                return false;
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = mFirebaseAuth.getCurrentUser();
                if (user == null){
                    Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivityForResult(loginIntent,1);
                }
            }
        };

        DashBoardFragment();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if(resultCode == RESULT_CANCELED){
                finish();
            }
        }
    }

    private void DashBoardFragment(){
        menuItem = R.id.dashboard;
        Fragment fragment = new DashboardFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().replace(R.id.fragment, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)){
            return true;
        }else if (item.getItemId() == R.id.log_out){
            if (networkAccess()){
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                dialogBuilder.setMessage("Are You want to quit Chat??");
                dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AuthUI.getInstance().signOut(MainActivity.this);
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
            }else {
                Toast.makeText(MainActivity.this,"Please Check your Network Connectivity!!!",Toast.LENGTH_LONG).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (typePreference.getInt("u_type",0)==1){
            mNavigationView.getMenu().clear();
            mNavigationView.inflateMenu(R.menu.patient_menu);
            Log.e("mani","cd  1");
            mNavigationView.getMenu().findItem(R.id.dashboard).setChecked(true);
        }else if (typePreference.getInt("u_type",0)==2){
            mNavigationView.getMenu().clear();
            Log.e("mani","cd  2");
            mNavigationView.inflateMenu(R.menu.doctor_menu);
            mNavigationView.getMenu().findItem(R.id.dashboard).setChecked(true);
        }else if(typePreference.getInt("u_type",0)==3){
            mNavigationView.getMenu().clear();
            Log.e("mani","cd  3");
            mNavigationView.inflateMenu(R.menu.staff_menu);
            mNavigationView.getMenu().findItem(R.id.dashboard).setChecked(true);
        }
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAuthStateListener != null){
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
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
        if (menuItem == R.id.dashboard){
            finish();
        }else {
            DashBoardFragment();
        }
    }

    public static void applyFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().replace(R.id.fragment,fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
        mDrawer.closeDrawer(GravityCompat.START);
    }


}
