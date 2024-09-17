package com.android.health.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.health.R;
import com.android.health.data.Patient;
import com.android.health.security.Security;

import java.util.List;

public class PatientListAdapter extends ArrayAdapter<Patient> {

    public PatientListAdapter(@NonNull Context context, @NonNull List<Patient> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Patient patient = getItem(position);

        if (convertView == null){
            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.patient_list_item,parent,false);
        }

        Security security = new Security(patient.getPhoneNumber(),2);

        TextView nameTextView = convertView.findViewById(R.id.name);
        TextView numberTextView = convertView.findViewById(R.id.number);

        nameTextView.setText(security.decrypt(patient.getName()));
        numberTextView.setText(patient.getPhoneNumber());

        return convertView;
    }
}
