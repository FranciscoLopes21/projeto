package com.example.crowdzero_android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Date;
import java.util.Calendar;

public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private GoogleMap gmap;
    private LocationRequest locationRequest;
    private Session session;
    private double lat, lon;
    private int idlR;
    private int nivel;
    private boolean feedback;
    private String idF;
    LatLng loc;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationManager lmanager;
    LocationListener llistener;

    private static final String MAP_VIEW_BUNDLE_KEY = "AIzaSyA9tbVF3v7p0wYLhoRkcYdKuTLr5KREXr4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        session = new Session(this);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        findViewById(R.id.btnMenu2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnMenu2:
                        startActivity(new Intent(MapaActivity.this, MainActivity.class));
                        finish();
                        break;
                }
            }
        });
        findViewById(R.id.btnPop1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnPop1:
                        nivel = 1;
                        break;
                }
            }
        });
        findViewById(R.id.btnPop2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnPop2:
                        nivel = 2;
                        break;
                }
            }
        });
        findViewById(R.id.btnPop3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnPop3:
                        nivel = 3;
                        break;
                }
            }
        });
        findViewById(R.id.btnSubmeter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnSubmeter:
                        verifReport();
                        break;
                }
            }
        });

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MapaActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {
                    LocationServices.getFusedLocationProviderClient(MapaActivity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    if (locationResult != null && locationResult.getLocations().size() > 0) {
                                        int index = locationResult.getLocations().size() - 1;
                                        lat = locationResult.getLocations().get(index).getLatitude();
                                        lon = locationResult.getLocations().get(index).getLongitude();
                                    }
                                }
                            }, Looper.getMainLooper());
                } else {
                    Toast.makeText(MapaActivity.this, "Para utilizar esta funcionalidade tem de ter o GPS ativo", Toast.LENGTH_SHORT).show();
                }
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    /* @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    //();

                } else {

                    turnOnGPS();
                }
            }
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                //getCurrentLocation();
            }
        }
    }

    /*private boolean gpsen() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }*/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        RequestQueue queue = Volley.newRequestQueue(this);
        String url1 = "https://crowdzeromapi.herokuapp.com/locals";
        String url2 = "https://crowdzeromapi.herokuapp.com/reports";

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gmap.setMyLocationEnabled(true);
            //getCurrentLocation();

            StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url1, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("message", response);

                    try {
                        JSONObject newData = new JSONObject(response);
                        JSONArray dataArray = newData.getJSONArray("locals");
                        for (int i = 0; i < dataArray.length(); i++) {
                            int idl = (Integer) dataArray.getJSONObject(i).get("idl");
                            String nome = (String) dataArray.getJSONObject(i).get("nome");
                            String lat = (String) dataArray.getJSONObject(i).get("latitude");
                            String lon = (String) dataArray.getJSONObject(i).get("longitude");
                            LatLng local = new LatLng(Double.valueOf(lat), Double.valueOf(lon));
                            Log.d("idlMESSAGE", Integer.toString(idl));


                            gmap.addMarker(new MarkerOptions().position(local).title(nome).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                            Circle circle = gmap.addCircle(new CircleOptions()
                                    .center(local)
                                    .radius(200)
                                    .strokeColor(Color.BLUE));
                        }

                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapaActivity.this);
                        if (ActivityCompat.checkSelfPermission(MapaActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                                @Override
                                public void onComplete(@NonNull Task<Location> task) {
                                    Location location = task.getResult();
                                    Log.d("latitude", Double.toString(location.getLatitude()));
                                    Log.d("longitude", Double.toString(location.getLongitude()));

                                    loc = new LatLng(location.getLatitude(),location.getLongitude());
                                }
                            });
                        } else {
                            ActivityCompat.requestPermissions(MapaActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("fail", error.toString());
                }
            });
            queue.add(stringRequest1);

            StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("message", response);

                    try {
                        JSONObject newData = new JSONObject(response);
                        JSONArray dataArray = newData.getJSONArray("reports");
                        for (int i = 0; i < dataArray.length(); i++) {
                            int idr = (Integer) dataArray.getJSONObject(i).get("idr");
                            String lat = (String) dataArray.getJSONObject(i).get("latr");
                            String lon = (String) dataArray.getJSONObject(i).get("longr");
                            int n = (Integer) dataArray.getJSONObject(i).get("nivel");
                            //Log.d("idrMESSAGE", Integer.toString(idr));

                            LatLng loc = new LatLng(Double.valueOf(lat),Double.valueOf(lon));

                            if (n == 1){
                                gmap.addMarker(new MarkerOptions().position(loc).snippet(String.valueOf(idr)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            } else if(n == 2){
                                gmap.addMarker(new MarkerOptions().position(loc).snippet(String.valueOf(idr)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                            } else if (n == 3) {
                                gmap.addMarker(new MarkerOptions().position(loc).snippet(String.valueOf(idr)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("fail", error.toString());
                }
            });
            queue.add(stringRequest2);
        } else {
            finish();
        }

        // adding on click listener to marker of google maps.
        gmap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                idF = marker.getSnippet();

                if (marker.getTitle() == null)
                {
                    Toast.makeText(MapaActivity.this, session.getId().toString() + ";" + idF.toString(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), SendFeedbackActivity.class);
                    intent.putExtra("message", idF);

                    startActivity(intent);
                }
                return false;
            }
        });
    }

    public void verifReport(){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://crowdzeromapi.herokuapp.com/locals";
        //getCurrentLocation();

        Location startPoint = new Location("locationA");
        startPoint.setLatitude(loc.latitude);
        startPoint.setLongitude(loc.longitude);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("message", response);

                try {
                    JSONObject newData = new JSONObject(response);
                    JSONArray dataArray = newData.getJSONArray("locals");
                    for (int i = 0; i < dataArray.length(); i++) {
                        int idl = (Integer) dataArray.getJSONObject(i).get("idl");
                        String nome = (String) dataArray.getJSONObject(i).get("nome");
                        String lat = (String) dataArray.getJSONObject(i).get("latitude");
                        String lon = (String) dataArray.getJSONObject(i).get("longitude");
                        Log.d("idlMESSAGE", Integer.toString(idl));

                        Location endPoint = new Location("locationB");
                        endPoint.setLatitude(Double.valueOf(lat));
                        endPoint.setLongitude(Double.valueOf(lon));

                        double distance = startPoint.distanceTo(endPoint);

                        if (distance <= 200) {
                            idlR = idl;
                            break;
                        } else {
                            idlR = 0;
                        }
                    }
                    Log.d("idlR", Double.toString(idlR));
                    if (idlR > 0){
                        Log.d("idlR", Double.toString(idlR));
                        report();
                    }
                    else{
                        Toast.makeText(MapaActivity.this, "Não se encontra perto de um ponto", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("fail", error.toString());
            }
        });
        queue.add(stringRequest);
    }

    public void report() {
        RequestQueue queueRepo = Volley.newRequestQueue(this);
        String urlRepo ="https://crowdzeromapi.herokuapp.com/reportPost";

            StringRequest sr = new StringRequest(Request.Method.POST, urlRepo,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                if (!response.equals("[]")){
                                    String[] sep = response.split(":");
                                }
                            }catch(Error error) {
                                Toast.makeText(MapaActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("HttpClient", "error: " + error.toString());
                }
            })
            {
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("idu", String.valueOf(session.getId()));
                    params.put("idl", String.valueOf(idlR));
                    params.put("latitude", String.valueOf(loc.latitude));
                    params.put("longitude", String.valueOf(loc.longitude));
                    params.put("nivel", String.valueOf(nivel));
                    params.put("data", String.valueOf(LocalDateTime.now()));
                    return params;
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("Content-Type","application/x-www-form-urlencoded");
                    return params;
                }
            };
            sr.setRetryPolicy(new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queueRepo.add(sr);
        Toast.makeText(MapaActivity.this, "Obrigado pelo seu report", Toast.LENGTH_SHORT).show();
    }

    /*private void getCurrentLocation(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(MapaActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                if(isGPSEnabled()){
                    LocationServices.getFusedLocationProviderClient(MapaActivity.this).requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult){
                            super.onLocationResult(locationResult);

                            LocationServices.getFusedLocationProviderClient(MapaActivity.this).removeLocationUpdates(this);

                            if(locationResult != null && locationResult.getLocations().size() > 0){

                                int index = locationResult.getLocations().size() - 1;
                                double latitude = locationResult.getLocations().get(index).getLatitude();
                                double longitude = locationResult.getLocations().get(index).getLongitude();

                                Log.d("Latitude", Double.toString(latitude));
                                Log.d("Longitude", Double.toString(longitude));
                            }
                        }
                    }, Looper.getMainLooper());
                } else {
                    turnOnGPS();
                }
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }*/

    private void turnOnGPS() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext()).checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try{
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(MapaActivity.this, "GPS ta a andar", Toast.LENGTH_SHORT).show();

                }catch(ApiException e){
                    switch(e.getStatusCode()){
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try{
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(MapaActivity.this, 2);
                            }catch(IntentSender.SendIntentException ex){
                                ex.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Log.d("ITS OVER...", "Bros...");
                            break;
                    }
                }
            }
        });
    }

    private boolean isGPSEnabled(){
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if(locationManager == null){
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }
}