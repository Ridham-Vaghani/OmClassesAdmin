package com.vd.omclasses;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewList extends AppCompatActivity {

    TextView Title;
    ImageView Back;
    RecyclerView recyclerView;
    ArrayList<Student> list = new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    MyStudentListAdapter adapter;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_view_list );

        dialog = new ProgressDialog( this );
        dialog.setMessage( "Loading data.." );
        dialog.setCancelable( false );
        dialog.show();

        bind();

        Title.setText( "Student List" );
        Back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        } );

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child( "Student" );

        databaseReference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Student student = ds.getValue( Student.class );
                    list.add( student );
                    adapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        loadData();

    }

    private void bind() {
        Title = findViewById( R.id.txtTitle );
        Back = findViewById( R.id.ivBack );
        recyclerView = findViewById( R.id.recyclerView );
    }


    private void loadData() {
        adapter = new MyStudentListAdapter( ViewList.this, list );
        LinearLayoutManager layoutManager = new LinearLayoutManager( getApplicationContext() );
        layoutManager.setStackFromEnd( true );
        layoutManager.setReverseLayout( true );
        recyclerView.setLayoutManager( layoutManager );
        recyclerView.setAdapter( adapter );
    }
}
