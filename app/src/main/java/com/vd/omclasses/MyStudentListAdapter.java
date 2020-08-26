package com.vd.omclasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Random;

public class MyStudentListAdapter extends RecyclerView.Adapter<MyStudentListAdapter.ViewHolder> {

    Context context;
    ArrayList<Student> list = new ArrayList<>();

    public MyStudentListAdapter(@NonNull Context context, ArrayList<Student> list) {
        this.context = context;
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtEmail, txtGender, txtNumber, txtNumber1, txtStd, txtFather, txtAddress, txtPassword;
        ImageView imageView, Edit, Delete;

        public ViewHolder(@NonNull View itemView) {
            super( itemView );

            this.txtName = itemView.findViewById( R.id.txtName );
            this.txtEmail = itemView.findViewById( R.id.txtEmail );
            this.txtGender = itemView.findViewById( R.id.txtGender );
            this.txtNumber = itemView.findViewById( R.id.txtNumber );
            this.txtNumber1 = itemView.findViewById( R.id.txtNumber1 );
            this.txtStd = itemView.findViewById( R.id.txtStd );
            this.imageView = itemView.findViewById( R.id.imageView );
            this.txtFather = itemView.findViewById( R.id.txtFather );
            this.txtAddress = itemView.findViewById( R.id.txtAddress );
            this.txtPassword = itemView.findViewById( R.id.txtPassword );
            this.Edit = itemView.findViewById( R.id.imgEdit );
            this.Delete = itemView.findViewById( R.id.imgDelete );
        }
    }


    @NonNull
    @Override
    public MyStudentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from( parent.getContext() );
        View view = layoutInflater.inflate( R.layout.my_custom_list, parent, false );

        return new ViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull final MyStudentListAdapter.ViewHolder holder, final int position) {

        String[] colorlist = context.getResources().getStringArray( R.array.colors );
        final String color = colorlist[randInt( 0, (colorlist.length - 1) )];
        holder.imageView.setBackgroundColor( Color.parseColor( color ) );

        holder.txtName.setText( list.get( position ).getName() );
        holder.txtEmail.setText( list.get( position ).getEmail() );
        holder.txtGender.setText( list.get( position ).getGender() );
        holder.txtNumber.setText( "Student: " + list.get( position ).getNumber() );
        holder.txtNumber1.setText( "Father: " + list.get( position ).getNumber1() );
        holder.txtStd.setText( "Standard: " + list.get( position ).getStandard() );
        holder.txtFather.setText( "Occupation: " + list.get( position ).getOccupation() );
        holder.txtAddress.setText( "Address: " + list.get( position ).getAddress() );
        holder.txtPassword.setText( "Password: " + list.get( position ).getPassword() );

        holder.Edit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( context, Edit.class );
                intent.putExtra( "id", list.get( position ).getId() );
                intent.putExtra( "name", list.get( position ).getName() );
                intent.putExtra( "gender", list.get( position ).getGender() );
                intent.putExtra( "standard", list.get( position ).getStandard() );
                intent.putExtra( "number", list.get( position ).getNumber() );
                intent.putExtra( "number1", list.get( position ).getNumber1() );
                intent.putExtra( "occu", list.get( position ).getOccupation() );
                intent.putExtra( "address", list.get( position ).getAddress() );
                intent.putExtra( "email", list.get( position ).getEmail() );
                intent.putExtra( "pass", list.get( position ).getPassword() );
                context.startActivity( intent );
                ((Activity) context).finish();
            }
        } );

        holder.Delete.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder( context );
                alertDialog.setTitle( "Remove User" );
                alertDialog.setMessage( "Are you sure to remove user?" );
                alertDialog.setPositiveButton( "CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                } );
                alertDialog.setNegativeButton( "YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Remove( position );
                    }
                } );

                AlertDialog dialog = alertDialog.create();
                dialog.show();
            }
        } );
    }

    private void Remove(final int pos) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider
                .getCredential( list.get( pos ).getEmail(), list.get( pos ).getPassword() );

        user.reauthenticate( credential )
                .addOnCompleteListener( new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        user.delete()
                                .addOnCompleteListener( new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            DatabaseReference dbNode = FirebaseDatabase.getInstance().getReference( "Student" ).child( list.get( pos ).getId() );
                                            dbNode.removeValue();
                                            Toast.makeText( context, "User Removed successfully", Toast.LENGTH_SHORT ).show();
                                            Intent intent = new Intent( context, ViewList.class );
                                            context.startActivity( intent );
                                            ((Activity) context).finish();
                                        }
                                    }
                                } );

                    }
                } );
    }

    private int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt( (max - min) + 1 ) + min;
        return randomNum;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}