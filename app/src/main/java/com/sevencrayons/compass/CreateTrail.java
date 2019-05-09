package com.sevencrayons.compass;

/*
    This activity allows a user to create a new trail
    by implementing buttons that trigger recording of the
    user's GPS path.
*/

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static android.location.LocationManager.GPS_PROVIDER;

public class CreateTrail extends AppCompatActivity{
    boolean isRecording = false;
    Trail trail = new Trail();
    Button start;
    Button stop;

    //printing to screen tester
    double x;
    double y;
    TextView xText;
    TextView yText;
    TextView printTrail;
    String coords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trail);
        start = findViewById(R.id.ButtonStart);
        stop = findViewById(R.id.ButtonStop);
        xText = findViewById(R.id.xText);
        yText = findViewById(R.id.yText);
        printTrail = findViewById(R.id.print_trail);

        // stop button should be disabled until recording starts
        stop.setEnabled(false);

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

//        // check if we have gps permission
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  }, 1);
        }
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //makeUseOfNewLocation(location);
                if (isRecording)
                    trail.addNode(location);
                else
                    //printTrail.setText(trail.toString());
                xText.setText("X: " + Double.toString(location.getLatitude()));
                yText.setText("Y: " + Double.toString(location.getLongitude()));
                Log.i("loc:", "lat: " + location.getLatitude() +" long: " + location.getLongitude() + "alt: " + location.getAltitude() + "acc: " +location.getAccuracy());
            }


            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.i("1","accessing the permission check");
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    public void startRecording(View view) {
        isRecording = true;
        start.setEnabled(false);
        stop.setEnabled(true);
    }

    public void stopRecording(View view) throws IOException{
        isRecording = false;
        stop.setEnabled(false);
        start.setEnabled(true);
        String trailName = "trail";
        trailName = trailName.replace("\n", "");
        trailName = trailName.replace(".", "");
        Log.d("name", trailName);
        Gson trailgson = new Gson();
        //trailgson.toJson(trail, new FileWriter("values/"+trailName + ".json"));
        String trailjson = trailgson.toJson(trail);
        File trailfile = new File(this.getFilesDir()+ "/" + trailName+".json");
        if (!trailfile.exists())
            trailfile.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(trailfile));
        bw.write(trailjson);
        bw.close();
        Log.d("r", "trail first loc:" + trail.getNode(0)) ;
    }
}
