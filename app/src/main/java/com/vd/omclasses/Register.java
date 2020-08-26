package com.vd.omclasses;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class Register extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button btnRegister;
    CircleImageView imgProfile;
    EditText etName, etNumber, etAddress, etEmail, etPassword, etNumber1, etParents;
    ImageView Back;
    TextView Title;
    String name, number, address, email, password, gender, std, number1, occupation;
    FirebaseAuth firebaseAuth;
    static int REQUEST_CODE = 1;
    Uri pickedImg;
    DatabaseReference mDataRef;
    FirebaseUser user;
    RadioButton Male, Female;
    Spinner etStd;
    String standard[] = {"8", "9", "10", "11 Arts", "11 Commerce", "12 Arts", "12 Commerce"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register );

        bind();

        etStd.setOnItemSelectedListener( this );

        ArrayAdapter arrayAdapter = new ArrayAdapter( this, android.R.layout.simple_spinner_item, standard );
        arrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        etStd.setAdapter( arrayAdapter );


        firebaseAuth = FirebaseAuth.getInstance();
        mDataRef = FirebaseDatabase.getInstance().getReference( "Student" );

        Title.setText( "Add Student" );
        Back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        } );

        imgProfile.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( Intent.ACTION_GET_CONTENT );
                intent.setType( "image/*" );
                startActivityForResult( intent, REQUEST_CODE );
            }
        } );

        btnRegister.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etName.getText().toString().trim();
                number = etNumber.getText().toString().trim();
                address = etAddress.getText().toString().trim();
                email = etEmail.getText().toString().trim();
                password = etPassword.getText().toString();
                number1 = etNumber1.getText().toString();
                occupation = etParents.getText().toString();

                if (Male.isChecked()) {
                    gender = "Male";
                }
                if (Female.isChecked()) {
                    gender = "Female";
                }

                if (TextUtils.isEmpty( name )) {
                    etName.setError( "Enter your Full Name" );
                    etName.requestFocus();
                } else if(TextUtils.isEmpty( address )){
                    etAddress.setError( "Enter your Address" );
                    etAddress.requestFocus();
                }else if (TextUtils.isEmpty( number )) {
                    etNumber.setError( "Enter your Mobile Number" );
                    etNumber.requestFocus();
                } else if (TextUtils.isEmpty( std )) {
                    Toast.makeText( Register.this, "Select Standard", Toast.LENGTH_SHORT ).show();
                } else if (TextUtils.isEmpty( number1 )) {
                    etNumber1.setError( "Enter your Secondary Number" );
                    etNumber1.requestFocus();
                } else if (TextUtils.isEmpty( occupation )) {
                    etParents.setError( "Enter your Occupation" );
                    etParents.requestFocus();
                } else if (TextUtils.isEmpty( email )) {
                    etEmail.setError( "Enter your Email" );
                    etEmail.requestFocus();
                } else if (TextUtils.isEmpty( password )) {
                    etPassword.setError( "Enter your Password" );
                    etPassword.requestFocus();
                } else if (pickedImg == null) {
                    Toast.makeText( Register.this, "Please select Image", Toast.LENGTH_SHORT ).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher( email ).matches()) {
                    etEmail.setError( "Please Enter Valid Email" );
                    etEmail.requestFocus();
                } else if (TextUtils.isEmpty( gender )) {
                    Toast.makeText( Register.this, "Please select Gender", Toast.LENGTH_SHORT ).show();
                } else {
                    startRegister( name, gender, std, number, number1, occupation, address, email, password );
                }
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

    private void startRegister(final String name, final String gender, final String standard, final String number, final String number1, final String occupation, final String address, final String email, final String password) {
        final ProgressDialog progressDialog = new ProgressDialog( this );
        progressDialog.setMessage( "Please wait..." );
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword( this.email, this.password ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                user = firebaseAuth.getCurrentUser();
                final String id = user.getUid();
                if (task.isSuccessful()) {
                    StorageReference ref = FirebaseStorage.getInstance().getReference( firebaseAuth.getUid() ).child( "Student" );
                    final StorageReference reference = ref.child( pickedImg.getLastPathSegment() );
                    reference.putFile( pickedImg ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                            .setDisplayName( Register.this.name )
                                            .setPhotoUri( uri )
                                            .build();

                                    Student student = new Student(id, name, gender, standard, number, number1, occupation, address, email, password );
                                    mDataRef.child( id ).setValue( student );

                                    firebaseAuth.getCurrentUser().updateProfile( userProfileChangeRequest ).addOnCompleteListener( new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                etAddress.setText( "" );
                                                etEmail.setText( "" );
                                                etName.setText( "" );
                                                etNumber.setText( "" );
                                                etPassword.setText( "" );
                                                etNumber1.setText( "" );
                                                etParents.setText( "" );
                                                if (Male.isChecked()) {
                                                    Male.setChecked( false );
                                                }
                                                if (Female.isChecked()) {
                                                    Female.setChecked( false );
                                                }
                                                imgProfile.setImageResource( R.drawable.ic_add_black );
                                                Toast.makeText( Register.this, "Registration Success", Toast.LENGTH_SHORT ).show();
                                                startActivity( new Intent( Register.this, ViewList.class ) );
                                            }
                                        }
                                    } );
                                }
                            } );
                        }
                    } );
                } else {
                    progressDialog.dismiss();
                    Toast.makeText( Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT ).show();
                }

            }
        } );
    }


    private void bind() {
        imgProfile = findViewById( R.id.imgProfile );
        btnRegister = findViewById( R.id.btnRegister );
        etName = findViewById( R.id.etName );
        etNumber = findViewById( R.id.etNumber );
        etAddress = findViewById( R.id.etAddress );
        etEmail = findViewById( R.id.etEmail );
        etPassword = findViewById( R.id.etPassword );
        Back = findViewById( R.id.ivBack );
        Title = findViewById( R.id.txtTitle );
        Male = findViewById( R.id.male );
        Female = findViewById( R.id.female );
        etStd = findViewById( R.id.etStd );
        etNumber1 = findViewById( R.id.etNumber1 );
        etParents = findViewById( R.id.etParents );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE && data != null) {
            pickedImg = data.getData();
            imgProfile.setImageURI( pickedImg );
        }
    }
}
