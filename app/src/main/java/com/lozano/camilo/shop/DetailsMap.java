package com.lozano.camilo.shop;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.PolylineOptions;


import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DetailsMap extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    LatLng Position;
    double lat, lon;
    private static String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION};
    private GoogleApiClient googleApiClient;
    GoogleMap googleMap;
    private Location lastLocation;
    SupportMapFragment mapFragment;
    LocationManager locationManager;
    Marker marker;
    AlertDialog alertGps = null;
    String nameCompany, txtPointA, txtPointB;
    GlobalClassMet gbl;
    Button DistancePoinst;
    DecimalFormat df = new DecimalFormat("#.00");
    int camara = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        gbl = new GlobalClassMet(this);

        lat = Double.parseDouble(getIntent().getStringExtra("lat"));
        lon = Double.parseDouble(getIntent().getStringExtra("lon"));
        DistancePoinst = (Button) findViewById(R.id.DistancePoinst);


        txtPointB = CaptureAddress(new LatLng(lat, lon));
        nameCompany = getIntent().getStringExtra("nameCompany");

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();
        }


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapCompany);
        mapFragment.getMapAsync(this);


        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListenerGPS);

        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }


    }


    public String CaptureAddress(LatLng latLng) {
        Address address = null;
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> list = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (!list.isEmpty()) {
                address = list.get(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address.getAddressLine(0);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        }
        if (lastLocation != null) {
            Position = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());


        } else {
            Position = new LatLng(1.2136100, -77.2811100);


        }
        txtPointA = CaptureAddress(Position);
        txtPointB = CaptureAddress(new LatLng(lat, lon));

        marker = googleMap.addMarker(new MarkerOptions()
                .position(Position)
                .title(txtPointA));

        googleMap.addMarker(new MarkerOptions().icon((BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                .position(new LatLng(lat, lon))
                .title(nameCompany));

        googleMap.addPolyline(new PolylineOptions()
                .add(new LatLng(Position.latitude, Position.longitude), new LatLng(lat, lon))
                .width(8)
                .color(Color.BLACK));

        double distance = gbl.distanciaCoord(Position.latitude, Position.longitude, lat, lon);
        distance = Double.parseDouble(String.format("%.2f", distance));

        if (distance < 3) {
            camara = 13;
        } else if (distance < 7) {
            camara = 11;
        } else if (distance < 16) {
            camara = 9;
        } else {
            camara = 7;
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Position, camara));
        DistancePoinst.setText("Distancia proxima " + distance + " Km");



    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String msg = "New Latitude: " + latitude + "New Longitude: " + longitude;

        }


        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            //  System.err.println("location status" + i + "  s:" + s);
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                AlertNoGps();
            }

        }
    };

    private void AlertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Su GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        alertGps = builder.create();
        alertGps.show();
    }



}