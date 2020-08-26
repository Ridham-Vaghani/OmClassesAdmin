package com.vd.omclasses;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Edit extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText etName, etNumber, etAddress, etNumber1, etParents, etEmail, etPassword;
    RadioButton Male, Female;
    Spinner etStd;
    Button btnRegister;
    ImageView Back;
    TextView Title;
    String name, number, address, email, gender, std, number1, occupation, password;
    String standard[] = {"8", "9", "10", "11 Arts", "11 Commerce", "12 Arts", "12 Commerce"};
    Intent intent;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_edit );

        Bind();

        firebaseAuth = FirebaseAuth.getInstance();

        etStd.setOnItemSelectedListener( this );
        ArrayAdapter arrayAdapter = new ArrayAdapter( this, android.R.layout.simple_spinner_item, standard );
        arrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        etStd.setAdapter( arrayAdapter );

        intent = getIntent();

        Title.setText( "Edit" );
        Back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( Edit.this, ViewList.class ) );
                finish();
            }
        } );

        etName.setText( intent.getStringExtra( "name" ) );

        if (intent.getStringExtra( "gender" ).equals( "Male" )) {
            Male.setChecked( true );
        } else {
            Female.setChecked( true );
        }

        int spinnerPosition = arrayAdapter.getPosition( intent.getStringExtra( "standard" ) );
        etStd.setSelection( spinnerPosition );

        etNumber.setText( intent.getStringExtra( "number" ) );
        etNumber1.setText( intent.getStringExtra( "number1" ) );

        etParents.setText( intent.getStringExtra( "occu" ) );

        etAddress.setText( intent.getStringExtra( "address" ) );
        etEmail.setText( intent.getStringExtra( "email" ) );
        etPassword.setText( intent.getStringExtra( "pass" ) );

        btnRegister.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etName.getText().toString().trim();
                number = etNumber.getText().toString().trim();
                address = etAddress.getText().toString().trim();
                number1 = etNumber1.getText().toString();
                occupation = etParents.getText().toString();
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();

                if (Male.isChecked()) {
                    gender = "Male";
                }
                if (Female.isChecked()) {
                    gender = "Female";
                }

                if (TextUtils.isEmpty( name )) {
                    etName.setError( "Enter your Full Name" );
                    etName.requestFocus();
                } else if (TextUtils.isEmpty( password )) {
                    etPassword.setError( "Enter your password" );
                    etPassword.requestFocus();
                } else if (TextUtils.isEmpty( email )) {
                    etEmail.setError( "Enter your Email" );
                    etEmail.requestFocus();
                } else if (TextUtils.isEmpty( address )) {
                    etAddress.setError( "Enter your Address" );
                    etAddress.requestFocus();
                } else if (TextUtils.isEmpty( number )) {
                    etNumber.setError( "Enter your Mobile Number" );
                    etNumber.requestFocus();
                } else if (TextUtils.isEmpty( std )) {
                    Toast.makeText( Edit.this, "Select Standard", Toast.LENGTH_SHORT ).show();
                } else if (TextUtils.isEmpty( number1 )) {
                    etNumber1.setError( "Enter your Secondary Number" );
                    etNumber1.requestFocus();
                } else if (TextUtils.isEmpty( occupation )) {
                    etParents.setError( "Enter your Occupation" );
                    etParents.requestFocus();
                } else {
                    startUpdate( intent.getStringExtra( "id" ), name, gender, std, number, number1, occupation, address, email, password );
                }
            }
        } );
    }

    private void startUpdate(final String id, final String name, final String gender, final String std, final String number, final String number1, final String occupation, final String address, final String email, final String password) {
        final ProgressDialog progressDialog = new ProgressDialog( this );
        progressDialog.setMessage( "Please wait..." );
        progressDialog.show();

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference( "Student" );
        mDatabase.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Student student = new Student( id, name, gender, std, number, number1, occupation, address, email, password );
                mDatabase.child( id ).setValue( student );

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                AuthCredential credential = EmailAuthProvider.getCredential( intent.getStringExtra( "email" ), intent.getStringExtra( "pass" ) );
                firebaseUser.reauthenticate( credential ).addOnCompleteListener( new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.updateEmail( email );
                        user.updatePassword( password );
                        progressDialog.dismiss();
                        Toast.makeText( Edit.this, "Update Successful", Toast.LENGTH_SHORT ).show();

                        etAddress.setText( "" );
                        etEmail.setText( "" );
                        etName.setText( "" );
                        etNumber.setText( "" );
                        etNumber1.setText( "" );
                        etParents.setText( "" );
                        if (Male.isChecked()) {
                            Male.setChecked( false );
                        }
                        if (Female.isChecked()) {
                            Female.setChecked( false );
                        }
                    }
                } );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        std = standard[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    private void Bind() {
        btnRegister = findViewById( R.id.btnRegister );
        etName = findViewById( R.id.etName );
        etNumber = findViewById( R.id.etNumber );
        etAddress = findViewById( R.id.etAddress );
        Back = findViewById( R.id.ivBack );
        Title = findViewById( R.id.txtTitle );
        Male = findViewById( R.id.male );
        Female = findViewById( R.id.female );
        etStd = findViewById( R.id.etStd );
        etNumber1 = findViewById( R.id.etNumber1 );
        etParents = findViewById( R.id.etParents );
        etEmail = findViewById( R.id.etEmail );
        etPassword = findViewById( R.id.etPassword );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity( new Intent( Edit.this, ViewList.class ) );
        finish();
    }
}