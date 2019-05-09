package com.sevencrayons.compass;

/*  This is the default activity that opens
    when a user opens the application.
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.sevencrayons.compass.CompassActivity;
import com.sevencrayons.compass.CreateTrail;
import com.sevencrayons.compass.R;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

    }

    public void openCreateTrail(View view) {
        Intent intent = new Intent(this, CreateTrail.class);
        startActivity(intent);
    }

    public void openViewTrail(View view) {
        Intent intent = new Intent(this, CompassActivity.class);
        startActivity(intent);
    }
}
