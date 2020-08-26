package com.vd.omclasses;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {

    TextView txtTitle;
    ImageView ivBack;
    EditText etEmail, etPassword;
    Button btnLogin;
    String email, password;
    FirebaseAuth firebaseAuth;
    public static final int MULTIPLE_PERMISSIONS = 10;
    String[] permissions = new String[]{Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE};
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        bind();

        if (checkAndRequestPermissions()) {
        }

        txtTitle.setText( "SignIn" );
        ivBack.setVisibility( View.GONE );

        firebaseAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = etEmail.getText().toString().trim();
                password = etPassword.getText().toString();

                if (TextUtils.isEmpty( email )) {
                    etEmail.setError( "Enter Email" );
                    etEmail.requestFocus();
                } else if (TextUtils.isEmpty( password )) {
                    etPassword.setError( "Enter Password" );
                    etPassword.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher( email ).matches()) {
                    etEmail.setError( "Please Enter Valid Email" );
                    etEmail.requestFocus();
                } else {
                    startLogin( email, password );
                }
            }
        } );

    }

    private boolean checkAndRequestPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission( this, p );
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add( p );
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions( this, listPermissionsNeeded.toArray( new String[listPermissionsNeeded.size()] ), MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissionsList[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    String permissionsDenied = "";
                    for (String per : permissionsList) {
                        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                            permissionsDenied += "\n" + per;
                        }
                    }
                }
            }
        }
    }

    private void startLogin(final String email, String password) {
        progressDialog = new ProgressDialog( this );
        progressDialog.setMessage( "Please wait.." );
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword( email, password ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    checkData();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText( Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT ).show();
                }
            }
        } );
    }

    private void checkData() {
        try {
            databaseReference = FirebaseDatabase.getInstance().getReference().child( "Admin" );
            databaseReference.addChildEventListener( new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    progressDialog.dismiss();
                    String value = dataSnapshot.getValue( String.class );
                    if (value.equals( email )) {
                        startLogin();
                    } else {
                        Toast.makeText( Login.this, "You are not Authorised", Toast.LENGTH_SHORT ).show();
                    }
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
            } );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void startLogin() {
        startActivity( new Intent( Login.this, Home.class ) );
        finish();
    }

    private void bind() {
        txtTitle = findViewById( R.id.txtTitle );
        ivBack = findViewById( R.id.ivBack );
        etEmail = findViewById( R.id.etEmail );
        etPassword = findViewById( R.id.etPassword );
        btnLogin = findViewById( R.id.btnLogin );
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            startLogin();
        }
    }
}