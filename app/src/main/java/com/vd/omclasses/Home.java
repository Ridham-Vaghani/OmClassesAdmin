package com.vd.omclasses;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity {

    ViewFlipper viewFlipper;
    int images[] = {R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img4, R.drawable.img5};
    ImageView Back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );

        bind();

        Back.setVisibility( View.GONE );

        for (int images : images) {
            flipImages( images );
        }
    }

    private void flipImages(int images) {
        ImageView imageView = new ImageView( this );
        imageView.setBackgroundResource( images );
        viewFlipper.addView( imageView );
        viewFlipper.setFlipInterval( 4000 );
        viewFlipper.setAutoStart( true );
        viewFlipper.setInAnimation( this, android.R.anim.slide_out_right );
        viewFlipper.setOutAnimation( this, android.R.anim.slide_out_right );
    }

    private void bind() {
        viewFlipper = findViewById( R.id.fliper );
        Back = findViewById( R.id.ivBack );
    }

    public void register(View view) {
        startActivity( new Intent( Home.this, Register.class ) );
    }

    public void upload(View view) {
        startActivity( new Intent( Home.this, Upload.class ) );
    }

    public void viewList(View view) {
        startActivity( new Intent( Home.this, ViewList.class ) );
    }

    public void videolist(View view) {
        startActivity( new Intent( Home.this, VideoList.class ) );
    }
}
