package com.vd.omclasses;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Upload extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ImageView Back;
    TextView Title;
    EditText Link;
    Button Upload, Video;
    DatabaseReference mDataRef, reference;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    Spinner spinner;
    String standard[] = {"8", "9", "10", "11 Arts", "11 Commerce", "12 Arts", "12 Commerce"};
    String std;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_upload );

        Upload = findViewById( R.id.btnUpload );
        Title = findViewById( R.id.txtTitle );
        Back = findViewById( R.id.ivBack );
        Link = findViewById( R.id.etLink );
        spinner = findViewById( R.id.spinner );

        firebaseAuth = FirebaseAuth.getInstance();
        mDataRef = FirebaseDatabase.getInstance().getReference( "Video" );
        reference = FirebaseDatabase.getInstance().getReference( "Videolist" );

        Back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        } );
        Title.setText( "Upload Video " );

        spinner.setOnItemSelectedListener( this );
        ArrayAdapter arrayAdapter = new ArrayAdapter( this, android.R.layout.simple_spinner_item, standard );
        arrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spinner.setAdapter( arrayAdapter );
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        std = standard[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    public void upload(View view) {
        if (Link.getText().toString().isEmpty()) {
            Link.setError( "Please Enter Link" );
        } else if (TextUtils.isEmpty( std )) {
            Toast.makeText( this, "Please select Standard", Toast.LENGTH_SHORT ).show();
        } else {
            startUpload( Link.getText().toString().trim() );
        }
    }

    private void startUpload(String link) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat( "dd-MM-yyyy HH:mm:ss" );
        String formattedDate = df.format( c );

        user = firebaseAuth.getCurrentUser();
        final String id = user.getUid();

        mDataRef.child( std ).child( formattedDate ).setValue( link ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Link.setText( "" );
                    Toast.makeText( Upload.this, "Upload Success", Toast.LENGTH_SHORT ).show();
                } else {
                    Toast.makeText( Upload.this, task.getException().toString(), Toast.LENGTH_SHORT ).show();
                }
            }
        } );

        reference.child( formattedDate ).setValue( link ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Link.setText( "" );
                } else {
                    Toast.makeText( Upload.this, task.getException().toString(), Toast.LENGTH_SHORT ).show();
                }
            }
        } );
    }
}