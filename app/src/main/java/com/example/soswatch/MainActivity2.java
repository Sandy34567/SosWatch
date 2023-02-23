package com.example.soswatch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.wear.ambient.AmbientModeSupport;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.soswatch.databinding.ActivityMain2Binding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity2 extends Activity{

    private TextView mTextView;
    private FusedLocationProviderClient fusedLocationProviderClient;
    String latitude ="",longitude="";
    Location mlocation;
    List<Address> addresses;
    String full_Address = "";
    String city_Name = "";
    private Button sendAlrtBtn;
    private ActivityMain2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();

        String token_id = intent.getStringExtra("token");

//        mTextView = binding.text;

        sendAlrtBtn = binding.sendAlert;
        // Request location updates
        getLastLocation();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
           addresses = geocoder.getFromLocation(mlocation.getLatitude(), mlocation.getLongitude(), 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (addresses!=null){
            full_Address = addresses.get(0).getAddressLine(0);
            city_Name =addresses.get(0).getLocality();
        }

        // Use getLastLocation to get the last known location

        sendAlrtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://app.arslaan.link/api/beachAlert";
//                String url = "http://localhost/misc_app/public/api/beachAlert";

                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity2.this);

                Map<String, String> params = new HashMap();
                params.put("token",token_id);
                if(mlocation!=null){
             params.put("latitude", String.valueOf(mlocation.getLatitude()));
             params.put("longitude", String.valueOf(mlocation.getLongitude()));
                }else{
                    params.put("latitude", "99.999");
                    params.put("longitude", "99.999");
                }

                JSONObject parameters = new JSONObject(params);

                RequestQueue req = Volley.newRequestQueue(MainActivity2.this);
                JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject obj = new JSONObject(String.valueOf(response));
                            String r_msg = obj.getString("msg");
                            Log.d("ars", response.toString());
                            Toast.makeText(MainActivity2.this, r_msg, Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }}, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }}) {};
                requestQueue.add(stringRequest);
                stringRequest.setShouldCache(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
    public void getFusedLocation(){
        try{
            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity2.this);
            mFusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity2.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location loc) {
                            if(loc!=null){
                                mlocation = loc;
                            }else {
                                requestNewLocationData();
                            }
                        }
                    })
                    .addOnFailureListener(MainActivity2.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG","FAILED FUSED LOCATION ");
                        }
                    });
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        com.google.android.gms.location.LocationRequest mLocationRequest = new com.google.android.gms.location.LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            mlocation = locationResult.getLastLocation();
           fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    };

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {
            // check if location is enabled
            if (isLocationEnabled(MainActivity2.this)) {
                getFusedLocation();
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION},1);
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

}