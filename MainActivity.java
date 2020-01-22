package com.example.filupload;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.drm.DrmStore;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {

    Button select, upload,nxt;
    TextView status;


    Uri pdfUri;
   // FirebaseAuth firebaseAuth;

    FirebaseStorage storage;

    StorageReference storageReference;
    StorageReference ref;

    FirebaseDatabase database;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();




        //  StorageReference storage;
        //  storage = FirebaseStorage.getInstance().getReference();

        select = findViewById(R.id.recording);
        upload = findViewById(R.id.upload);
        status = findViewById(R.id.status);
        nxt=findViewById(R.id.next);

        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,download.class);
                startActivity(intent);
            }
        });



        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    selectpdf();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                }
            }
        });


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pdfUri != null)
                    uploadFile(pdfUri);
                else
                    Toast.makeText(MainActivity.this, "Select File", Toast.LENGTH_SHORT).show();
            }
        });




    }



    private void selectpdf() {
        Intent in = new Intent();
        //in.setType("application/pdf");
        in.setType("audio/*");

        in.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(in, 86);
    }

    private void uploadFile(Uri pdfUri) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading File");
        progressDialog.setProgress(0);
        progressDialog.show();

        final String fileName = System.currentTimeMillis() + "";
        StorageReference storageReference = storage.getReference();
        storageReference.child("Upload").child(fileName).putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                String url = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString(); //important
                DatabaseReference reference = database.getReference();
                reference.child(fileName).setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Toast.makeText(MainActivity.this, "File Succesfully Uploaded", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(MainActivity.this, "" , Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "File Not Succesfully Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                int currentProgress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectpdf();
        } else {
            Toast.makeText(MainActivity.this, "Please Provide permission", Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 86 && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData();
            status.setText("A file is selected" + data.getData().getLastPathSegment());
        } else {
            Toast.makeText(MainActivity.this, "Select the Pdf", Toast.LENGTH_SHORT).show();
        }

    }



}


/*
        upload.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {

        Intent in=new Intent();
        in.setType("application/pdf");
        in.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(in,1);
        }

        });
        }

@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
        if(requestCode==1)
        {
        Uri uri = null;
        if (data!=null)
        {
        uri=data.getData();
        }
        if (uri!=null)
        {
        String[] filepath={"applicaiton/pdf"};
        Cursor cursor=getContentResolver().query(uri,filepath,null,null,null);
        }
        }
        }
        }*/


/*   ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
@Override
public void onSuccess(Uri uri) {


        }
        }).addOnFailureListener(new OnFailureListener() {
@Override
public void onFailure(@NonNull Exception e) {

        }
        });*/
