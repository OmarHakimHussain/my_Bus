package com.example.mybus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import butterknife.ButterKnife;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.example.mybus.Common.common;
import com.example.mybus.Model.CustomerLocation;
import com.example.mybus.Model.User;
import com.example.mybus.Remote.IGoogleApI;
import com.example.mybus.Remote.RetrofitClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LocationOfCustomer extends FragmentActivity implements OnMapReadyCallback, ValueEventListener {

    private GoogleMap mMap;
    private Marker shipperMarker;
    private PolylineOptions polylineOptions , blackPolylineOptions ;
    private List<LatLng> polylineList ;
    private Polyline yellowPolyline, grayPolyline,blackPolyline;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private IGoogleApI iGoogleAPI ;
    private CompositeDisposable compositeDisposable = new CompositeDisposable() ;

    private DatabaseReference shipperRef;

    private boolean isInit = false;
    private Location previousLocation = null;

    FirebaseAuth mAuth;
    String userID;

    //Move Marker
    private Handler handler;
    private int index,next;
    private LatLng start,end;
    private float v;
    private double lat,lng;

    PlacesClient placesClient;
    DatabaseReference reference;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_of_customer);

        iGoogleAPI = RetrofitClient.getInstance().create(IGoogleApI.class);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync( this);


        ButterKnife.bind(this);
        buildLocationRequest();
       buildLocationCallBack();


       Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map);
                        mapFragment.getMapAsync(LocationOfCustomer.this::onMapReady);

                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(LocationOfCustomer.this);
                        if (ActivityCompat.checkSelfPermission(LocationOfCustomer.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationOfCustomer.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(LocationOfCustomer.this, "You must enabled this location permission", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();

      //  drawRoutes();


        subscribeShipperMove();

    }

    private void subscribeShipperMove() {

        mAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        shipperRef = FirebaseDatabase.getInstance()
                .getReference("currentLocation")
                .child("WWPgImyiowaJeOQrVmsKwhdHl4M2");

        shipperRef.addValueEventListener(this);
    }


    private void buildLocationCallBack() {
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);



                // Add a marker in Sydney and move the camera
                LatLng locationShipper = new LatLng(locationResult.getLastLocation().getLatitude(),
                        locationResult.getLastLocation().getLongitude());

                updateLocation(locationResult.getLastLocation());

                if(shipperMarker == null)
                {
                    // inflate drawable
                    int height,width;
                    height = width = 80;
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) ContextCompat
                            .getDrawable(LocationOfCustomer.this , R.drawable.box); // Change icon
                    Bitmap resized = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(),width,height,false);

                    shipperMarker =   mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(resized))
                            .position(locationShipper).title("You"));


                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationShipper,18));


                }






            }
        };
    }
    private void buildLocationRequest() {
        locationRequest =  new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(15000); // 15 sec
        locationRequest.setFastestInterval(10000); // 10 sec
        locationRequest.setSmallestDisplacement(20f); //20 meters
    }
    // mohmaaaaa
    private void updateLocation(Location lastLocation) {
        CustomerLocation customerLocation = new CustomerLocation(lastLocation.getLatitude(), lastLocation.getLongitude());


        mAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(userID);


        FirebaseDatabase.getInstance()
                .getReference("currentLocation")
                .child(userID)
                .setValue(customerLocation)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(LocationOfCustomer.this, "DONE", Toast.LENGTH_SHORT).show();
                    }
                });

    }






    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {

        reference =  FirebaseDatabase.getInstance().getReference("currentLocation");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CustomerLocation customerLocation = snapshot.getValue(CustomerLocation.class);
                final LatLng location2;
                location2 = new LatLng((customerLocation).getCurrentLatCustomer() , customerLocation.getCurrentLngCustomer());

                String from = new StringBuilder()
                        .append(location2.latitude)
                        .append(",")
                        .append(location2.longitude)
                        .toString();

                //Update Position
                common.customerLocation = snapshot.getValue(CustomerLocation.class);


                String to = new StringBuilder()
                        .append(location2.latitude)
                        .append(",")
                        .append(location2.longitude)
                        .toString();

                if(snapshot.exists())
                {
                    if(isInit)
                        moveMarkerAnimation(shipperMarker,from,to);
                    else
                        isInit = true  ;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });







        //Save new Position





    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {
        Toast.makeText(this, "Error 1 ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);


        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,
                    R.raw.uber_light_with_label));
            if(!success)
                Log.e("EDMTDEV" ,"Style parsing failed");
        }catch (Resources.NotFoundException ex)
        {
            Log.e("EDMTDEV" ,"Resource not found");
        }

        drawRoutes();

     //
    }

    private void drawRoutes() {

        /*
         reference =  FirebaseDatabase.getInstance().getReference("currentLocation");

       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {


               CustomerLocation customerLocation = snapshot.getValue(CustomerLocation.class);
               final LatLng location2;
               location2 = new LatLng(requireNonNull(customerLocation).getCurrentLatCustomer() , customerLocation.getCurrentLngCustomer());
         */

       reference = FirebaseDatabase.getInstance().getReference("currentLocation");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CustomerLocation customerLocation = snapshot.getValue(CustomerLocation.class);
                User user = snapshot.getValue(User.class);
                final LatLng location2;
                location2 = new LatLng (customerLocation.getCurrentLatCustomer() , customerLocation.getCurrentLngCustomer());
                final LatLng  location = new LatLng(user.getCurrentLat() , user.getCurrentLng());
                LatLng locationOrder = new LatLng(location2.latitude
                        , location2.longitude);
                LatLng locationShipper = new LatLng(location.latitude,
                        location.longitude);
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.box))
                        .position(locationOrder));

                if (shipperMarker == null) {
                    int height, width;
                    height = width = 80;
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) ContextCompat
                            .getDrawable(LocationOfCustomer.this, R.drawable.shippernew);
                    Bitmap resized = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), width, height, false);
                    shipperMarker = mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(resized))
                            .position(locationShipper));

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationShipper, 18));
                } else {
                    shipperMarker.setPosition(locationShipper);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationShipper, 18));

                }


                String from = new StringBuilder()
                        .append(location2.latitude)
                        .append(",")
                        .append(location2.longitude)
                        .toString();
                String to = new StringBuilder()
                        .append(location.latitude)
                        .append(",")
                        .append(location.longitude)
                        .toString();

                compositeDisposable.add(iGoogleAPI.getDirections("driving",
                        "less_driving",
                        from, to,
                        getString(R.string.google_maps_key))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    JSONArray jsonArray = jsonObject.getJSONArray("routes");

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject route = jsonArray.getJSONObject(i);
                                        JSONObject poly = route.getJSONObject("overview_polyline");
                                        String polyline = poly.getString("points");
                                        polylineList = common.decodePoly(polyline);
                                    }

                                    polylineOptions = new PolylineOptions();
                                    polylineOptions.color(Color.RED);
                                    polylineOptions.width(12);
                                    polylineOptions.startCap(new SquareCap());
                                    polylineOptions.jointType(JointType.ROUND);
                                    polylineOptions.addAll(polylineList);

                                    yellowPolyline = mMap.addPolyline(polylineOptions);
                                }
                                catch (Exception e) {
                                    Toast.makeText(LocationOfCustomer.this, "ERROR DRAW ROUT " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(LocationOfCustomer.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        //Add Box


        //Add Shipper

        // Draw Routes





    }


    private void moveMarkerAnimation(Marker shipperMarker, String from, String to) {
        compositeDisposable.add(iGoogleAPI.getDirections("driving" ,
                "less_driving" ,
                from , to ,
                getString(R.string.google_maps_key))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(returnResult -> {


                    try {
                        JSONObject jsonObject = new JSONObject(returnResult);
                        JSONArray jsonArray = jsonObject.getJSONArray("routes");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject route = jsonArray.getJSONObject(i);
                            JSONObject poly = route.getJSONObject("overview_polyline");
                            String polyline = poly.getString("points");
                            polylineList = common.decodePoly(polyline);
                        }

                        polylineOptions = new PolylineOptions();
                        polylineOptions.color(Color.GRAY);
                        polylineOptions.width(12);
                        polylineOptions.startCap(new SquareCap());
                        polylineOptions.jointType(JointType.ROUND);
                        polylineOptions.addAll(polylineList);

                        grayPolyline = mMap.addPolyline(polylineOptions);


                        blackPolylineOptions = new PolylineOptions();
                        blackPolylineOptions.color(Color.BLACK);
                        blackPolylineOptions.width(5);
                        blackPolylineOptions.startCap(new SquareCap());
                        blackPolylineOptions.jointType(JointType.ROUND);
                        blackPolylineOptions.addAll(polylineList);

                        blackPolyline = mMap.addPolyline(blackPolylineOptions);

                        //Animators
                        ValueAnimator polylineAnimator = ValueAnimator.ofInt(0,100);
                        polylineAnimator.setDuration(2000);
                        polylineAnimator.setInterpolator(new LinearInterpolator());
                        polylineAnimator.addUpdateListener(valueAnimator ->{
                            List<LatLng> points = grayPolyline.getPoints();
                            int percentValue = (int) valueAnimator.getAnimatedValue();
                            int size = points.size();
                            int newPoints = (int) (size*(percentValue/100.0f));
                            List<LatLng> p = points.subList(0,newPoints);
                            blackPolyline.setPoints(p);
                        });
                        polylineAnimator.start();

                        //Bike Moving
                        handler = new Handler();
                        index = -1;
                        next = 1;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if(index < polylineList.size()-1)
                                {
                                    index++;
                                    next = index+1;
                                    start = polylineList.get(index);
                                    end = polylineList.get(next);
                                }

                                ValueAnimator valueAnimator = ValueAnimator.ofInt(0,1);
                                valueAnimator.setDuration(1500);
                                // shipperMarker
                                // mMap.moveCamera(CameraUpdateFactory.newLatLng(newPos));
                                valueAnimator.setInterpolator(new LinearInterpolator());
                                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        v = animation.getAnimatedFraction();
                                        lng = v*end.longitude+(1-v)
                                                *start.longitude;
                                        lat = v*end.latitude+(1-v)
                                                *start.latitude;
                                        LatLng newPos =new LatLng(lat,lng);
                                        shipperMarker.setPosition(newPos);
                                        shipperMarker.setAnchor(0.5f,0.5f);
                                        shipperMarker.setRotation(common.getBearing(start,newPos));

                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(newPos)); // Move Camera
                                    }
                                });
                                valueAnimator.start();
                                if(index < polylineList.size() -2) // Reach destination
                                    handler.postDelayed(this,1500);

                            }
                        },1500);

                    }
                    catch (Exception e) {
                        Toast.makeText(LocationOfCustomer.this, "ERROR MOVE ANIMITIONS " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    Toast.makeText(this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }
}