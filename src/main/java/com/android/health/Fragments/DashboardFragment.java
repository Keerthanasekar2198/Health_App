package com.android.health.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.health.Adapters.DashboardAdapter;
import com.android.health.MainActivity;
import com.android.health.ProfileActivity;
import com.android.health.R;
import com.android.health.data.Dashboard;

import java.util.ArrayList;
import java.util.Arrays;


public class DashboardFragment extends Fragment implements DashboardAdapter.DashboardOnclickHandler {

    private RecyclerView dashboardList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard,container,false);

        dashboardList = view.findViewById(R.id.dashboard);

        dashboardList.setHasFixedSize(true);

        if (MainActivity.typePreference.getInt("u_type",0) == 1){
            Dashboard dashboard = new Dashboard(R.drawable.profile,"Profile");
            Dashboard dashboard1 = new Dashboard(R.drawable.viewrecords,"Records");
            Dashboard dashboard2 = new Dashboard(R.drawable.prescription,"Prescription");
            Dashboard dashboard3 = new Dashboard(R.drawable.appointment,"Make an Appointment");
            Dashboard dashboard4 = new Dashboard(R.drawable.payment,"Payment");
            Dashboard dashboard5 = new Dashboard(R.drawable.finddoctor,"Find Doctors");
            Dashboard dashboard6 = new Dashboard(R.drawable.settings,"Settings");
            Dashboard dashboard7 = new Dashboard(R.drawable.about,"About");
            ArrayList<Dashboard> pictures = new ArrayList<>(Arrays.asList(dashboard
                    ,dashboard1
                    ,dashboard2
                    ,dashboard3
                    ,dashboard4
                    ,dashboard5
                    ,dashboard6
                    ,dashboard7));
            DashboardAdapter dashBoardAdapter = new DashboardAdapter( this,pictures);
            dashboardList.setAdapter(dashBoardAdapter);
            Log.e("m","j1");
        }else if (MainActivity.typePreference.getInt("u_type",0) == 2){
            Dashboard dashboard = new Dashboard(R.drawable.profile,"Profile");
            Dashboard dashboard1 = new Dashboard(R.drawable.records,"Patient Records");
            Dashboard dashboard2 = new Dashboard(R.drawable.appointmentview,"View Appointment");
            Dashboard dashboard3 = new Dashboard(R.drawable.finddoctor,"Find Staffs");
            Dashboard dashboard4 = new Dashboard(R.drawable.hospital,"Hospital Maintenance");
            Dashboard dashboard5 = new Dashboard(R.drawable.viewrecords,"View Records");
            Dashboard dashboard6 = new Dashboard(R.drawable.settings,"Settings");
            Dashboard dashboard7 = new Dashboard(R.drawable.about,"About");
            ArrayList<Dashboard> pictures = new ArrayList<>(Arrays.asList(dashboard
                    ,dashboard1
                    ,dashboard2
                    ,dashboard3
                    ,dashboard4
                    ,dashboard5
                    ,dashboard6
                    ,dashboard7));
            DashboardAdapter dashBoardAdapter = new DashboardAdapter( this,pictures);
            dashboardList.setAdapter(dashBoardAdapter);
        }else if (MainActivity.typePreference.getInt("u_type",0) == 3){
            Dashboard dashboard = new Dashboard(R.drawable.profile,"Profile");
            Dashboard dashboard1 = new Dashboard(R.drawable.viewrecords,"Patient Records");
            Dashboard dashboard2 = new Dashboard(R.drawable.upload,"Upload Records");
            Dashboard dashboard3 = new Dashboard(R.drawable.finddoctor,"Find Doctors");
            Dashboard dashboard4 = new Dashboard(R.drawable.payment,"Billing");
            Dashboard dashboard5 = new Dashboard(R.drawable.appointment,"Maintain Appointment");
            Dashboard dashboard6 = new Dashboard(R.drawable.settings,"Settings");
            Dashboard dashboard7 = new Dashboard(R.drawable.about,"About");
            ArrayList<Dashboard> pictures = new ArrayList<>(Arrays.asList(dashboard
                    ,dashboard1
                    ,dashboard2
                    ,dashboard3
                    ,dashboard4
                    ,dashboard5
                    ,dashboard6
                    ,dashboard7));
            DashboardAdapter dashBoardAdapter = new DashboardAdapter( this,pictures);
            dashboardList.setAdapter(dashBoardAdapter);
        }

        return view;
    }

    @Override
    public void dashboardOnclick(int position) {
        if (position == 0){
            Intent profileIntent = new Intent(getContext(),ProfileActivity.class);
            startActivity(profileIntent);
        }
        if (MainActivity.typePreference.getInt("u_type",0) == 1){
            if (position == 1){
                MainActivity.menuItem = R.id.patient_records;
                MainActivity.mNavigationView.getMenu().findItem(R.id.patient_records).setChecked(true);
                Fragment patientViewRecordsFragment = new PatientViewRecordsFragment();
                MainActivity.applyFragment(patientViewRecordsFragment);
            }else if (position == 2){

            }else if (position == 3){
                MainActivity.menuItem = R.id.patient_appointment;
                MainActivity.mNavigationView.getMenu().findItem(R.id.patient_appointment).setChecked(true);
                Fragment doctorListFragment = new DoctorListFragment();
                MainActivity.applyFragment(doctorListFragment);
            }else if (position == 4){

            }else if (position == 5){

            }else if (position == 6){

            }
        }else if (MainActivity.typePreference.getInt("u_type",0) == 2){
            if (position == 1){

            }else if (position == 2){
                MainActivity.menuItem = R.id.doctor_appointment;
                MainActivity.mNavigationView.getMenu().findItem(MainActivity.menuItem).setChecked(true);
                Fragment doctorViewAppointmentFragment = new DoctorViewAppointmentFragment();
                MainActivity.applyFragment(doctorViewAppointmentFragment);
            }else if (position == 3){

            }else if (position == 4){

            }else if (position == 5){

            }else if (position == 6){

            }
        }else if (MainActivity.typePreference.getInt("u_type",0) == 3){
            if (position == 1){

            }else if (position == 2){
                MainActivity.menuItem = R.id.staff_upload_records;
                MainActivity.mNavigationView.getMenu().findItem(R.id.staff_upload_records).setChecked(true);
                Fragment patientListFragment = new PatientListFragment();
                MainActivity.applyFragment(patientListFragment);
            }else if (position == 3){

            }else if (position == 4){

            }else if (position == 5){

            }else if (position == 6){

            }
        }
    }
}
