package com.android.health;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.health.data.RecordFile;
import com.android.health.security.Security;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class PatientUploadRecordsActivity extends AppCompatActivity {

    ProgressBar progressBar;
    private TextView emptyTextView;

    private String uid = "",phoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_upload_records);


        final ListView listView = findViewById(R.id.records_list);
        progressBar = findViewById(R.id.progress_bar);
        emptyTextView = findViewById(R.id.no_records);

        FloatingActionButton floatingActionButton = findViewById(R.id.upload_records);

        if (getIntent().getExtras() != null){
            uid = getIntent().getExtras().getString("u_id");
            phoneNumber = getIntent().getExtras().getString("ph_no");
        }

        DatabaseReference recordsReference = FirebaseDatabase.getInstance().getReference().child(uid).child("records");

        final ArrayAdapter<String> recordAdapter = new ArrayAdapter<>(PatientUploadRecordsActivity.this,android.R.layout.simple_list_item_1,new ArrayList<String>());

        recordsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    for (DataSnapshot ds:dataSnapshot.getChildren()){
                        progressBar.setVisibility(View.GONE);
                        RecordFile recordFile = ds.getValue(RecordFile.class);
                        Security security = new Security(phoneNumber,2);
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

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(PatientUploadRecordsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("application/*");
                    startActivityForResult(intent,2);
                }else {
                    Intent intent = new Intent();
                    intent.setType("application/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Select Pdf"),2);
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null){
            progressBar.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);


            Uri uri = data.getData();
            final String id = DocumentsContract.getDocumentId(uri);
            final Uri contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            Log.e(cursor.getString(column_index)," huj");
            final File file = new File(cursor.getString(column_index));

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(uid).child("records").child(file.getName());
            storageReference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    progressBar.setVisibility(View.GONE);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(uid).child("records");
                    Security security = new Security(phoneNumber,2);

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
                    final String upload_time = format1.format(calendar.getTime());

                    RecordFile recordFile = new RecordFile(security.encrypt(task.getResult().toString()),security.encrypt(file.getName()),security.encrypt(upload_time));

                    reference.push().setValue(recordFile);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("fil","hj");
                }
            });
        }

    }
}
