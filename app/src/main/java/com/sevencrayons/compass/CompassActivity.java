package com.sevencrayons.compass;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
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
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;

import static android.location.LocationManager.GPS_PROVIDER;


public class CompassActivity extends AppCompatActivity {

    private static final String TAG = "CompassActivity";

    private Location myLoc;
    private Location destLoc = new Location(GPS_PROVIDER);
    private float direction;

    private Gson trailGson = new Gson();
    Trail trail = new Trail();

    //String json = trailGson.fromJson(json, );

    private double distance;
    private double closeEnough = 1.5;
    private Compass compass;
    private ImageView arrowView;
    private TextView sotwLabel;  // SOTW is for "side of the world"
    private TextView locLabel;

    private float currentAzimuth;
    private float trailNorth;
    private SOTWFormatter sotwFormatter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        destLoc.setLatitude(40.596968);
        destLoc.setLongitude(-75.510654);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        sotwFormatter = new SOTWFormatter(this);

        arrowView = findViewById(R.id.main_image_hands);
        locLabel = findViewById(R.id.sotw_label2);
        locLabel.setText("nothing yet!");
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  }, 1);
        }
        locationManager.requestLocationUpdates(GPS_PROVIDER, 0, 3, locationListener);

        setupCompass();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "start compass");
        compass.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        compass.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        compass.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "stop compass");
        compass.stop();
    }

    private void setupCompass() {
        compass = new Compass(this);
        Compass.CompassListener cl = getCompassListener();
        compass.setListener(cl);
    }

    private void adjustArrow(float azimuth) {
        Log.d(TAG, "will set rotation from " + currentAzimuth + " to "
                + azimuth);

        Animation an = new RotateAnimation(-currentAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currentAzimuth = direction; //changed azimuth to trailNorth

        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);

        arrowView.startAnimation(an);
    }



    private Compass.CompassListener getCompassListener() {
        return new Compass.CompassListener() {
            @Override
            public void onNewAzimuth(final float azimuth) {
                if (myLoc == null){return;}
                trailNorth = azimuth;
                GeomagneticField geoField = new GeomagneticField( Double.valueOf(myLoc.getLatitude()).floatValue(),     // ignore this
                        Double.valueOf(myLoc.getLongitude()).floatValue(), Double.valueOf(myLoc.getAltitude()).floatValue(),
                        System.currentTimeMillis());
                trailNorth -= geoField.getDeclination(); //true north

                float bearTo = myLoc.bearingTo(destLoc);
                if (bearTo < 0){
                    bearTo = bearTo + 360;
                }
                direction = bearTo-trailNorth + 90; //put back as what it was
                if (direction < 0){
                    direction = direction + 360;
                }
                final float irlDirection = direction;

                // UI updates only in UI thread
                // https://stackoverflow.com/q/11140285/444966
                runOnUiThread(new Runnable() {
                    @Override
                    //trailNorth = myLoc.bearTo()
                    public void run() {
                        adjustArrow(irlDirection );//tried +azimuth
                    }
                });
            }
        };
    }

    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            //makeUseOfNewLocation(location);
            Log.i("loc:", "lat: " + location.getLatitude() + " long: " + location.getLongitude() + "alt: " + location.getAltitude() + "acc: " + location.getAccuracy());
            myLoc = location;
            distance = Math.hypot(myLoc.getLatitude() - destLoc.getLatitude(), myLoc.getLongitude() - destLoc.getLongitude()) ;
            locLabel.setText(""+distance);
            if (distance < closeEnough) {
                //locLabel.setText("you made it!");
            }
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
}