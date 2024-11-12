package lk.jiat.app.happycart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.jiat.app.happycart.Model.Invoice;
import lk.jiat.app.happycart.service.DirectionApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyLocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = MainActivity.class.getName();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 10;
    private GoogleMap map;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker marker_current,marker_pin;
    private Polyline polyline;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private String userId;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_location);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            // User is signed in
            userId = user.getUid();

            Bundle bundle = getIntent().getExtras();
            String getId = bundle.getString("getId");

            fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);

            SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
         updateLocation(getId);
                }
            });


            // Now 'userId' contains the unique identifier (UID) for the currently signed-in user
        } else {
            userId = null;
            // User is not signed in
            // Handle the situation accordingly
        }


    }

    private void updateLocation(String getId){
        double latitude = currentLocation.getLatitude();
        double longitude = currentLocation.getLongitude();

// Assuming your deep link format is like: "https://example.com/map?lat={latitude}&lng={longitude}"
        String deepLink = String.format("https://google.com/maps?lat=%f&lng=%f", latitude, longitude);
        // Create a map with the fields you want to update
        Map<String, Object> updates = new HashMap<>();
        updates.put("addressLocation", deepLink);
        firestore.collection("Users").document(userId).collection("invoice").document(getId)
                .update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MyLocationActivity.this, "Location Updated",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MyLocationActivity.this, OrderHistoryActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MyLocationActivity.this, "Something went wrong please Try again",Toast.LENGTH_LONG).show();
                    }
                });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        MapStyleOptions styleOptions = MapStyleOptions.loadRawResourceStyle(getApplicationContext(),R.raw.map_style);
        map.setMapStyle(styleOptions);

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng end) {
                if(marker_pin==null){
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(end);
                    marker_pin=map.addMarker(markerOptions);
                }else {
                    marker_pin.setPosition(end);
                }

                LatLng start = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
//                LatLng end = new LatLng(7.6706719,80.3447723);

                getDirection(start,end);
            }
        });

        if(checkPermissions()){
//            map.setMyLocationEnabled(true);
            getLastLocation();
        }else {
            requestPermissions(
                    new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    private boolean checkPermissions(){
        boolean permission = false;

        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            permission= true;
        }
        return permission;
    }


    @SuppressLint("MissionPermission")
    private void getLastLocation(){
        if(checkPermissions()){
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        currentLocation = location;
                        LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                        map.addMarker(new MarkerOptions().position(latLng).title("My Location"));
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,50));
                    }
                }
            });


        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==LOCATION_PERMISSION_REQUEST_CODE){
            if(grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else {
                Snackbar.make(findViewById(R.id.container),"Location permission denied", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Settings", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }).show();
            }
        }
    }
    public void getDirection(LatLng start,LatLng end){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/directions/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        DirectionApi directionApi =retrofit.create(DirectionApi.class);

        String origin = start.latitude+","+start.longitude;
        String destination = end.latitude+","+end.longitude;
        String key= "Enter your key here";

        Call<JsonObject> apiJson = directionApi.getJson(origin, destination, true, key);
        apiJson.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                System.out.println(response.body().toString());
//                Log.i(TAG,response.body().toString());

                JsonObject body = response.body();
                JsonArray routes = body.getAsJsonArray("routes");

                JsonObject route = routes.get(0).getAsJsonObject();
                JsonObject overviewPolyline = route.getAsJsonObject("overview_polyline");

                List<LatLng> points = PolyUtil.decode(overviewPolyline.get("points").getAsString());

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(polyline== null){
                            PolylineOptions polylineOptions = new PolylineOptions();
                            polylineOptions.width(20);
                            polylineOptions.color(getColor(android.R.color.holo_blue_dark));
                            polylineOptions.addAll(points);
                            map.addPolyline(polylineOptions);
                        }else {
                            polyline.setPoints(points);
                        }

                    }
                });
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }


}
