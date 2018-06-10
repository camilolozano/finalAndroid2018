package com.lozano.camilo.shop.start_content;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lozano.camilo.shop.GlobalClassMet;
import com.lozano.camilo.shop.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class Map extends AppCompatActivity implements GoogleMap.OnMapClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    GoogleMap googleMap;
    private Location lastLocation;
    double lat, lon;
    SupportMapFragment mapFragment;
    LatLng Position;
    EditText editAddres;
    JSONObject jsonObject = new JSONObject();
    ProgressDialog dialog;
    Marker marker;

    SharedPreferences prefsUser;
    SharedPreferences.Editor edit;
    String localhost;
    AlertDialog alertGps = null;

    LocationManager locationManager;
    GlobalClassMet gbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        editAddres = (EditText) findViewById(R.id.editAddres);

        prefsUser = getSharedPreferences("UserData", this.MODE_PRIVATE);
        localhost = (prefsUser.getString("localhost", getResources().getString(R.string.localhost)));


        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();
        }


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapProduct);
        mapFragment.getMapAsync(this);


        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListenerGPS);

        }

        gbl = new GlobalClassMet(this);


    }

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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        }
        if (lastLocation != null) {
            Position = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Position, 17));

        } else {
            Position = new LatLng(1.2136100, -77.2811100);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Position, 14));


        }
        marker = googleMap.addMarker(new MarkerOptions()
                .position(Position)
                .title("position").icon(BitmapDescriptorFactory.fromResource(R.drawable.icolocation)));

        CaptureAddress(Position);

    }

    private void CaptureAddress(LatLng latLng) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> list = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (!list.isEmpty()) {
                Address address = list.get(0);
                editAddres.setText(address.getAddressLine(0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMapClickListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }

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


    public void SaveRegister(View view) {
        try {
            jsonObject.put("firstNameUser", getIntent().getStringExtra("firstNameUser").trim())
                    .put("lastNameUser", getIntent().getStringExtra("lastNameUser").trim())
                    .put("addressUser", editAddres.getText().toString().trim())
                    .put("contactUser", getIntent().getStringExtra("contactUser").trim())
                    .put("emailUsername", getIntent().getStringExtra("emailUsername").trim())
                    .put("password", getIntent().getStringExtra("password"));

            Registervolley();

        } catch (JSONException e) {
            gbl.ProblemApp(this);
        }
    }


    private void Registervolley() {
        if (gbl.ConectionValidate()) {
            dialog = new ProgressDialog(this);
            dialog.setMessage("Loading..");
            dialog.show();
            String urlInsert = "http://" + localhost + ":8000/create-user-app/create";
            final RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, urlInsert, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    edit = prefsUser.edit();
                    edit.putString("type_login", "bigapp");
                    edit.putString("email", getIntent().getStringExtra("emailUsername").trim().toLowerCase());
                    edit.putString("name", getIntent().getStringExtra("firstNameUser") + " " + getIntent().getStringExtra("lastNameUser").trim());
                    edit.putString("address", editAddres.getText().toString().trim());
                    edit.commit();

                    ReloadLogin();

                    if (dialog.isShowing()) dialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    gbl.ToastProblem(Map.this);
                    if (dialog.isShowing()) dialog.dismiss();


                }
            });
            queue.add(request);
        } else {
            gbl.ToastInternetNot(Map.this);
        }


    }

    private void ReloadLogin() {
        Intent pIntent = new Intent().setClass(this, Login.class);
        pIntent.putExtra("MsgFirstRegistres", "MsgFirstRegistres");
        pIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(pIntent);
        googleApiClient.disconnect();
        finish();

    }


    @Override
    protected void onRestart() {
        marker.remove();
        super.onRestart();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        CaptureAddress(latLng);
        marker.remove();
        marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("Direccion").icon(BitmapDescriptorFactory.fromResource(R.drawable.icolocation)));
    }

    LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String msg = "New Latitude: " + latitude + "New Longitude: " + longitude;
            System.err.println("msg   " + msg);

        }


        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            System.err.println("location status" + i + "  s:" + s);
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


}
