package com.amycohen.lab38maptaskapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private static final int LOCATION_REFRESH_TIME = 1;
    private static final int LOCATION_REFRESH_DISTANCE = 1;
    private static final int REQUEST_PERMISSION_GRANT = 1;
    private static final String TAG = "";
    private GoogleMap mMap;
    private LocationManager locationManager;

    //from lecture demo
    private LatLng mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ButterKnife.bind(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final Intent data = getIntent();

        FirebaseDatabase.getInstance().getReference("errands")
                .child(data.getStringExtra("id")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Errand errand = Errand.fromSnapshot(dataSnapshot);
                mMap.addMarker(new MarkerOptions().position(errand.start).title("start"));
                mMap.addMarker(new MarkerOptions().position(errand.end).title("end"));

                double centerLat = (errand.start.latitude + errand.end.latitude) / 2;
                double centerLng = (errand.start.longitude + errand.end.longitude) / 2;

                mMap.moveCamera(CameraUpdateFactory.zoomTo(8));

                LatLng center = new LatLng(centerLat, centerLng);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(center));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initializeLocationListener();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                    ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            }, REQUEST_PERMISSION_GRANT );
        }
    }

    @SuppressLint("MissingPermission")
    private void initializeLocationListener() {
        //found on stackoverflow via class lecture video:
        // https://stackoverflow.com/questions/17591147/how-to-get-current-location-in-android
        LocationListener listener = this;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, listener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_GRANT && grantResults[0] == RESULT_OK && grantResults[1] == RESULT_OK) {
            initializeLocationListener();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mCurrentLocation = latLng;
    }

    @OnClick(R.id.goToMyLocation)
    public void goToMyLocation () {
        if (mCurrentLocation != null) {
            mMap.addMarker(new MarkerOptions().position(mCurrentLocation).title("Here you are"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mCurrentLocation));

        }
    }

    @OnClick(R.id.goToErrandList)
    public void goToMyErrandList() {
        Intent intent = new Intent(this, ErrandListActivity.class);
        startActivity(intent);
    }

    //I think these are working. They seem to be, but I don't know how to check it on the emulator
    @OnClick(R.id.gpsOff)
    public void turnGpsOff() {
        //Disable GPS
        onRequestPermissionsResult(0, new String[]{"off"}, new int[]{0, 0});
        Toast.makeText(getApplicationContext(), "GPS is off", Toast.LENGTH_LONG).show();
        Log.d("GPS", "gps turned off");
    }

    @OnClick(R.id.gpsOn)
    public void turnGpsOn() {
        //Enable GPS
        onRequestPermissionsResult(REQUEST_PERMISSION_GRANT, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION},
                new int[]{RESULT_OK});
        Toast.makeText(getApplicationContext(), "GPS is on", Toast.LENGTH_LONG).show();
        Log.d("GPS", "gps turned on");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("GPS", "gps turned on");
    }

    //not required, but good idea to have a toast pop up letting the user know that this app requires location data to be useful.
    @Override
    public void onProviderDisabled(String provider) {
        Log.d("GPS", "gps turned off");
    }
}
